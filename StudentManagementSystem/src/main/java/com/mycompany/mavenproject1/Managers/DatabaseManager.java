/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Managers;

import com.mycompany.mavenproject1.Exceptions.InvalidDatabaseSchemaException;
import com.mycompany.mavenproject1.Exceptions.UserCancelledFileChooserException;
import com.mycompany.mavenproject1.models.StudentsModel;
import com.mycompany.mavenproject1.models.SubscriptionsModel;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
        StudentsModel.getModel().Delete();
    }
    
    /*******************************************************************/
    // BACKUP CURRENT DATABASE

    // creates a copy of the current database, and saves at user-choice's location
    public void Backup() throws IOException, UserCancelledFileChooserException {
        // copy the original .db file to the selected location (of user's choice)
        Path source = ConfigurationManager.getManager().getCurrentDatabasePath(); // exists because we are using it (sqlite would have created it)
        Path destination = FileManager.chooseSavePathOf_Db(stage); // throws UserCancelledFileChooserException
        FileManager.copyFile(source, destination);
    }
    
    /*******************************************************************/
    
    // allows user to choose a new .db file, and validates it for schema-requirements
    public void Restore() throws IOException, SQLException, InvalidDatabaseSchemaException, UserCancelledFileChooserException {
        Path restoredDbPath = FileManager.chooseOpenPathOf_Db(stage);
        ConfigurationManager.getManager().updateDbConfigFile(restoredDbPath);

        // validate new database compatibility with our database schema
        this.ValidateSchema(restoredDbPath);

        // update connection
        StudentsModel.getModel().reconnect();
        SubscriptionsModel.getModel().reconnect();
    }
    
    public void ValidateSchema(Path dbFilePath) throws IOException, SQLException, InvalidDatabaseSchemaException {
        Connection newConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        Connection curConnection = DatabaseConnectionManager.getManager().connect();
        if(DatabaseConnectionManager.compareDatabaseSchemas(newConnection, curConnection) == false) throw new InvalidDatabaseSchemaException();
    }
    
    /*******************************************************************/
    
    public String getCurrentDatabaseName() throws IOException {
        return ConfigurationManager.getManager().getCurrentDatabasePath().toString();
    }
}
