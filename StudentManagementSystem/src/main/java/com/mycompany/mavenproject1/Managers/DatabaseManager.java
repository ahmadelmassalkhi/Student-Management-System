/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Managers;

import com.mycompany.mavenproject1.Exceptions.InvalidDatabaseSchemaException;
import com.mycompany.mavenproject1.models.StudentsModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author AHMAD
 */
public class DatabaseManager {
    
    private DatabaseManager() {}
    private static DatabaseManager DbManager = null;
    public static DatabaseManager getManager() throws IOException {
        if(DbManager == null) DbManager = new DatabaseManager();
        return DbManager;
    }

    private Stage stage = null;
    public void setStage(Stage stage) { this.stage = stage; }
    
    /*******************************************************************/
    // CLEAN DATABASE
    
    public void DeleteAllData() throws SQLException, IOException {
        // delete data from all tables
        StudentsModel.getModel().deleteAllStudents();
    }
    
    /*******************************************************************/
    // BACKUP CURRENT DATABASE

    // creates a copy of the current database, and saves at user-choice's location
    public void Backup() throws IOException {
        try {
            // Perform the backup by copying the original .db file to the selected location
            Path source = ConfigurationManager.getCurrentDatabasePath();
            Path destination = this.chooseSaveLocation().toPath();
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch(NullPointerException ex) {
            // user cancelled in FileChooser
        }
    }
    
    private File chooseSaveLocation() {
        // Create a file chooser
        FileChooser fileChooser = new FileChooser();
        
        // set initial directory
        fileChooser.setInitialDirectory(ConfigurationManager.getDatabasesDirPath().toFile());

        // Add a filter to only show .db files
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter("Database Files (*.db)", "*.db");
        fileChooser.getExtensionFilters().add(dbFilter);

        // Show the file chooser dialog
        return fileChooser.showSaveDialog(stage);
    }
    
    /*******************************************************************/
    
    // allows user to choose a new .db file, and validates it for schema-requirements
    public void Restore() throws IOException, URISyntaxException, SQLException, InvalidDatabaseSchemaException {
        try {
            File restoredDbFile = this.chooseBackupDatabase();
            ConfigurationManager.updateDbConfigFile(restoredDbFile);

            // validate new database compatibility with our database schema
            ValidateSchema(restoredDbFile.toPath());
            
            // update connection
            DatabaseConnectionManager.getManager().disconnect();
            DatabaseConnectionManager.getManager().connect();
        } catch(FileNotFoundException ex) {
            // user cancelled in FileChooser
        }
    }
    
    private File chooseBackupDatabase() {
        // Create a file chooser
        FileChooser fileChooser = new FileChooser();

        // set initial directory
        fileChooser.setInitialDirectory(ConfigurationManager.getDatabasesDirPath().toFile());

        // Add a filter to only show .db files
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter("Database Files (*.db)", "*.db");
        fileChooser.getExtensionFilters().add(dbFilter);

        // Show the file chooser dialog
        return fileChooser.showOpenDialog(null);
        /*
         * we use null instead of stage because
         * we do not need to prevent interaction with the application during this time
         * because any interaction will be useless (we are changing the database)
         */
    }
    
    public void ValidateSchema(Path dbFilePath) throws IOException, SQLException {
        Connection newConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        Connection curConnection = DatabaseConnectionManager.getManager().connect();
        if(DatabaseConnectionManager.compareDatabaseSchemas(newConnection, curConnection) == false) throw new InvalidDatabaseSchemaException();
    }
    
    /*******************************************************************/
}
