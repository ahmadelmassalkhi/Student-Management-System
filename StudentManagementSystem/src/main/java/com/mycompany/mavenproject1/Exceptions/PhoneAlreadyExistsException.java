/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Exceptions;

/**
 *
 * @author AHMAD
 */
public class PhoneAlreadyExistsException extends RuntimeException {
    public PhoneAlreadyExistsException() {
        super("Already exists a student with such phone number !");
    }
}
