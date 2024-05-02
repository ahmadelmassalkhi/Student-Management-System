package com.mycompany.mavenproject1.Controllers;

// imports from same project
import com.mycompany.mavenproject1.Exceptions.UserCancelledFileChooserException;
import com.mycompany.mavenproject1.Managers.ConfigurationManager;
import com.mycompany.mavenproject1.Managers.FileManager;

// imports from javafx
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

// other imports
import java.net.URL;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Screen;

public class BaseController implements Initializable {
    /*******************************************************************/
    // PANE NAVIGATION
    
    /* BUTTONS */
    @FXML
    private Button button_Students;
    @FXML
    private Button button_Marks;
    @FXML
    private Button button_Settings;
    @FXML
    private Button button_Exit;
    
    /* PANES */
    @FXML
    private AnchorPane anchorPane_Students;
    @FXML
    private AnchorPane anchorPane_Settings;
    @FXML
    private AnchorPane anchorPane_Marks;
    
    /*******************************************************************/
    
    private Stage stage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initially, set `Students` Pane
        anchorPane_Students.toFront();

        // initialize properties
        initializeProfilePicture();
        initializeOwnerName();
        Platform.runLater(() -> {
            stage = (Stage) button_Exit.getScene().getWindow();
        });
    }
    
    /*******************************************************************/

    public void handleSidebarClicks(ActionEvent actionEvent) {
        
        // handle clicks from `Students` button
        if (actionEvent.getSource() == button_Students) {
            StudentsController.getController().refresh(); // update data incase other pages made changes to the database
            anchorPane_Students.toFront();
        }

        // handle clicks from `Marks` button
        if(actionEvent.getSource() == button_Marks) {
            MarksController.getController().refresh(); // update data incase other pages made changes to the database
            anchorPane_Marks.toFront();
        }
        
        // handle clicks from `Settings` button
        if(actionEvent.getSource() == button_Settings) {
            SettingsController.setStage(stage);
            anchorPane_Settings.toFront();
        }
        
        // handle clicks from `Signout` button
        if(actionEvent.getSource() == button_Exit) {
            System.out.println("Closing the application !");
            stage.close();
        }
    }
    
    /*******************************************************************/

    /* PROFILE PICTURE VIEWS */
    @FXML
    private StackPane stackPane_Pfp;
    @FXML
    private ImageView img_Pfp;
    @FXML
    private ImageView img_EditPfp;
    
    private Path getImagePath() {
        return ConfigurationManager.getManager().getFilePath_ProfilePicture();
    }
    private static final double PFP_SIZE = 80;
    
    /*******************************************************************/
    
    private void initializeProfilePicture() {
        /* INITIALIZE DIMENSIONS */
        stackPane_Pfp.setMaxHeight(PFP_SIZE);
        stackPane_Pfp.setMaxWidth(PFP_SIZE);
        img_Pfp.setFitHeight(PFP_SIZE);
        img_Pfp.setFitWidth(PFP_SIZE);

        /* MODIFY ON MOUSE EVENTS */
        stackPane_Pfp.setOnMouseEntered((MouseEvent event) -> {
            showEditIcon();
        });
        stackPane_Pfp.setOnMouseExited((MouseEvent event) -> {
            if(Files.exists(getImagePath())) hideEditIcon();
            else showEditIcon();
        });
        stackPane_Pfp.setOnMouseClicked((MouseEvent event) -> {
            chooseProfilePicture();
            event.consume();
        });
        
        /* SET */
        setProfilePicture();
    }
    private void showEditIcon() {
        img_Pfp.setOpacity(0.5); // Make the image grayish
        img_EditPfp.setOpacity(1); // show edit icon
    }
    private void hideEditIcon() {
        // Restore the original appearance
        img_Pfp.setOpacity(1); // back to normal
        img_EditPfp.setOpacity(0); // hide edit icon
    }
    private void setProfilePicture() {
        // get image path
        Path path = getImagePath();
        
        // set appearance
        if(Files.exists(path)) hideEditIcon();
        else showEditIcon();
        
        // Set image
        img_Pfp.setImage(new Image(path.toUri().toString()));
        stackPane_Pfp.setClip(new Circle(PFP_SIZE / 2, PFP_SIZE / 2, PFP_SIZE / 2));
    }
    
    public void chooseProfilePicture() {
        try {
            // get and store new picture
            ConfigurationManager.getManager().updateProfilePicture(
                    FileManager.chooseOpenPathOf_Image(stage)
            );
            // update pfp
            setProfilePicture();
        } catch (UserCancelledFileChooserException ex) {
            // user cancelled (image file was not found/chosen)
        } catch (IOException | IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } finally {
            // reset original appearance
            img_Pfp.setOpacity(1);
            img_EditPfp.setOpacity(0);
        }
    }
    
    /*******************************************************************/
    
    @FXML
    private Label label_OwnerName;
    @FXML
    private ImageView img_EditOwnerName;
    @FXML
    private StackPane stackPane_OwnerName;
    
    private void initializeOwnerName() {
        
        // hidden initially
        img_EditOwnerName.setVisible(false);
        
        /* MODIFY ON MOUSE EVENTS */
        stackPane_OwnerName.setOnMouseEntered((MouseEvent event) -> {
            img_EditOwnerName.setVisible(true);
        });
        stackPane_OwnerName.setOnMouseExited((MouseEvent event) -> {
            img_EditOwnerName.setVisible(false);
        });
        stackPane_OwnerName.setOnMouseClicked((MouseEvent event) -> {
            UpdateOwnerName();
        });
        
        // set
        setOwnerName();
    }
    private void setOwnerName() {
        // set stored owner name
        try {
            this.label_OwnerName.setText(ConfigurationManager.getManager().getOwnerName());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    private void UpdateOwnerName() {
        try {
            // load resource
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpdateOwnerName.fxml"));
            Parent root = loader.load();
            
            // create window
            Stage stage = new Stage();
            stage.setScene(new Scene(root, root.prefWidth(-1), root.prefHeight(-1)));
            
            // Center stage on screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - root.prefWidth(-1)) / 2);
            stage.setY((screenBounds.getHeight() - root.prefHeight(-1)) / 2);

            // Access the controller and set the data
            UpdateOwnerNameController controller = loader.getController();
            controller.setOwnerName(this.label_OwnerName.getText());
            controller.setWindowInformation(stage, root);
            
            // wait until student is successfully updated or cancelled
            stage.showAndWait();
            
            // set new owner name
            setOwnerName();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
