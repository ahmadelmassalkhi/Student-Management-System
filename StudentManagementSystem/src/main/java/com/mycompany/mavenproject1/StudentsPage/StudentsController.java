/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.StudentsPage;

// imports from same package
import com.mycompany.mavenproject1.Common.ErrorAlert;
import com.mycompany.mavenproject1.Common.CountryCodesManager;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

// other imports
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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

    private StudentsModel model;
    
    @FXML
    private Label label_TotalStudents;
    @FXML
    private Label label_TotalSubscriptions;
    
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
    
    /* TABLE */
    @FXML
    private TableView studentsTable;
    
    /* COLUMNS */
    @FXML
    private TableColumn col_ID;
    @FXML
    private TableColumn col_FirstName;
    @FXML
    private TableColumn col_LastName;
    @FXML
    private TableColumn col_Phone;
    @FXML
    private TableColumn col_Grade;
    @FXML
    private TableColumn col_Language;
    @FXML
    private TableColumn col_Subscription;
    
    /*******************************************************************/
    
    private void initializeTable() {
        // allow multiple selections
        studentsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // associate data to columns
        col_ID.setCellValueFactory(new PropertyValueFactory<Student, String>("id"));
        col_FirstName.setCellValueFactory(new PropertyValueFactory<Student, String>("firstName"));
        col_LastName.setCellValueFactory(new PropertyValueFactory<Student, String>("lastName"));
        col_Grade.setCellValueFactory(new PropertyValueFactory<Student, String>("grade"));
        col_Language.setCellValueFactory(new PropertyValueFactory<Student, String>("language"));
        col_Phone.setCellValueFactory(new PropertyValueFactory<Student, String>("phone"));
        col_Subscription.setCellValueFactory(new PropertyValueFactory<Student, String>("subscriptionStatus"));
        
        // hide ID column by default (made to be interacted with, programmatically)
        col_ID.setVisible(false);
        
        // Handle double click event
        studentsTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                Student s = (Student) studentsTable.getSelectionModel().getSelectedItem();
                if (s != null) updateStudent(s);
            }
        });
        
        ObservableList<TableColumn> allColumns = studentsTable.getColumns();
        for (TableColumn column : allColumns) {
            column.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                // get sum of all currently set widths (widths)
                double widthSum = 0;
                for(TableColumn col : allColumns) widthSum += col.getWidth();

                // if exceeds totalWidth, set the max width to the oldWidth
                if (widthSum > studentsTable.getWidth()) {
                    column.setMaxWidth(oldWidth.doubleValue()); // Revert back to oldWidth 
                                                                // (by setting maxWidth to oldWidth we enforce prefWidth = oldWidth without overlapping listener events resulting in errors)
                } else {
                    column.setMaxWidth(Double.MAX_VALUE); // reset max width
                }
            });
        }
    }
    
    private void initializeTextFields() {
        // set interactive filtering feature
        tf_FirstName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            Search();
        });
        tf_LastName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            Search();
        });
        tf_Phone.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // force the field `Phone` to be numeric only
            if (!newValue.matches("\\d*")) {
                tf_Phone.setText(newValue.replaceAll("[^\\d]", ""));
            }
            Search();
        });
    }
    
    private static ObservableList<String> countryList;
    private void initializeComboBoxes() {
        // Add items to the `Subscription` ComboBox
        comboBox_Subscription.setItems(FXCollections.observableArrayList("Any", "Active", "InActive"));
        comboBox_Subscription.setValue("Any");        

        // Add items to the `Language` ComboBox
        comboBox_Language.setItems(FXCollections.observableArrayList("Any", "English", "French"));
        comboBox_Language.setValue("Any");        

        // Add items to the `Grade` ComboBox
        comboBox_Grade.setItems(FXCollections.observableArrayList(
                "Any", 
                "8", 
                "Brevet", 
                "10", 
                "11",
                "Terminal SE",
                "Terminal LS",
                "Terminal GS",
                "Terminal LH"
            )
        );
        comboBox_Grade.setValue("Any");
        
        // Add items to the `Code` ComboBox
        countryList = CountryCodesManager.getCountryCodesList();
        comboBox_CountryCode.setItems(countryList);
        comboBox_CountryCode.setValue("+961");
        
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
        
        // show on selected, only the country-code of the country-string
        comboBox_CountryCode.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(CountryCodesManager.getCountryCode(item));
                }
            }
        });
        
        // set interactive filtering feature
        comboBox_Language.valueProperty().addListener((obs, oldValue, newValue) -> {
            Search();
        });
        comboBox_CountryCode.valueProperty().addListener((obs, oldValue, newValue) -> {
            Search();
        });
        comboBox_Grade.valueProperty().addListener((obs, oldValue, newValue) -> {
            Search();
        });
        comboBox_Subscription.valueProperty().addListener((obs, oldValue, newValue) -> {
            Search();
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTable();
        initializeTextFields();
        initializeComboBoxes();
    
        // initialize Students Controller (with its model)
        try {
            model = StudentsModel.getModel();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        
        // set students data
        this.refresh();
    }
    
    /*******************************************************************/
    // CRUD OPERATIONS (FROM UI)
    
    // CREATE
    public void AddStudent() {
        
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
            InputValidatorForStudentFields.validateAddFields(firstName, lastName, phone, grade, language, subscription, countryCode);

            // fill student information
            Student s = new Student();
            s.setFirstName(firstName);
            s.setLastName(lastName);
            s.setPhone(countryCode + " " + phone);
            s.setGrade(grade);
            s.setLanguage(language);
            s.setSubscriptionStatus(Student.getSubscriptionStatusInt(subscription));

            // add student to database
            model.addStudent(s); // (throws PhoneAlreadyExistsException if found matching phone number in the database)
            
            // adopt to data changes
            this.clearTextFields(); // make adding another new student easier
            this.refresh();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (MissingInputFieldException | PhoneAlreadyExistsException ex) {
            ErrorAlert alert = new ErrorAlert("Error", "Invalid Input !", ex.getMessage());
            alert.showAndWait();
        }
    }
    
    // UPDATE
    private void updateStudent(Student s) {
        try {
            // load resource
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpdateStudent.fxml"));
            Parent root = loader.load();

            // create window
            Stage stage = new Stage();
            stage.setScene(new Scene(root, root.prefWidth(-1), root.prefHeight(-1)));
            
            // Access the controller and set the data
            UpdateStudentController controller = loader.getController();
            controller.setWindowInformation(stage, root);
            controller.setStudent(s);
            
            // wait until student is successfully updated or cancelled
            stage.showAndWait();
            
            // adopt to data changes
            this.refresh();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    // DELETE
    public void deleteSelectedStudents() {
        // get all selected students
        ObservableList<Student> selectedStudents = studentsTable.getSelectionModel().getSelectedItems();
        if(selectedStudents.isEmpty()) return; // nothing was selected
        
        // get all their IDs
        List<Integer> IDs = new ArrayList<>();
        for(Student s : selectedStudents) IDs.add(s.getId());
        
        // delete from database
        try {
            model.deleteStudentsByIDs(IDs);
            
            // adopt to data changes
            this.refresh();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    public void clearButton() {
        // clear all inputs
        this.clearTextFields();
        this.clearComboBoxes();
        
        // refresh data
        this.refresh();
    }
    
    /*******************************************************************/
    // HELPER METHODS
    
    private void refresh() {
        this.refreshStats();
        this.Search();
    }
    
    private void clearTextFields() {
        // clear inputs
        tf_FirstName.clear();
        tf_LastName.clear();
        tf_Phone.clear();
    }
    
    private void clearComboBoxes() {
        // clear drop-down menus
        comboBox_Language.setValue("Any");
        comboBox_Subscription.setValue("Any");
        comboBox_Grade.setValue("Any");
    }
    
    private void refreshStats() {
        // set statistical data
        try {
            label_TotalStudents.setText(model.getNumberOfStudents() + "");
            label_TotalSubscriptions.setText(model.getNumberOfSubscriptions() + "");
        } catch (SQLException ex) {
            Logger.getLogger(StudentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // refresh table based on input
    private void Search() {
        // extract data from input-fields
        String firstName = tf_FirstName.getText();
        String lastName = tf_LastName.getText();
        String phone = tf_Phone.getText();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();
        String subscription = (String) comboBox_Subscription.getValue();

        // perform search
        try {
            List<Student> result = model.searchStudents(firstName, lastName, phone, grade, language, subscription);
            studentsTable.setItems(FXCollections.observableArrayList(result));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    /*******************************************************************/
}
