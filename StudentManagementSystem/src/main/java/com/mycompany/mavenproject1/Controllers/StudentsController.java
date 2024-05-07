/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Controllers;

// imports from same project
import com.mycompany.mavenproject1.Common.ErrorAlert;
import com.mycompany.mavenproject1.Common.InputValidatorForStudentFields;
import com.mycompany.mavenproject1.ViewsInitializers.TableViewInitializer;
import com.mycompany.mavenproject1.Exceptions.MissingInputFieldException;
import com.mycompany.mavenproject1.Exceptions.PhoneAlreadyExistsException;
import com.mycompany.mavenproject1.ModelObjects.Student;
import com.mycompany.mavenproject1.models.StudentsModel;
import com.mycompany.mavenproject1.ModelObjects.Subscription;
import com.mycompany.mavenproject1.ViewsInitializers.ComboBoxInitializer;
import com.mycompany.mavenproject1.ViewsInitializers.TextFieldInitializer;

// imports from javafx
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

// other imports
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AHMAD
 */
public class StudentsController implements Initializable {
    
    
    /*******************************************************************/
    
    /* LABELS */
    @FXML private Label label_TotalStudents;
    @FXML private Label label_TotalSubscriptions;
    
    /* TEXT FIELDS */
    @FXML private TextField tf_FullName;
    @FXML private TextField tf_Phone;
    
    /* COMBO BOXES */
    @FXML private ComboBox comboBox_SubscriptionStatus;
    @FXML private ComboBox comboBox_Language;
    @FXML private ComboBox comboBox_Grade;
    @FXML private ComboBox comboBox_CountryCode;
    
    /* TABLE */
    @FXML
    private TableView studentsTable;
    
    /* TABLE COLUMNS */
    @FXML private TableColumn col_ID;
    @FXML private TableColumn col_FullName;
    @FXML private TableColumn col_Phone;
    @FXML private TableColumn col_Grade;
    @FXML private TableColumn col_Language;
    @FXML private TableColumn col_SubscriptionStatus;
    @FXML private TableColumn col_SubscriptionExpireDate;
    
    /*******************************************************************/

    // so we are able to refresh data changes from outside the class object
    public StudentsController() { studentsController = this; }
    private static StudentsController studentsController = null;
    public static StudentsController getController() { return studentsController; }
    
    /*******************************************************************/
    
    private void initializeTable() {
        TableViewInitializer.Initialize_ID(studentsTable, col_ID);
        TableViewInitializer.Initialize_FullName(studentsTable, col_FullName);
        TableViewInitializer.Initialize_Phone(studentsTable, col_Phone);
        TableViewInitializer.Initialize_Grade(studentsTable, col_Grade);
        TableViewInitializer.Initialize_Language(studentsTable, col_Language);
        TableViewInitializer.Initialize_SubscriptionStatus(studentsTable, col_SubscriptionStatus);
        TableViewInitializer.Initialize_SubscriptionExpireDate(studentsTable, col_SubscriptionExpireDate);
        
        // Handle double click event
        studentsTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                Student s = (Student) studentsTable.getSelectionModel().getSelectedItem();
                if (s != null) Update(s);
            }
        });
    }
    
    private void initializeComboBoxes() {
        ComboBoxInitializer.Initialize_Languages(comboBox_Language);
        ComboBoxInitializer.Initialize_CountryCodes(comboBox_CountryCode);
        ComboBoxInitializer.Initialize_Grades(comboBox_Grade);
        ComboBoxInitializer.Initialize_SubscriptionStatus(comboBox_SubscriptionStatus);
        
        // set interactive filtering feature
        comboBox_Language.valueProperty().addListener((obs, oldValue, newValue) -> Read());
        comboBox_CountryCode.valueProperty().addListener((obs, oldValue, newValue) -> Read());
        comboBox_Grade.valueProperty().addListener((obs, oldValue, newValue) -> Read());
        comboBox_SubscriptionStatus.valueProperty().addListener((obs, oldValue, newValue) -> Read());
    }
    
    private void initializeTextFields() {
        /* set interactive filtering feature */
        tf_FullName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches(TextFieldInitializer.REGEX_FULLNAME)) {
                tf_FullName.setText(oldValue);
            } else Read();
        });
        tf_Phone.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // force the field `Phone` to be numeric only
            if (!newValue.matches(TextFieldInitializer.REGEX_PHONE)) {
                tf_Phone.setText(oldValue);
            } else Read();
        });
    }
    
    private StudentsModel model;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // initialize views
        initializeTable();
        initializeTextFields();
        initializeComboBoxes();
    
        // initialize model
        try {
            model = StudentsModel.getModel();
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        
        // set students data
        this.Read();
    }
    
    /*******************************************************************/
    // CRUD OPERATIONS (FROM UI)
    
    // CREATE
    public void Create() {
        
        // extract data from input-fields
        String fullName = (tf_FullName.getText()).trim();
        String countryCode = (String) comboBox_CountryCode.getValue();
        String phone = tf_Phone.getText();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();
        String subscriptionStatus = (String) comboBox_SubscriptionStatus.getValue();
        
        try {
            // validate input (throws MissingInputFieldException)
            InputValidatorForStudentFields.validateAddFields(
                    fullName, // must not be Empty
                    phone, // must not be Empty
                    grade, // must not be `Any`
                    language, // must not be `Any`
                    subscriptionStatus, // must not be `Any`
                    countryCode // must not be Empty (might be because of its filtering mechanism)
            );
            
            // fill subscription information
            Subscription subscription = new Subscription();
            if(subscriptionStatus.equalsIgnoreCase(Subscription.ACTIVE_STRING)) {
                // active subscription
                subscription.setStatus(true);
                subscription.setDate(LocalDate.now().plusMonths(1)); // default for 1 month subscription
            } else {
                // inactive subscription
                subscription.setStatus(false);
                subscription.setDate((LocalDate) null);
            }

            // fill student information
            Student s = new Student();
            s.setFullName(fullName);
            s.setPhone(ComboBoxInitializer.extractCountryCode(countryCode) + " " + phone);
            s.setGrade(grade);
            s.setLanguage(language);
            s.setSubscription(subscription);
//            s.setMark(mark); // by convension, Students page should not allow adding/searching mark

            // add student to database
            model.Create(s); // (throws PhoneAlreadyExistsException if found matching phone number in the database)
            
            // adopt to data changes
            this.clearTextFields(); // make adding another new student easier
            this.Read();
        } catch (NullPointerException | SQLException | IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (MissingInputFieldException | PhoneAlreadyExistsException ex) {
            ErrorAlert alert = new ErrorAlert("Error", "Invalid Input !", ex.getMessage());
            alert.showAndWait();
        }
    }
    
    // READ
    public void Read() {
        // extract data from input-fields
        String fullName = tf_FullName.getText();
        String phone = tf_Phone.getText();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();
        String subscriptionStatus = (String) comboBox_SubscriptionStatus.getValue();

        // fix input-data format
        if(fullName.isEmpty()) fullName = null;
        if(phone.isEmpty()) phone = null;
        if(grade.equals("Any")) grade = null;
        if(language.equals("Any")) language = null;
        
        // extract subscription status (if not set to any)
        Subscription subscription = new Subscription();
        if(!subscriptionStatus.equals("Any")) {
            subscription.setStatus(subscriptionStatus.equalsIgnoreCase(Subscription.ACTIVE_STRING));
        } // else, its attributes default to null (because we defined them as objects not primitive types)
        
        // perform search
        try {
            // get filtered students
            List<Student> result = model.Read(
                    null, // id
                    fullName, 
                    phone, 
                    grade, 
                    language, 
                    subscription, 
                    null, // minimum mark
                    null, // maximum mark
                    null); // marks order
            // display them
            studentsTable.setItems(FXCollections.observableArrayList(result));
            
            label_TotalStudents.setText(result.size() + "");
            label_TotalSubscriptions.setText(
                    "" + model.getNumberOfActiveSubscriptions(null, fullName, phone, grade, language, null, null, null)
            );
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    // UPDATE
    private void Update(Student s) {
        try {
            // load resource
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpdateStudent.fxml"));
            Parent root = loader.load();
            
            // create window
            Stage stage = new Stage();
            stage.setScene(new Scene(root, root.prefWidth(-1), root.prefHeight(-1)));
            
            // Center stage on screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - root.prefWidth(-1)) / 2);
            stage.setY((screenBounds.getHeight() - root.prefHeight(-1)) / 2);

            // Access the controller and set the data
            UpdateStudentController controller = loader.getController();
            controller.setWindowInformation(stage, root);
            controller.setStudent(s);
            
            // wait until student is successfully updated or cancelled
            stage.showAndWait();
            
            // adopt to data changes
            this.Read();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    // DELETE
    public void Delete() {
        // get all selected students
        ObservableList<Student> selectedStudents = studentsTable.getSelectionModel().getSelectedItems();
        if(selectedStudents.isEmpty()) return; // nothing was selected
        
        // get all their IDs
        List<Long> IDs = new ArrayList<>();
        for(Student s : selectedStudents) IDs.add(s.getId());
        
        // delete from database
        try {
            model.deleteStudentsByIDs(IDs);
            
            // adopt to data changes
            this.Read();
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    /*******************************************************************/
    // HELPER METHODS
    
    // called by `Clear` button
    public void Clear() {
        // clear all inputs
        this.clearTextFields();
        
        // clear combo-boxes
        comboBox_Language.setValue(ComboBoxInitializer.OPTION_DEFAULT_LANGUAGE);
        comboBox_SubscriptionStatus.setValue(ComboBoxInitializer.OPTION_DEFAULT_SUBSCRIPTION);
        comboBox_Grade.setValue(ComboBoxInitializer.OPTION_DEFAULT_GRADE);
        
        // refresh data
        this.Read();
    }
    
    private void clearTextFields() {
        // clear inputs
        tf_FullName.clear();
        tf_Phone.clear();
    }
    
    /*******************************************************************/
}
