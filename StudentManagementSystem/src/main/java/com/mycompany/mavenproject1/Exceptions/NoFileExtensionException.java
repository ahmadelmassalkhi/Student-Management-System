/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Exceptions;

/**
 *
 * @author AHMAD
 */
public class NoFileExtensionException extends RuntimeException {
    public NoFileExtensionException() {
        super("File has no extension !");
    }
    
    public NoFileExtensionException(String message) {
        super(message);
    }
}
