/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

// imports from javafx
import com.mycompany.mavenproject1.models.StudentsModel;
import javafx.fxml.Initializable;

// other imports
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AHMAD
 */
public class SettingsController implements Initializable {
    
    private StudentsModel model;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // initialize model
        try {
            model = StudentsModel.getModel();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    public void Clean() {
        try {
            model.deleteAllStudents();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
}
