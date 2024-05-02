/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Controllers;

import com.mycompany.mavenproject1.Common.ErrorAlert;
import com.mycompany.mavenproject1.Managers.ConfigurationManager;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author AHMAD
 */
public class UpdateOwnerNameController {
    
    private Label label_OwnerName;
    public void setLabel(Label label_OwnerName) {
        this.label_OwnerName = label_OwnerName;
    }
    
    // DRAGGABLE WINDOW FEATURE
    private Stage stage;
    private double x, y;
    public void setWindowInformation(Stage stage, Parent root) {
        this.stage = stage;
        
        // block interaction with the parent window until it's closed
        stage.initModality(Modality.APPLICATION_MODAL);
        
        // set stage borderless
        stage.initStyle(StageStyle.UNDECORATED);

        // make it draggable
        root.setOnMousePressed(event -> {
            this.x = event.getSceneX();
            this.y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - this.x);
            stage.setY(event.getScreenY() - this.y);
        });
        
        this.display();
    }
   
    @FXML
    private TextField tf_OwnerName;
    private void display() {
        tf_OwnerName.setText(label_OwnerName.getText());
    }
    
    public void Cancel() {
        stage.close();
    }
    
    public void Update() {
        String newOwnerName = tf_OwnerName.getText();
        if(newOwnerName.isEmpty()) {
            ErrorAlert alert = new ErrorAlert("Error", "Invalid Input !", "Please enter a name !");
            alert.showAndWait();
            return;
        }
        
        try {
            ConfigurationManager.getManager().updateOwnerName(newOwnerName);
            label_OwnerName.setText(newOwnerName);
            Cancel();
        } catch (IOException | IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
}
