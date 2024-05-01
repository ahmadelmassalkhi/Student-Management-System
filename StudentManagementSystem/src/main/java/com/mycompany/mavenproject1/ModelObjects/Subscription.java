/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.ModelObjects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    // GETTERS
    public Long getId() { return id; }
    public Boolean getStatus() { return status; }
    public LocalDate getDate() { return date; }

    // string getter for date (to insert date into database)
    public String getDateString() {
        if(date == null) return null; // leave null for nullable column
        return date.toString(); 
    }
    
    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", status=" + (status ? ACTIVE_STRING : INACTIVE_STRING) +
                ", date=" + date.format(DateTimeFormatter.ISO_DATE) +
                '}';
    }
}
