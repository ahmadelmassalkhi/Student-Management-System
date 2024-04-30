/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.StudentsPage;

// imports from same package
import com.mycompany.mavenproject1.Common.ComboBoxesOptions;
import com.mycompany.mavenproject1.Common.CountryCodesManager;
import com.mycompany.mavenproject1.Common.ErrorAlert;
import com.mycompany.mavenproject1.Common.InputValidatorForStudentFields;
import com.mycompany.mavenproject1.Exceptions.MissingInputFieldException;
import com.mycompany.mavenproject1.Exceptions.PhoneAlreadyExistsException;
import com.mycompany.mavenproject1.ModelObjects.Student;
import com.mycompany.mavenproject1.models.StudentsModel;
import com.mycompany.mavenproject1.ModelObjects.Subscription;
import java.io.IOException;

// imports from javafx
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

// other imports
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.DatePicker;

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
    
    @FXML
    private DatePicker datePicker_Date;
    
    /*******************************************************************/
    
    private static ObservableList<String> countryCodesObservableList;
    private void initializeComboBoxes() {
        List<String> options;
        
        // Add items to the `Subscription` ComboBox
        options = new ArrayList<>(ComboBoxesOptions.OPTIONS_SUBSCRIPTION);
        options.remove("Any");
        comboBox_Subscription.setItems(FXCollections.observableArrayList(options));
        
        // Add items to the `Language` ComboBox
        options = new ArrayList<>(ComboBoxesOptions.OPTIONS_LANGUAGE);
        options.remove("Any");
        comboBox_Language.setItems(FXCollections.observableArrayList(options));
        
        // Add items to the `Grade` ComboBox
        options = new ArrayList<>(ComboBoxesOptions.OPTIONS_GRADE);
        options.remove("Any");
        comboBox_Grade.setItems(FXCollections.observableArrayList(options));
        
        // Add items to the `Code` ComboBox
        countryCodesObservableList = ComboBoxesOptions.OPTIONS_COUNTRYCODES;
        comboBox_CountryCode.setItems(countryCodesObservableList);
        
        // set ComboBox search on key-press feature
        comboBox_CountryCode.setOnKeyPressed(event -> {
            String filter = event.getText();
            ObservableList<String> filteredList = FXCollections.observableArrayList();
            for (String country : countryCodesObservableList) {
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
                    setText(CountryCodesManager.getCountryCode(item));
                }
            }
        });
        
        // Add a listener to the valueProperty of the ComboBox
        comboBox_Subscription.valueProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.equals("InActive")) {
                // disable and set to null
                datePicker_Date.setDisable(true);
                datePicker_Date.setValue(null);
            } else {
                datePicker_Date.setDisable(false);
            }
            
            // if activated, set 1 month subscription by default
            if(oldValue != null && oldValue.equals("InActive") && newValue.equals("Active")) {
                // set date to 1 month in future (by default)
                datePicker_Date.setValue(LocalDate.now().plusMonths(1));
            }
        });
    }
    
    private void initializeTextFields() {
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
        initializeTextFields();
        
        // initialize model
        try {
            model = StudentsModel.getModel();
        } catch (SQLException | IOException ex) {
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
        tf_Phone.setText(CountryCodesManager.getNumber(student.getPhone()));
        
        comboBox_CountryCode.setValue(CountryCodesManager.getCountryCode(student.getPhone()));
        comboBox_Grade.setValue(student.getGrade() + "");
        comboBox_Language.setValue(student.getLanguage());
        comboBox_Subscription.setValue(student.getSubscription().getStatus() ? Subscription.ACTIVE_STRING : Subscription.INACTIVE_STRING);
        datePicker_Date.setValue(student.getSubscription().getDate());
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
        String countryCode = (String) comboBox_CountryCode.getValue();
        String phone = tf_Phone.getText();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();        

        try {
            Subscription subscription = new Subscription();
            subscription.setDate(datePicker_Date.getValue());
            subscription.setStatus(comboBox_Subscription.getValue().equals(Subscription.ACTIVE_STRING));
            
            // validate input (throws MissingInputFieldException)
            InputValidatorForStudentFields.validateUpdateFields(
                    firstName, // must not be Empty
                    lastName, // must not be Empty
                    phone, // must not be Empty
                    countryCode, // must not be Empty (might be because of its filtering mechanism)
                    subscription // must not be (active & null date)
            );
            
            // create subscription
            if(datePicker_Date.getValue() == null || datePicker_Date.getValue().compareTo(LocalDate.now()) <= 0) {
                subscription.setStatus(Boolean.FALSE);
                subscription.setDate(null);
            } else {
                subscription.setStatus(Boolean.TRUE);
                subscription.setDate(datePicker_Date.getValue());
            }
        
            // create updated student
            Student s = new Student();
            s.setFirstName(firstName);
            s.setLastName(lastName);
            s.setPhone(CountryCodesManager.getCountryCode(countryCode) + " " + phone);
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
