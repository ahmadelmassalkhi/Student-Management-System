/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Exceptions;

/**
 *
 * @author AHMAD
 */
public class InvalidDatabaseSchemaException extends RuntimeException {
    public InvalidDatabaseSchemaException(String message) {
        super(message);
    }
    
    public InvalidDatabaseSchemaException() {
        super("Invalid database schema detected !");
    }
}
