package com.mycompany.mavenproject1;

// imports from same package
import com.mycompany.mavenproject1.Exceptions.UserCancelledFileChooserException;
import com.mycompany.mavenproject1.Managers.ConfigurationManager;
import com.mycompany.mavenproject1.Managers.FileManager;
import com.mycompany.mavenproject1.MarksPage.MarksController;
import com.mycompany.mavenproject1.StudentsPage.StudentsController;
import java.io.FileNotFoundException;
import java.io.IOException;

// imports from javafx
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

// other imports
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

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
        initializeImageViews();
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

    /* PROFILE PICTURE */
    @FXML
    private ImageView img_Pfp;
    @FXML
    private ImageView img_EditPfp;
    
    private static final double PFP_SIZE = 80;
    private void setProfilePicture() {
        stackPane_Pfp.setMaxHeight(PFP_SIZE);
        stackPane_Pfp.setMaxWidth(PFP_SIZE);
        
        // Set image
        Image img = new Image(ConfigurationManager.getManager().getFilePath_ProfilePicture().toUri().toString());
        img_Pfp.setImage(img);

        // Use the smaller dimension for the size of the ImageView and the radius of the circle
        img_Pfp.setFitHeight(PFP_SIZE);
        img_Pfp.setFitWidth(PFP_SIZE);

        stackPane_Pfp.setClip(new Circle(PFP_SIZE / 2, PFP_SIZE / 2, PFP_SIZE / 2));
    }

    
    @FXML
    private StackPane stackPane_Pfp;
    
    private void initializeImageViews() {
        setProfilePicture();

        /* MODIFY ON MOUSE EVENTS */
        stackPane_Pfp.setOnMouseEntered((MouseEvent event) -> {
            img_Pfp.setOpacity(0.5); // Make the image grayish
            img_EditPfp.setOpacity(1);
        });
        stackPane_Pfp.setOnMouseExited((MouseEvent event) -> {
            // Restore the original appearance
            img_Pfp.setOpacity(1);
            img_EditPfp.setOpacity(0);
        });

        stackPane_Pfp.setOnMouseClicked((MouseEvent event) -> {
            chooseProfilePicture();
            event.consume(); // prevent other listeners from executing
        });

        // initially
        img_EditPfp.setOpacity(0);
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
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } finally {
            // reset original appearance
            img_Pfp.setOpacity(1);
            img_EditPfp.setOpacity(0);
        }
    }
      
    /*******************************************************************/
}
