/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Common;

/**
 *
 * @author AHMAD
 */
public class InputValidator {
    public static String inputErrorMessage(
            String firstName, 
            String lastName, 
            String phone, 
            String grade, 
            String language, 
            String subscription) {
        
        // make sure all fields were filled
        if(firstName.isEmpty() 
                || lastName.isEmpty() 
                || phone.isEmpty()
                || grade.equals("Any") 
                || language.equals("Any") 
                || subscription.equals("Any")) return "Please fill all fields !";

        return null;
    }
}
