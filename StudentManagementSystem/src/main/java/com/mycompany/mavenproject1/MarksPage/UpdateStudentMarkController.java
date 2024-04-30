/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.MarksPage;

// imports from same package
import com.mycompany.mavenproject1.Common.CountryCodesManager;
import com.mycompany.mavenproject1.Common.ErrorAlert;
import com.mycompany.mavenproject1.ModelObjects.Student;
import com.mycompany.mavenproject1.models.StudentsModel;
import com.mycompany.mavenproject1.ModelObjects.Subscription;
import java.io.IOException;

// imports from javafx
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// other imports
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 *
 * @author AHMAD
 */
public class UpdateStudentMarkController implements Initializable {

    /*******************************************************************/
    
    /* TEXT FIELDS */
    @FXML
    private TextField tf_FullName;
    @FXML
    private TextField tf_Phone;
    @FXML
    private TextField tf_Mark;
    
    /* COMBO BOXES */
    @FXML
    private ComboBox comboBox_Subscription;
    @FXML
    private ComboBox comboBox_Language;
    @FXML
    private ComboBox comboBox_Grade;
    @FXML
    private ComboBox comboBox_CountryCode;
    
    /*******************************************************************/
    
    private StudentsModel model;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // initialize model
        try {
            model = StudentsModel.getModel();
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        
        // force the field `Mark` to be numeric only (can contain up to 1 decimal point too)
        tf_Mark.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tf_Mark.setText(oldValue); // Revert to the old value if not matching
            }
        });
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
    }
    
    private Student student;
    public void setStudent(Student s) {
        this.student = s;
        displayStudent();
    }
    
    // helper
    private void displayStudent() {
        tf_FullName.setText(student.getFullName());
        tf_Phone.setText(CountryCodesManager.getNumber(student.getPhone()));
        tf_Mark.setText(student.getMark() + "");
        
        comboBox_CountryCode.setValue(CountryCodesManager.getCountryCode(student.getPhone()));
        comboBox_Grade.setValue(student.getGrade() + "");
        comboBox_Language.setValue(student.getLanguage());
        comboBox_Subscription.setValue(student.getSubscription().getStatus() ? Subscription.ACTIVE_STRING : Subscription.INACTIVE_STRING);
    }
    
    /*******************************************************************/
    
    // `Cancel` button handler (also used by `Ok` button handler)
    public void closeStage() {
        stage.close();
    }
    
    // `Ok` button handler
    public void updateMark() {

        Float mark;
        try {
            // get mark
            mark = Float.valueOf((String) tf_Mark.getText());
        } catch (NumberFormatException ex) {
            ErrorAlert alert = new ErrorAlert("Error", "Invalid Input !", "Please enter a Mark !");
            alert.showAndWait();
            return;
        }
        
        // validate mark
        if(mark > 20 || mark < 0) {
            ErrorAlert alert = new ErrorAlert("Error", "Invalid Input !", "Mark should be between 0 and 20 !");
            alert.showAndWait();
            return;
        }
        
        // update mark in the database
        try {
            model.UpdateMark(student.getId(), mark);
            this.closeStage();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    /*******************************************************************/
}
