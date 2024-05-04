/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Controllers;

// imports from same project
import com.mycompany.mavenproject1.Exceptions.InvalidDatabaseSchemaException;
import com.mycompany.mavenproject1.Exceptions.UserCancelledFileChooserException;
import com.mycompany.mavenproject1.Managers.DatabaseManager;

// imports from javafx
import javafx.fxml.Initializable;

// other imports
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author AHMAD
 */
public class SettingsController implements Initializable {
    
    private static Stage stage = null;
    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
        try {
            DatabaseManager.getManager().setStage(stage);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showCurrentDatabase();
    }
    
    @FXML
    private Label label_DatabaseName;
    private void showCurrentDatabase() {
        try {
            label_DatabaseName.setText(DatabaseManager.getManager().getCurrentDatabaseName());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    /*******************************************************************/
    // OPERATIONS
    
    public void BackupDatabase() {
        try {
            DatabaseManager.getManager().Backup();
        } catch (UserCancelledFileChooserException ex) {
            // do nothing
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    public void RestoreDatabase() {
        try {
            DatabaseManager.getManager().Restore();
            this.showCurrentDatabase();
        } catch (UserCancelledFileChooserException ex) {
            // do nothing
        } catch (InvalidDatabaseSchemaException | IOException | SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    public void CleanDatabase() {
        try {
            // load resource
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CleanDatabase.fxml"));
            Parent root = loader.load();
            
            // create window
            Stage stage = new Stage();
            stage.setScene(new Scene(root, root.prefWidth(-1), root.prefHeight(-1)));

            // Center stage on screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - root.prefWidth(-1)) / 2);
            stage.setY((screenBounds.getHeight() - root.prefHeight(-1)) / 2);

            // Access the controller and set the data
            CleanDatabaseController controller = loader.getController();
            controller.setWindowInformation(stage, root);
            
            // wait until student is successfully updated or cancelled
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
