package com.mycompany.mavenproject1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.StageStyle;

/**
 * JavaFX App
 */
public class App extends Application {

    private double x, y;
    
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        Parent root = loadFXML("Base");
        Scene scene = new Scene(root, root.prefWidth(-1), root.prefHeight(-1));
        stage.setScene(scene);

        // set stage borderless
        stage.initStyle(StageStyle.UNDECORATED);

        // make it draggable
        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });

        // Center stage on screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - root.prefWidth(-1)) / 2);
        stage.setY((screenBounds.getHeight() - root.prefHeight(-1)) / 2);

        stage.show();
    }


    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}