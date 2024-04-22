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
    private String subscriptionStatus;
    
    // CONSTRUCTORS
    public Student() {}
    public Student(String firstName, String lastName, String phone, int grade, String language, String subscriptionStatus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.grade = grade;
        this.language = language;
        this.subscriptionStatus = subscriptionStatus;
    }
    
    // SETTERS
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setGrade(int grade) { this.grade = grade; }
    public void setLanguage(String language) { this.language = language; }
    public void setSubscriptionStatus(String subscribtionStatus) { this.subscriptionStatus = subscribtionStatus; }
    public void setId(int id) { this.id = id; }
    
    // GETTERS
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getPhone() { return this.phone; }
    public String getLanguage() { return this.language; }
    public int getGrade() { return this.grade; }
    public String getSubscriptionStatus() { return this.subscriptionStatus; }
    public int getId() { return this.id; }
    
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
}
