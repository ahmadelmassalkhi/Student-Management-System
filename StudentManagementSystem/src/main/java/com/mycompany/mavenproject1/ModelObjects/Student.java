/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.ModelObjects;

import com.mycompany.mavenproject1.models.Model;

/**
 *
 * @author AHMAD
 */
public class Student {
    // ATTRIBUTES
    private Long id;
    private String fullName;
    private String phone;
    private String grade;
    private String language;
    private Float mark; // over 20
    private Subscription subscription;
    
    // CONSTRUCTORS
    public Student() {}
    
    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setLanguage(String language) { this.language = language; }
    public void setMark(Float mark) { this.mark = mark; }
    public void setSubscription(Subscription subscription) { this.subscription = subscription; }
    public void setMark(String mark) {
        if(mark == null) this.mark = null;
        else this.mark = Float.valueOf(mark);
    }
    
    // GETTERS
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getGrade() { return grade; }
    public String getLanguage() { return language; }
    public Subscription getSubscription() { return subscription; }
    public String getMark() {
        if(mark == null) return Model.NULL;
        return String.valueOf(mark);
    }
    
    // GETTERS USED FOR TableColumns
    public String getSubscriptionStatus() {
        if(subscription == null || subscription.getStatus() == null) return Subscription.INACTIVE_STRING;
        return subscription.getStatusString();
    }
    public String getSubscriptionDate() {
        if(subscription == null) return Model.NULL;
        return subscription.getDateStringFormatted();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", grade=" + grade +
                ", language='" + language + '\'' +
                ", subscription='" + subscription.toString() + '\'' +
                '}';
    }
}
