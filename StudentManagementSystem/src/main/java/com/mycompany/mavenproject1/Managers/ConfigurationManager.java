/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author AHMAD
 */
public class ConfigurationManager {
    
    private static final String CONFIG_DIR_NAME = "config";
    private static final String DATABASES_DIR_NAME = "databases";
    private static final String APP_NAME = "StudentManagementSystem";
    
    private static Path getRootPath() {
        return Paths.get(System.getenv("APPDATA") + File.separator + APP_NAME);
    }
    
    /*******************************************************************/
    
    public static Path getConfigDirPath() {
        return getRootPath().resolve(CONFIG_DIR_NAME);
    }
    
    public static Path getDatabasesDirPath() {
        return getRootPath().resolve(DATABASES_DIR_NAME);
    }
    
    private static Path getDbConfigPath() throws IOException {
        // Construct the path to the config file
        return getConfigDirPath().resolve("DbConfig.txt");
    }
    
    /*******************************************************************/
    
    private static Path currentDatabasePath = null;
    public static Path getCurrentDatabasePath() throws IOException {
        if(currentDatabasePath != null) return currentDatabasePath;
        // Open file for reading
        BufferedReader reader = Files.newBufferedReader(getDbConfigPath());
   
        // read and return first line
        return (currentDatabasePath = Paths.get(reader.readLine().trim()));
    }
    
    protected static void updateDbConfigFile(File backupDbFile) throws IOException, URISyntaxException, FileNotFoundException {
        // make sure file exists
        if(backupDbFile == null || !backupDbFile.exists()) throw new FileNotFoundException();

        // Initialize BufferedWriter
        BufferedWriter writer = new BufferedWriter(new FileWriter(getDbConfigPath().toFile()));
        
        // set new path
        currentDatabasePath = backupDbFile.toPath();

        // Write the backupDb Path to the file
        writer.write(currentDatabasePath.toString());
        writer.flush();
    }
    
    /*******************************************************************/
}
