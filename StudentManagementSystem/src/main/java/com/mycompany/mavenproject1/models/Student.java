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
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String grade;
    private String language;
    private Float mark; // over 20
    private Subscription subscription;
    
    // CONSTRUCTORS
    public Student() {}
    
    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setLanguage(String language) { this.language = language; }
    public void setMark(Float mark) { this.mark = mark; }
    public void setSubscription(Subscription subscription) { this.subscription = subscription; }
    
    // GETTERS
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getGrade() { return grade; }
    public String getLanguage() { return language; }
    public Float getMark() { return mark; }
    public Subscription getSubscription() { return subscription; }
    
    public String getSubscriptionStatus() {
        if(subscription == null || subscription.getStatus() == null) return Subscription.INACTIVE_STRING;
        return subscription.getStatus() ? Subscription.ACTIVE_STRING : Subscription.INACTIVE_STRING;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", grade=" + grade +
                ", language='" + language + '\'' +
                ", subscription='" + subscription.toString() + '\'' +
                '}';
    }
}
