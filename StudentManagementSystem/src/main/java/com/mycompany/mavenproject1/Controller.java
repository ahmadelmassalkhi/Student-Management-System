package com.mycompany.mavenproject1;

// imports from same package
import com.mycompany.mavenproject1.MarksPage.MarksController;
import com.mycompany.mavenproject1.StudentsPage.StudentsController;

// imports from javafx
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

// other imports
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    
    /*******************************************************************/
    // PANE NAVIGATION
    
    /* BUTTONS */
    @FXML
    private Button btnStudents;
    @FXML
    private Button btnMarks;
    @FXML
    private Button btnSettings;
    @FXML
    private Button btnExit;
    
    /* PANES */
    @FXML
    private AnchorPane anchorPane_Students;
    @FXML
    private AnchorPane anchorPane_Settings;
    @FXML
    private AnchorPane anchorPane_Marks;
    
    /*******************************************************************/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initially, set `Students` Pane
        anchorPane_Students.toFront();
    }
    
    public void handleSidebarClicks(ActionEvent actionEvent) {
        
        // handle clicks from `Students` button
        if (actionEvent.getSource() == btnStudents) {
            StudentsController.getController().refresh(); // update data incase other pages made changes to the database
            anchorPane_Students.toFront();
        }

        // handle clicks from `Marks` button
        if(actionEvent.getSource() == btnMarks) {
            MarksController.getController().refresh(); // update data incase other pages made changes to the database
            anchorPane_Marks.toFront();
        }
        
        // handle clicks from `Settings` button
        if(actionEvent.getSource() == btnSettings) {
            SettingsController.setStage((Stage) anchorPane_Settings.getScene().getWindow());
            anchorPane_Settings.toFront();
        }
        
        // handle clicks from `Signout` button
        if(actionEvent.getSource() == btnExit) {
            System.out.println("Closing the application !");
            Stage stage = (Stage) btnExit.getScene().getWindow(); // get the stage
            stage.close(); // close
        }
    }
      
    /*******************************************************************/
}
