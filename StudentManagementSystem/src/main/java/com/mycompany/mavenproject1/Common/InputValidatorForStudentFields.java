/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Common;

import com.mycompany.mavenproject1.Exceptions.MissingInputFieldException;
import com.mycompany.mavenproject1.ModelObjects.Subscription;

/**
 *
 * @author AHMAD
 */
public class InputValidatorForStudentFields {
    
    private static boolean validFullName(String fullName) { return fullName != null && !fullName.isEmpty(); }
    private static boolean validPhone(String phone) { return phone != null && !phone.isEmpty(); }
    private static boolean validGrade(String grade) { return grade != null && !grade.isEmpty() && !grade.equalsIgnoreCase("any"); }
    private static boolean validLanguage(String language) { return language != null && !language.isEmpty() && !language.equalsIgnoreCase("any"); }
    private static boolean validCountryCode(String countryCode) { return countryCode != null && !countryCode.isEmpty(); }
    private static boolean validSubscriptionStatus(String status) { return status != null && !status.isEmpty() && !status.equalsIgnoreCase("any"); }
    private static boolean validSubscription(Subscription s) { return !(s.getStatus() && s.getDate() == null); }
    
    public static void validateAddFields(
            String fullName, 
            String phone, 
            String grade, 
            String language, 
            String subscriptionStatus,
            String countryCode) throws MissingInputFieldException {

        // validate
        if(!validFullName(fullName)
                || !validPhone(phone) 
                || !validGrade(grade) 
                || !validLanguage(language) 
                || !validSubscriptionStatus(subscriptionStatus) 
                || !validCountryCode(countryCode)) throw new MissingInputFieldException();
    }
    
    public static void validateUpdateFields(
            String fullName, 
            String phone, 
            String countryCode,
            Subscription subscription) throws MissingInputFieldException {
        
        // validate
        if(!validFullName(fullName)
                || !validPhone(phone) 
                || !validCountryCode(countryCode)
                || !validSubscription(subscription)) throw new MissingInputFieldException();
        
        // doesn't have to check other property as they are enforced within the UI
    }
    
    public static void validateSearchFields(
            String fullName, 
            String phone, 
            String grade, 
            String language, 
            String subscription,
            String countryCode) throws MissingInputFieldException {
        
        // validate
        if(fullName == null
                || phone == null
                || grade == null
                || language == null
                || subscription == null
                || countryCode == null) throw new MissingInputFieldException();
    }
}
