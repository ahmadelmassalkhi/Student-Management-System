/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.Common.Common;
import com.mycompany.mavenproject1.Common.ErrorAlert;
import com.mycompany.mavenproject1.Common.InputValidator;
import com.mycompany.mavenproject1.Exceptions.PhoneAlreadyExistsException;
import com.mycompany.mavenproject1.models.Student;
import com.mycompany.mavenproject1.models.StudentsModel;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author AHMAD
 */
public class UpdateStudentController implements Initializable {

    /*******************************************************************/
    
    /* TEXT FIELDS */
    @FXML
    private TextField tf_FirstName;
    @FXML
    private TextField tf_LastName;
    @FXML
    private TextField tf_Phone;
    
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
    
    private static ObservableList<String> countryList = Common.getCountryCodesList();
    private void initializeComboBoxes() {
        // Add items to the `Subscription` ComboBox
        comboBox_Subscription.setItems(FXCollections.observableArrayList("Active", "InActive"));
        
        // Add items to the `Language` ComboBox
        comboBox_Language.setItems(FXCollections.observableArrayList("English", "French"));
        
        // Add items to the `Grade` ComboBox
        comboBox_Grade.setItems(FXCollections.observableArrayList("8", "9", "10", "11", "12"));
        
        // Add items to the `Code` ComboBox
        countryList = Common.getCountryCodesList();
        comboBox_CountryCode.setItems(countryList);
        
        // set ComboBox search on key-press feature
        comboBox_CountryCode.setOnKeyPressed(event -> {
            String filter = event.getText();
            ObservableList<String> filteredList = FXCollections.observableArrayList();
            for (String country : countryList) {
                if (country.toLowerCase().startsWith(filter.toLowerCase())) {
                    filteredList.add(country);
                }
            }
            comboBox_CountryCode.setItems(filteredList);
        });
        
        // show on selected => country-code of country-string
        comboBox_CountryCode.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // set country-code
                    setText(Common.getCountryCode(item));
                }
            }
        });

        // force the field `Phone` to be numeric only
        tf_Phone.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                tf_Phone.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    private StudentsModel model;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeComboBoxes();
        
        // initialize model
        try {
            model = StudentsModel.getModel();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
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
    
    /*******************************************************************/
    
    // helper
    private void displayStudent() {
        tf_FirstName.setText(student.getFirstName());
        tf_LastName.setText(student.getLastName());
        tf_Phone.setText(Common.getNumber(student.getPhone()));
        
        comboBox_CountryCode.setValue(Common.getCountryCode(student.getPhone()));
        comboBox_Grade.setValue(student.getGrade()+"");
        comboBox_Language.setValue(student.getLanguage());
        comboBox_Subscription.setValue(student.getSubscriptionStatus());
    }
    
    // `Cancel` button handler (also used by `Ok` button handler)
    public void closeStage() {
        stage.close();
    }
    
    // `Ok` button handler
    public void updateStudent() {
        
        // extract data from input-fields
        String firstName = tf_FirstName.getText();
        String lastName = tf_LastName.getText();
        String phone = tf_Phone.getText();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();
        String subscription = (String) comboBox_Subscription.getValue();
        
        // validate input data
        String errorMessage = InputValidator.inputErrorMessage(firstName, lastName, phone, grade, language, subscription);
        if(errorMessage != null) {
            ErrorAlert alert = new ErrorAlert("Error", "Invalid Input !", errorMessage);
            alert.showAndWait();
            return;
        }
        
        // no need to validate this, it is already enforced within the UI
        String countryCode = Common.getCountryCode((String) comboBox_CountryCode.getValue());
        
        // create updated student
        Student s = new Student();
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setPhone(countryCode + " " + phone);
        s.setGrade(Integer.parseInt(grade));
        s.setLanguage(language);
        s.setSubscriptionStatus(subscription);
        
        // update student in the database
        try {
            model.updateStudent(student, s);
            closeStage();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (PhoneAlreadyExistsException ex) {
            ErrorAlert alert = new ErrorAlert("Error", "Invalid Input !", ex.getMessage());
            alert.showAndWait();
        }
    }

    /*******************************************************************/
}
