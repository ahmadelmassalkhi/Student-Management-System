package com.mycompany.mavenproject1;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;


public class Controller implements Initializable {
    
    /*******************************************************************/
    // PANE NAVIGATION
    
    /* BUTTONS */
    @FXML
    private Button btnStudents;
    @FXML
    private Button btnSettings;
    @FXML
    private Button btnSignout;
    
    /* PANES */
    @FXML
    private AnchorPane anchorPane_Students;
    @FXML
    private AnchorPane anchorPane_Settings;
    
    /*******************************************************************/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initially, set `Students` Pane
        anchorPane_Students.toFront();
    }
    
    public void handleSidebarClicks(ActionEvent actionEvent) {
        
        // handle clicks from `Students` button
        if (actionEvent.getSource() == btnStudents) {
            System.out.println("Pushed `Students` Pane to front !");
            anchorPane_Students.toFront();
        }
        
        // handle clicks from `Settings` button
        if(actionEvent.getSource() == btnSettings) {
            System.out.println("Pushed `Settings` Pane to front !");
            anchorPane_Settings.toFront();
        }
        
        // handle clicks from `Signout` button
        if(actionEvent.getSource() == btnSignout) {
            System.out.println("Closing the application !");
                        
            // Get the stage from the button
            Stage stage = (Stage) btnSignout.getScene().getWindow();

            // Close the stage
            stage.close();
        }
    }
      
    /*******************************************************************/
}