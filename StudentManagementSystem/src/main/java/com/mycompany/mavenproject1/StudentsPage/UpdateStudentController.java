/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.StudentsPage;

// imports from same package
import com.mycompany.mavenproject1.Common.CountryCodesManager;
import com.mycompany.mavenproject1.Common.ErrorAlert;
import com.mycompany.mavenproject1.Common.InputValidatorForStudentFields;
import com.mycompany.mavenproject1.Exceptions.MissingInputFieldException;
import com.mycompany.mavenproject1.Exceptions.PhoneAlreadyExistsException;
import com.mycompany.mavenproject1.models.Student;
import com.mycompany.mavenproject1.models.StudentsModel;

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
import java.util.ResourceBundle;

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
    
    private static ObservableList<String> countryList = CountryCodesManager.getCountryCodesList();
    private void initializeComboBoxes() {
        // Add items to the `Subscription` ComboBox
        comboBox_Subscription.setItems(FXCollections.observableArrayList("Active", "InActive"));
        
        // Add items to the `Language` ComboBox
        comboBox_Language.setItems(FXCollections.observableArrayList("English", "French"));
        
        // Add items to the `Grade` ComboBox
        comboBox_Grade.setItems(FXCollections.observableArrayList("8", "9", "10", "11", "12"));
        
        // Add items to the `Code` ComboBox
        countryList = CountryCodesManager.getCountryCodesList();
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
                    setText(CountryCodesManager.getCountryCode(item));
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
        tf_Phone.setText(CountryCodesManager.getNumber(student.getPhone()));
        
        comboBox_CountryCode.setValue(CountryCodesManager.getCountryCode(student.getPhone()));
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
        String countryCode = (String) comboBox_CountryCode.getValue();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();
        String subscription = (String) comboBox_Subscription.getValue();
        
        try {
            // validate input (throws MissingInputFieldException)
            InputValidatorForStudentFields.validateUpdateFields(firstName, lastName, phone, grade, language, subscription, countryCode);
        
            // create updated student
            Student s = new Student();
            s.setFirstName(firstName);
            s.setLastName(lastName);
            s.setPhone(countryCode + " " + phone);
            s.setGrade(grade);
            s.setLanguage(language);
            s.setSubscriptionStatus(Student.getSubscriptionStatusInt(subscription));

            // update student in the database
            model.updateStudent(student, s); // (throws PhoneAlreadyExistsException if found matching phone number in the database)
            
            // return back to parent (StudentsController) page
            closeStage();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (MissingInputFieldException | PhoneAlreadyExistsException ex) {
            ErrorAlert alert = new ErrorAlert("Error", "Invalid Input !", ex.getMessage());
            alert.showAndWait();
        }
    }

    /*******************************************************************/
}
