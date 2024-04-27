/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Common;

import com.mycompany.mavenproject1.Exceptions.MissingInputFieldException;

/**
 *
 * @author AHMAD
 */
public class InputValidatorForStudentFields {
    
    private static boolean validFirstName(String firstName) { return firstName != null && !firstName.isEmpty(); }
    private static boolean validLastName(String lastName) { return lastName != null && !lastName.isEmpty(); }
    private static boolean validPhone(String phone) { return phone != null && !phone.isEmpty(); }
    private static boolean validGrade(String grade) { return grade != null && !grade.isEmpty() && !grade.equalsIgnoreCase("any"); }
    private static boolean validLanguage(String language) { return language != null && !language.isEmpty() && !language.equalsIgnoreCase("any"); }
    private static boolean validSubscription(String subscription) { return subscription != null && !subscription.isEmpty() && !subscription.equalsIgnoreCase("any"); }
    private static boolean validCountryCode(String countryCode) { return countryCode != null && !countryCode.isEmpty(); }
    
    public static void validateAddFields(
            String firstName, 
            String lastName, 
            String phone, 
            String grade, 
            String language, 
            String subscription,
            String countryCode) throws MissingInputFieldException {

        // validate
        if(!validFirstName(firstName)
                || !validLastName(lastName) 
                || !validPhone(phone) 
                || !validGrade(grade) 
                || !validLanguage(language) 
                || !validSubscription(subscription) 
                || !validCountryCode(countryCode)) throw new MissingInputFieldException();
    }
    
    public static void validateUpdateFields(
            String firstName, 
            String lastName, 
            String phone, 
            String grade, 
            String language, 
            String subscription,
            String countryCode) throws MissingInputFieldException {
        InputValidatorForStudentFields.validateAddFields(firstName, lastName, phone, grade, language, subscription, countryCode);
    }
    
    public static void validateSearchFields(
            String firstName, 
            String lastName, 
            String phone, 
            String grade, 
            String language, 
            String subscription,
            String countryCode) throws MissingInputFieldException {
        
        // validate
        if(firstName == null
                || lastName == null
                || phone == null
                || grade == null
                || language == null
                || subscription == null
                || countryCode == null) throw new MissingInputFieldException();
    }
}
