/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Common;

// imports from same package
import com.mycompany.mavenproject1.App;

// imports from javafx
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// other imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AHMAD
 */
public class CountryCodesManager {
    
    public static String getCountryCode(String phone) throws NullPointerException {
        if(phone == null) throw new NullPointerException();
        int indexOfPlus = phone.indexOf("+");
        if(indexOfPlus == -1) throw new IllegalArgumentException(); // "+" doesn't exist in the string
        
        // Find the first space after "+"
        int indexOfSpace = phone.indexOf(" ", indexOfPlus);
        if(indexOfSpace == -1) return phone.substring(indexOfPlus); // Extract from "+" to the end
        return phone.substring(indexOfPlus, indexOfSpace); // Extract the substring from "+" to the space
    }

    public static String getNumber(String phone) throws NullPointerException, IllegalArgumentException {
        if(phone == null) throw new NullPointerException();
        int lastSpace = phone.lastIndexOf(" ");
        if(lastSpace == -1) throw new IllegalArgumentException();
        return phone.substring(lastSpace + 1); // Extract the substring from the last space to the end
    }
    
    public static final ObservableList<String> countryCodesObservableList = getCountryCodesObservableList();
    public static ObservableList<String> getCountryCodesObservableList() {
        if(countryCodesObservableList != null) return countryCodesObservableList;
        
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
                return FXCollections.observableArrayList(countryCodes);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        
        return FXCollections.observableArrayList();
    }
}
