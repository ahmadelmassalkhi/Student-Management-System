/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

// imports from same package
import com.mycompany.mavenproject1.models.Student;
import com.mycompany.mavenproject1.models.StudentsModel;

// other imports
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

/**
 *
 * @author AHMAD
 */
public class MarksController implements Initializable {
    
    private StudentsModel model;

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
    private ComboBox comboBox_Language;
    @FXML
    private ComboBox comboBox_Grade;

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
    private TableColumn col_Mark;
    
    /*******************************************************************/

    /* CHECK BOXES */
    @FXML
    private CheckBox checkbox_FirstName;
    @FXML
    private CheckBox checkbox_LastName;
    @FXML
    private CheckBox checkbox_Phone;
    @FXML
    private CheckBox checkbox_Grade;
    @FXML
    private CheckBox checkbox_Language;

    /*******************************************************************/
    
    // so we are able to refresh data changes from outside the class object
    public MarksController() { marksController = this; }
    private static MarksController marksController = null;
    public static MarksController getController() { return marksController; }
    
    /*******************************************************************/
    
    private void initializeCheckBoxes() {
        this.checkAllCheckBoxes();
        
        checkbox_FirstName.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                col_FirstName.setVisible(true);
            } else {
                col_FirstName.setVisible(false);
            }
        });
        checkbox_LastName.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                col_LastName.setVisible(true);
            } else {
                col_LastName.setVisible(false);
            }
        });
        checkbox_Phone.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                col_Phone.setVisible(true);
            } else {
                col_Phone.setVisible(false);
            }
        });
        checkbox_Grade.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                col_Grade.setVisible(true);
            } else {
                col_Grade.setVisible(false);
            }
        });
        checkbox_Language.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                col_Language.setVisible(true);
            } else {
                col_Language.setVisible(false);
            }
        });
    }
    
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
        col_Mark.setCellValueFactory(new PropertyValueFactory<Student, Float>("mark"));
        
        // Handle double click event
        studentsTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                Student s = (Student) studentsTable.getSelectionModel().getSelectedItem();
                if (s != null) updateStudentMark(s);
            }
        });
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
    
    private void initializeComboBoxes() {
        // Add items to the `Language` ComboBox
        comboBox_Language.setItems(FXCollections.observableArrayList("Any", "English", "French"));
        comboBox_Language.setValue("Any");        

        // Add items to the `Grade` ComboBox
        comboBox_Grade.setItems(FXCollections.observableArrayList("Any", "8", "9", "10", "11", "12"));
        comboBox_Grade.setValue("Any");
        
        // set interactive filtering feature
        comboBox_Language.valueProperty().addListener((obs, oldValue, newValue) -> {
            Search();
        });
        comboBox_Grade.valueProperty().addListener((obs, oldValue, newValue) -> {
            Search();
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.initializeCheckBoxes();
        this.initializeTextFields();
        this.initializeComboBoxes();
        this.initializeTable();
        
        // initialize model
        try {
            model = StudentsModel.getModel();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        this.refresh();
    }
    
    /*******************************************************************/
    
        private void updateStudentMark(Student s) {
        try {
            // load resource
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpdateStudentMark.fxml"));
            Parent root = loader.load();

            // create window
            Stage stage = new Stage();
            stage.setScene(new Scene(root, root.prefWidth(-1), root.prefHeight(-1)));
            
            // Access the controller and set the data
            UpdateStudentMarkController controller = loader.getController();
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
        
    /*******************************************************************/
    
    /*
     * refresh data (stats, and table based on inputs)
     * public because it is used in `Controller` to make sure data is up to date with possible changes to the database, made by the previous page
     */
    public void refresh() {
        this.setStats();
        this.Search();
    }
    
    private void unCheckAllCheckBoxes() {
        checkbox_FirstName.setSelected(false);
        checkbox_LastName.setSelected(false);
        checkbox_Phone.setSelected(false);
        checkbox_Grade.setSelected(false);
        checkbox_Language.setSelected(false);
    }
    
    private void checkAllCheckBoxes() {
        checkbox_FirstName.setSelected(true);
        checkbox_LastName.setSelected(true);
        checkbox_Phone.setSelected(true);
        checkbox_Grade.setSelected(true);
        checkbox_Language.setSelected(true);
    }
    
    private void setStats() {
        try {
            label_TotalSubscriptions.setText(model.getNumberOfSubscriptions() + "");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    private void Search() {
        // extract data from input-fields
        String firstName = tf_FirstName.getText();
        String lastName = tf_LastName.getText();
        String phone = tf_Phone.getText();
        String grade =  (String) comboBox_Grade.getValue();
        String language = (String) comboBox_Language.getValue();

        // perform search
        try {
            List<Student> result = model.searchStudents(firstName, lastName, phone, grade, language, "Active");
            studentsTable.setItems(FXCollections.observableArrayList(result));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    /*******************************************************************/
}
