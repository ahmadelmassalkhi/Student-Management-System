/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Exceptions;

/**
 *
 * @author AHMAD
 */
public class UserCancelledFileChooserException extends RuntimeException {
    public UserCancelledFileChooserException() {
        super("User cancelled FileChooser, take action !");
    }
    
    public UserCancelledFileChooserException(String message) {
        super(message);
    }
}
