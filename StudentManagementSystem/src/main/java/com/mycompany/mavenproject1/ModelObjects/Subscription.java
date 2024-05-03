/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.ModelObjects;

import com.mycompany.mavenproject1.models.Model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author AHMAD
 */
public class Subscription {
    
    // CONSTANTS
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;
    public static final String ACTIVE_STRING = "Active";
    public static final String INACTIVE_STRING = "InActive";
    public static final String FORMAT_EXPIRATION_DATE = "MM/dd/yyyy";

    // ATTRIBUTES
    private Long id;
    private Boolean status;
    private LocalDate date; // expiration date
    
    // CONSTRUCTOR
    public Subscription() {}

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setStatus(Boolean status) { this.status = status; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setDate(String date) throws DateTimeParseException {
        if(date == null || date.equalsIgnoreCase(Model.NULL)) this.date = null;
        else {
            this.date = LocalDate.parse(date);
            this.date.format(DateTimeFormatter.ofPattern(FORMAT_EXPIRATION_DATE));
        }
    }

    // GETTERS
    public Long getId() { return id; }
    public Boolean getStatus() { return status; }
    public LocalDate getDate() { return date; }
    
    public String getStatusString() { return status ? ACTIVE_STRING : INACTIVE_STRING; }

    // string getter for date (to insert date into database)
    public String getDateString() {
        if(date == null) return Model.NULL; // null for nullable column
        return date.toString();
    }
    // string getter to format date into application's UI requirements
    public String getDateStringFormatted() throws DateTimeParseException {
        if(date == null) return Model.NULL; // null for nullable column
        return date.format(DateTimeFormatter.ofPattern(FORMAT_EXPIRATION_DATE));
    }
    
    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", status=" + this.getStatusString() +
                ", date=" + this.getDateString() +
                '}';
    }
}
