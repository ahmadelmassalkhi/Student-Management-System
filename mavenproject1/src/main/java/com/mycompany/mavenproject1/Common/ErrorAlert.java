/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Common;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author AHMAD
 */
public class ErrorAlert extends Alert {
    
    public ErrorAlert(String title, String headerText, String contentText) {
        super(AlertType.ERROR); // Set AlertType.ERROR to get default functionality

        // set custom information
        setTitle(title);
        setHeaderText(headerText);
        setContentText(contentText);

        // Create custom dialog pane
        DialogPane dialogPane = getDialogPane();

        // Add custom icon
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons8-error-48.png")));
        dialogPane.setGraphic(icon);
    }
}
