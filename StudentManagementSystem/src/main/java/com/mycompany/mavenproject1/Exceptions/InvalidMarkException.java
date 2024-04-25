/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Exceptions;

/**
 *
 * @author AHMAD
 */
public class InvalidMarkException extends RuntimeException {
    public InvalidMarkException() {
        super("A Mark should be between 0 and 20 !");
    }
}
