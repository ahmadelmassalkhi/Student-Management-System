/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.ViewsInitializers;

import com.mycompany.mavenproject1.App;
import com.mycompany.mavenproject1.ModelObjects.Subscription;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

/**
 *
 * @author AHMAD
 */
public class ComboBoxInitializer {
    
    /* GRADE OPTIONS */
    public static final String OPTION_GRADE_8 = "8";
    public static final String OPTION_GRADE_9 = "Brevet";
    public static final String OPTION_GRADE_10 = "10";
    public static final String OPTION_GRADE_11 = "11";
    public static final String OPTION_GRADE_12 = "Terminal";
    public static final String OPTION_GRADE_12_LS = OPTION_GRADE_12 + " LS";
    public static final String OPTION_GRADE_12_GS = OPTION_GRADE_12 + " GS";
    public static final String OPTION_GRADE_12_SE = OPTION_GRADE_12 + " SE";
    public static final String OPTION_GRADE_12_LH = OPTION_GRADE_12 + " LH";
    
    /* SUBSCRIPTION OPTIONS */
    public static final String OPTION_SUBSCRIPTION_ACTIVE = Subscription.ACTIVE_STRING;
    public static final String OPTION_SUBSCRIPTION_INACTIVE = Subscription.INACTIVE_STRING;

    /* LANGUAGE OPTIONS */
    public static final String OPTION_LANGUAGE_ENGLISH = "English";
    public static final String OPTION_LANGUAGE_FRENCH = "French";
    
    /* MARKSORDER OPTIONS */
    public static final String OPTION_MARKSORDER_ASC = "ASC";
    public static final String OPTION_MARKSORDER_DESC = "DESC";
    
    /*******************************************************************/
    
    /* DEFAULT OPTIONS */
    public static final String ANY = "Any";
    public static final String OPTION_DEFAULT_LANGUAGE = ANY;
    public static final String OPTION_DEFAULT_GRADE = ANY;
    public static final String OPTION_DEFAULT_SUBSCRIPTION = ANY;
    public static final String OPTION_DEFAULT_MARKORDER = OPTION_MARKSORDER_DESC;
    public static final String OPTION_DEFAULT_COUNTRYCODE = "+961";
    
    /* OPTIONS */
    public static final List<String> OPTIONS_GRADE = Arrays.asList("Any", OPTION_GRADE_8, OPTION_GRADE_9, OPTION_GRADE_10, OPTION_GRADE_11, OPTION_GRADE_12, OPTION_GRADE_12_SE, OPTION_GRADE_12_LS, OPTION_GRADE_12_GS, OPTION_GRADE_12_LH);
    public static final List<String> OPTIONS_SUBSCRIPTION = Arrays.asList("Any", OPTION_SUBSCRIPTION_ACTIVE, OPTION_SUBSCRIPTION_INACTIVE);
    public static final List<String> OPTIONS_LANGUAGE = Arrays.asList("Any", OPTION_LANGUAGE_ENGLISH, OPTION_LANGUAGE_FRENCH);
    public static final List<String> OPTIONS_MARKSORDER = Arrays.asList("Any", OPTION_MARKSORDER_ASC, OPTION_MARKSORDER_DESC);
    public static final List<String> OPTIONS_COUNTRYCODES = getCountryCodes();

    /* OPTIONS AS OBSERVABLE LIST */
    public static final ObservableList<String> OPTIONS_OBSERVABLE_LANGUAGE = FXCollections.observableArrayList(OPTIONS_LANGUAGE);
    public static final ObservableList<String> OPTIONS_OBSERVABLE_GRADE = FXCollections.observableArrayList(OPTIONS_GRADE);
    public static final ObservableList<String> OPTIONS_OBSERVABLE_MARKSORDER = FXCollections.observableArrayList(OPTIONS_MARKSORDER);
    public static final ObservableList<String> OPTIONS_OBSERVABLE_COUNTRYCODE = FXCollections.observableArrayList(OPTIONS_COUNTRYCODES);
    public static final ObservableList<String> OPTIONS_OBSERVABLE_SUBSCRIPTION = FXCollections.observableArrayList(OPTIONS_SUBSCRIPTION);
    
    /*******************************************************************/
    
    /* INITIALIZE METHODS */
    public static void Initialize_Grades(ComboBox comboBox_Grade) { Initialize(null, comboBox_Grade, null, null, null); }
    public static void Initialize_Languages(ComboBox comboBox_Language) { Initialize(comboBox_Language, null, null, null, null); }
    public static void Initialize_CountryCodes(ComboBox comboBox_CountryCode) { Initialize(null, null, comboBox_CountryCode, null, null); }
    public static void Initialize_SubscriptionStatus(ComboBox comboBox_SubscriptionStatus) { Initialize(null, null, null, comboBox_SubscriptionStatus, null); }
    public static void Initialize_MarksOrder(ComboBox comboBox_MarksOrder) { Initialize(null, null, null, null, comboBox_MarksOrder); }
    public static void Initialize(
            ComboBox comboBox_Language,
            ComboBox comboBox_Grade,
            ComboBox comboBox_CountryCode,
            ComboBox comboBox_SubscriptionStatus,
            ComboBox comboBox_MarksOrder) {

        // Add items to the `Language` ComboBox
        if(comboBox_Language != null) {
            comboBox_Language.setItems(OPTIONS_OBSERVABLE_LANGUAGE);
            comboBox_Language.setValue(OPTION_DEFAULT_LANGUAGE);
        }
        
        // Add items to the `Grade` ComboBox
        if(comboBox_Grade != null) {
            comboBox_Grade.setItems(OPTIONS_OBSERVABLE_GRADE);
            comboBox_Grade.setValue(OPTION_DEFAULT_GRADE);
        }
        
        // Add items to the `SubscriptionStatus` ComboBox
        if(comboBox_SubscriptionStatus != null) {
            comboBox_SubscriptionStatus.setItems(OPTIONS_OBSERVABLE_SUBSCRIPTION);
            comboBox_SubscriptionStatus.setValue(OPTION_DEFAULT_SUBSCRIPTION);
        }
        
        // Add items to the `MarksOrder` ComboBox
        if(comboBox_MarksOrder != null) {
            comboBox_MarksOrder.setItems(OPTIONS_OBSERVABLE_MARKSORDER);
            comboBox_MarksOrder.setValue(OPTION_DEFAULT_MARKORDER);
        }
        
        // Add items to the `Code` ComboBox
        if(comboBox_CountryCode != null) {
            comboBox_CountryCode.setItems(OPTIONS_OBSERVABLE_COUNTRYCODE);
            comboBox_CountryCode.setValue(OPTION_DEFAULT_COUNTRYCODE);

            // set ComboBox search on key-press feature
            comboBox_CountryCode.setOnKeyPressed(event -> {
                String filter = event.getText();
                ObservableList<String> filteredList = FXCollections.observableArrayList();
                for (String country : OPTIONS_OBSERVABLE_COUNTRYCODE) {
                    if (country.toLowerCase().startsWith(filter.toLowerCase())) {
                        filteredList.add(country);
                    }
                }
                comboBox_CountryCode.setItems(filteredList);
            });

            // show on selected, only the country-code of the country-string
            comboBox_CountryCode.setButtonCell(new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(extractCountryCode(item));
                    }
                }
            });
        }
    }
    
    /*******************************************************************/
    /* COUNTRY CODES OPERATIONS */
    
    public static List<String> getCountryCodes() {
        // get resource
        URL resourceURL = App.class.getResource("/txt/countryCodes.txt");
        // open resource
        if (resourceURL != null) {
            try (
                    InputStream inputStream = resourceURL.openStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
                ) {
                String line;
                List<String> countryCodes = new ArrayList<>();

                // extract data from file
                while ((line = bufferedReader.readLine()) != null) countryCodes.add(line);
                
                // return as observable list
                return countryCodes;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        return new ArrayList<>();
    }
    
    public static String extractCountryCode(String phone) throws NullPointerException {
        if(phone == null) throw new NullPointerException();
        int indexOfPlus = phone.indexOf("+");
        if(indexOfPlus == -1) throw new IllegalArgumentException(); // "+" doesn't exist in the string
        
        // Find the first space after "+"
        int indexOfSpace = phone.indexOf(" ", indexOfPlus);
        if(indexOfSpace == -1) return phone.substring(indexOfPlus); // Extract from "+" to the end
        return phone.substring(indexOfPlus, indexOfSpace); // Extract the substring from "+" to the space
    }
    
    public static String extractNumber(String phone) throws NullPointerException, IllegalArgumentException {
        if(phone == null) throw new NullPointerException();
        int lastSpace = phone.lastIndexOf(" ");
        if(lastSpace == -1) throw new IllegalArgumentException();
        return phone.substring(lastSpace + 1); // Extract the substring from the last space to the end
    }

    /*******************************************************************/
}
