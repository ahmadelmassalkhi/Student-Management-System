/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.models;

/**
 *
 * @author AHMAD
 */
public class Student {
    // ATTRIBUTES
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private int grade;
    private String language;
    private int subscriptionStatus;
    
    // CONSTRUCTORS
    public Student() {}
    
    // SETTERS
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setGrade(int grade) { this.grade = grade; }
    public void setLanguage(String language) { this.language = language; }
    public void setSubscriptionStatus(int subscribtionStatus) { this.subscriptionStatus = subscribtionStatus; }
    public void setId(int id) { this.id = id; }
    
    // GETTERS
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getLanguage() { return language; }
    public int getGrade() { return grade; }
    
    // official getter (uses naming convension)
    public String getSubscriptionStatus() { return Student.getSubscriptionStatusString(subscriptionStatus); }
    
    // to inject into database
    public int getSubscriptionStatusInt() { return subscriptionStatus; }
    
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", grade=" + grade +
                ", language='" + language + '\'' +
                ", subscriptionStatus='" + subscriptionStatus + '\'' +
                '}';
    }
    
    public static String getSubscriptionStatusString(int status) {
        return (status == 1) ? "Active":"InActive" ;
    }
    public static int getSubscriptionStatusInt(String status) {
        return status.equalsIgnoreCase("active") ? 1 : 0 ;
    }
}
