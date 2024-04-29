/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

// imports from same project
import com.mycompany.mavenproject1.Exceptions.InvalidDatabaseSchemaException;
import com.mycompany.mavenproject1.Exceptions.UserCancelledFileChooserException;
import com.mycompany.mavenproject1.Managers.DatabaseManager;

// imports from javafx
import javafx.fxml.Initializable;

// other imports
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.stage.Stage;

/**
 *
 * @author AHMAD
 */
public class SettingsController implements Initializable {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    private static Stage stage = null;
    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
        try {
            DatabaseManager.getManager().setStage(stage);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    public void BackupDatabase() {
        try {
            DatabaseManager.getManager().Backup();
        } catch (UserCancelledFileChooserException ex) {
            // do nothing
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    public void CleanDatabase() {
        try {
            DatabaseManager.getManager().DeleteAllData();
        } catch (IOException | SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    public void RestoreDatabase() {
        try {
            DatabaseManager.getManager().Restore();
        } catch (UserCancelledFileChooserException ex) {
            // do nothing
        } catch (InvalidDatabaseSchemaException | IOException | SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
}
