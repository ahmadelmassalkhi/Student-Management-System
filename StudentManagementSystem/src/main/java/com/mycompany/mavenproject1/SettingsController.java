/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

// imports from javafx
import com.mycompany.mavenproject1.Exceptions.InvalidDatabaseSchemaException;
import com.mycompany.mavenproject1.Managers.DatabaseManager;
import com.mycompany.mavenproject1.models.StudentsModel;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.fxml.Initializable;

// other imports
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 *
 * @author AHMAD
 */
public class SettingsController implements Initializable {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public void BackupDatabase() {
        try {
            DatabaseManager.getManager().Backup();
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
        } catch (InvalidDatabaseSchemaException | IOException | URISyntaxException | SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
}
