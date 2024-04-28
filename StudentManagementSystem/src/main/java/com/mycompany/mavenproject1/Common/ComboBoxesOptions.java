/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Common;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author AHMAD
 */
public class ComboBoxesOptions {
    public static final List<String> OPTIONS_GRADE = Arrays.asList("Any", "8", "Brevet", "10", "11", "Terminal SE", "Terminal LS", "Terminal GS", "Terminal LH");
    public static final List<String> OPTIONS_SUBSCRIPTION = Arrays.asList("Any", "Active", "InActive");
    public static final List<String> OPTIONS_COUNTRYCODES = CountryCodesManager.getCountryCodesList();
    public static final List<String> OPTIONS_LANGUAGE = Arrays.asList("Any", "English", "French");
    public static final List<String> OPTIONS_MARKSORDER = Arrays.asList("Any", "ASC", "DESC");

    public static final String OPTION_DEFAULT_GRADE = "Any";
    public static final String OPTION_DEFAULT_SUBSCRIPTION = "Any";
    public static final String OPTION_DEFAULT_COUNTRYCODE = "+961";
    public static final String OPTION_DEFAULT_LANGUAGE = "Any";
    public static final String OPTION_DEFAULT_MARKORDER = "DESC";
}
