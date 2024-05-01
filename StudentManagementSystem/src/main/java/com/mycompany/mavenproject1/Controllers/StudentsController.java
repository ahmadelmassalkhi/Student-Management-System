/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Controllers;

// imports from same package
import com.mycompany.mavenproject1.Common.ErrorAlert;
import com.mycompany.mavenproject1.Common.CountryCodesManager;
import com.mycompany.mavenproject1.Common.ComboBoxesOptions;
import com.mycompany.mavenproject1.Common.InputValidatorForStudentFields;
import com.mycompany.mavenproject1.Exceptions.MissingInputFieldException;
import com.mycompany.mavenproject1.Exceptions.PhoneAlreadyExistsException;
import com.mycompany.mavenproject1.ModelObjects.Student;
import com.mycompany.mavenproject1.models.StudentsModel;
import com.mycompany.mavenproject1.ModelObjects.Subscription;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

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
    private TextField tf_FullName;
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
    private TableColumn col_FullName;
    @FXML
    private TableColumn col_Phone;
    @FXML
    private TableColumn col_Grade;
    @FXML
    private TableColumn col_Language;
    @FXML
    private TableColumn col_SubscriptionStatus;
    @FXML
    private TableColumn col_SubscriptionExpireDate;
    
    /*******************************************************************/

    // so we are able to refresh data changes from outside the class object
    public StudentsController() { studentsController = this; }
    private static StudentsController studentsController = null;
    public static StudentsController getController() { return studentsController; }
    
    /*******************************************************************/
    
    private void initializeTable() {
        // allow multiple selections
        studentsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // associate data to columns
        col_ID.setCellValueFactory(new PropertyValueFactory<Student, String>("id"));
        col_FullName.setCellValueFactory(new PropertyValueFactory<Student, String>("fullName"));
        col_Grade.setCellValueFactory(new PropertyValueFactory<Student, String>("grade"));
        col_Language.setCellValueFactory(new PropertyValueFactory<Student, String>("language"));
        col_Phone.setCellValueFactory(new PropertyValueFactory<Student, String>("phone"));
        col_SubscriptionStatus.setCellValueFactory(new PropertyValueFactory<Student, String>("subscriptionStatus"));
        col_SubscriptionExpireDate.setCellValueFactory(new PropertyValueFactory<Student, String>("subscriptionDate"));

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
            ObservableList<TableColumn> subColumns = column.getColumns();
            for(TableColumn subCol : subColumns) setColumnResizeLimitListener(subCol, allColumns);
            if(subColumns.isEmpty()) setColumnResizeLimitListener(column, allColumns);
        }
    }
    private void setColumnResizeLimitListener(TableColumn column, ObservableList<TableColumn> allColumns) {
        column.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            // get sum of all currently set widths (widths)
            double widthSum = 0;
            for(TableColumn col : allColumns) {
                ObservableList<TableColumn> subColumns = col.getColumns();
                for(TableColumn subCol : subColumns) widthSum += subCol.getWidth();
                if(subColumns.isEmpty()) widthSum += col.getWidth();
            }

            // if exceeds totalWidth, set the max width to the oldWidth
            if (widthSum > studentsTable.getWidth()) column.setMaxWidth(oldWidth.doubleValue());
            else column.setMaxWidth(Double.MAX_VALUE); // reset max width
        });
    }
    
    private void initializeTextFields() {
        // set interactive filtering feature
        tf_FullName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                tf_FullName.setText(oldValue);
            } else Search();
        });
        tf_Phone.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // force the field `Phone` to be numeric only
            if (!newValue.matches("\\d*")) {
                tf_Phone.setText(newValue.replaceAll("[^\\d]", ""));
            } else Search();
        });
    }
    
    private static ObservableList<String> countryCodesObservableList;
    private void initializeComboBoxes() {
        // Add items to the `Subscription` ComboBox
        comboBox_Subscription.setItems(FXCollections.observableArrayList(ComboBoxesOptions.OPTIONS_SUBSCRIPTION));
        comboBox_Subscription.setValue(ComboBoxesOptions.OPTION_DEFAULT_SUBSCRIPTION);
        
        // Add items to the `Language` ComboBox
        comboBox_Language.setItems(FXCollections.observableArrayList(ComboBoxesOptions.OPTIONS_LANGUAGE));
        comboBox_Language.setValue(ComboBoxesOptions.OPTION_DEFAULT_LANGUAGE);

        // Add items to the `Grade` ComboBox
        comboBox_Grade.setItems(FXCollections.observableArrayList(ComboBoxesOptions.OPTIONS_GRADE));
        comboBox_Grade.setValue(ComboBoxesOptions.OPTION_DEFAULT_GRADE);        
        
        // Add items to the `Code` ComboBox
        countryCodesObservableList = ComboBoxesOptions.OPTIONS_COUNTRYCODES;
        comboBox_CountryCode.setItems(countryCodesObservableList);
        comboBox_CountryCode.setValue("+961");
        
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
    
        // initialize model
        try {
            model = StudentsModel.getModel();
        } catch (SQLException | IOException ex) {
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
        String fullName = tf_FullName.getText();
        String phone = tf_Phone.getText();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();
        String subscriptionStatus = (String) comboBox_Subscription.getValue();
        String countryCode = (String) comboBox_CountryCode.getValue();
        
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
                // default for 1 month subscription
                subscription.setDate(LocalDate.now().plusMonths(1));
                subscription.setStatus(true);
            } else {
                // inactive subscription
                subscription.setDate((LocalDate) null);
                subscription.setStatus(false);
            }

            // fill student information
            Student s = new Student();
            s.setFullName(fullName);
            s.setPhone(CountryCodesManager.getCountryCode(countryCode) + " " + phone);
            s.setGrade(grade);
            s.setLanguage(language);
            s.setSubscription(subscription);
//            s.setMark(mark); // by convension, Students page should not allow adding/searching mark

            // add student to database
            model.addStudent(s); // (throws PhoneAlreadyExistsException if found matching phone number in the database)
            
            // adopt to data changes
            this.clearTextFields(); // make adding another new student easier
            this.refresh();
        } catch (NullPointerException | SQLException ex) {
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
        List<Long> IDs = new ArrayList<>();
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
    
    /*
     * refresh data (stats, and table based on inputs)
     * public because it is used in `Controller` to make sure data is up to date with possible changes to the database, made by other pages
     */
    public void refresh() {
        this.refreshStats();
        this.Search();
    }
    
    private void clearTextFields() {
        // clear inputs
        tf_FullName.clear();
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
        String fullName = tf_FullName.getText();
        String phone = tf_Phone.getText();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();
        String subscriptionStatus = (String) comboBox_Subscription.getValue();

        // fix input-data format
        if(fullName.isEmpty()) fullName = null;
        if(phone.isEmpty()) phone = null;
        if(grade.isEmpty() || grade.equals("Any")) grade = null;
        if(language.isEmpty() || language.equals("Any")) language = null;
        Subscription subscription = new Subscription();
        if(!(subscriptionStatus.isEmpty() || subscriptionStatus.equals("Any"))) {
            subscription.setStatus(subscriptionStatus.equalsIgnoreCase(Subscription.ACTIVE_STRING));
        } // else, its attributes default to null (because we defined them as objects not primitive types)
        
        // perform search
        try {
            // get & display filtered students
            List<Student> result = model.Read(null, fullName, phone, grade, language, subscription, null, null, null);
            studentsTable.setItems(FXCollections.observableArrayList(result));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    /*******************************************************************/
}
