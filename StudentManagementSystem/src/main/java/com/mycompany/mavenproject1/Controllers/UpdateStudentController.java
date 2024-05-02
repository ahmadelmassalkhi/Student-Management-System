/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Controllers;

// imports from same project
import com.mycompany.mavenproject1.ViewsInitializers.ComboBoxInitializer;
import com.mycompany.mavenproject1.Common.ErrorAlert;
import com.mycompany.mavenproject1.Common.InputValidatorForStudentFields;
import com.mycompany.mavenproject1.Exceptions.MissingInputFieldException;
import com.mycompany.mavenproject1.Exceptions.PhoneAlreadyExistsException;
import com.mycompany.mavenproject1.ModelObjects.Student;
import com.mycompany.mavenproject1.models.StudentsModel;
import com.mycompany.mavenproject1.ModelObjects.Subscription;

// imports from javafx
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.DatePicker;

// other imports
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.io.IOException;

/**
 *
 * @author AHMAD
 */
public class UpdateStudentController implements Initializable {

    /*******************************************************************/
    
    /* TEXT FIELDS */
    @FXML
    private TextField tf_FullName;
    @FXML
    private TextField tf_Phone;
    
    /* COMBO BOXES */
    @FXML
    private ComboBox comboBox_SubscriptionStatus;
    @FXML
    private ComboBox comboBox_Language;
    @FXML
    private ComboBox comboBox_Grade;
    @FXML
    private ComboBox comboBox_CountryCode;
    
    @FXML
    private DatePicker datePicker_Date;
    
    /*******************************************************************/
    
    private void initializeComboBoxes() {
        List<String> options;
        
        // Add items to the `Subscription` ComboBox
        options = new ArrayList<>(ComboBoxInitializer.OPTIONS_SUBSCRIPTION);
        options.remove("Any");
        comboBox_SubscriptionStatus.setItems(FXCollections.observableArrayList(options));
        
        // Add items to the `Language` ComboBox
        options = new ArrayList<>(ComboBoxInitializer.OPTIONS_LANGUAGE);
        options.remove("Any");
        comboBox_Language.setItems(FXCollections.observableArrayList(options));
        
        // Add items to the `Grade` ComboBox
        options = new ArrayList<>(ComboBoxInitializer.OPTIONS_GRADE);
        options.remove("Any");
        comboBox_Grade.setItems(FXCollections.observableArrayList(options));
        
        // Add items to the `Code` ComboBox
        ComboBoxInitializer.Initialize_CountryCodes(comboBox_CountryCode);
    }
    
    private void initializeTextFields() {
        // force the field `Phone` to be numeric only
        tf_Phone.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                tf_Phone.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        // set interactive filtering feature
        tf_FullName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                tf_FullName.setText(oldValue);
            }
        });
    }
    
    private void initializeSubscription() {
        // Add a listener to the valueProperty of the ComboBox
        comboBox_SubscriptionStatus.valueProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.equals(Subscription.INACTIVE_STRING)) {
                // disable and set to null
                datePicker_Date.setDisable(true);
                datePicker_Date.setValue(null);
            } else if(oldValue != null && oldValue.equals(Subscription.INACTIVE_STRING)) {
                datePicker_Date.setValue(LocalDate.now().plusMonths(1));
                datePicker_Date.setDisable(false);
            }
        });
        
        // Create a listener to trigger when the value of the DatePicker changes
        datePicker_Date.setOnAction(event -> {
            if(datePicker_Date.getValue() != null && datePicker_Date.getValue().compareTo(LocalDate.now()) <= 0) {
                datePicker_Date.setValue(null);
                datePicker_Date.setDisable(true);
                comboBox_SubscriptionStatus.setValue(Subscription.INACTIVE_STRING);
            }
        });
    }
    
    private StudentsModel model;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeComboBoxes();
        initializeTextFields();
        initializeSubscription();

        // initialize model
        try {
            model = StudentsModel.getModel();
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    /*******************************************************************/
    
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
        tf_Phone.setText(ComboBoxInitializer.getNumber(student.getPhone()));
        
        comboBox_CountryCode.setValue(ComboBoxInitializer.getCountryCode(student.getPhone()));
        comboBox_Grade.setValue(student.getGrade() + "");
        comboBox_Language.setValue(student.getLanguage());
        comboBox_SubscriptionStatus.setValue(student.getSubscription().getStatusString());
        datePicker_Date.setValue(student.getSubscription().getDate());
    }
    
    // `Cancel` button handler (also used by `Ok` button handler)
    public void closeStage() {
        stage.close();
    }
    
    /*******************************************************************/
    
    // `Ok` button handler
    public void updateStudent() {
        
        // extract data from input-fields
        String fullName = tf_FullName.getText();
        String countryCode = (String) comboBox_CountryCode.getValue();
        String phone = tf_Phone.getText();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();        

        try {
            // create subscription of user's input (to contain user's input, used in validation)
            Subscription subscription = new Subscription();
            subscription.setDate(datePicker_Date.getValue());
            subscription.setStatus(comboBox_SubscriptionStatus.getValue().equals(Subscription.ACTIVE_STRING));
            
            // validate input (throws MissingInputFieldException)
            InputValidatorForStudentFields.validateUpdateFields(
                    fullName, // must not be Empty
                    phone, // must not be Empty
                    countryCode, // must not be Empty (might be because of its filtering mechanism)
                    subscription // must not be (active & null date)
            );
            
            // create updated student
            Student s = new Student();
            s.setFullName(fullName);
            s.setPhone(ComboBoxInitializer.getCountryCode(countryCode) + " " + phone);
            s.setGrade(grade);
            s.setLanguage(language);
            s.setSubscription(subscription);
            // s.setMark(Float.NaN); // by convension, UpdateStudentController should not update marks (UI doesn't allow it too)

            // update student in the database
            model.Update(student, s); // (throws PhoneAlreadyExistsException if found matching phone number in the database)
            
            // return back to parent (StudentsController) page
            closeStage();
        } catch (NullPointerException | IllegalArgumentException | SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (MissingInputFieldException | PhoneAlreadyExistsException ex) {
            ErrorAlert alert = new ErrorAlert("Error", "Invalid Input !", ex.getMessage());
            alert.showAndWait();
        }
    }

    /*******************************************************************/
}
