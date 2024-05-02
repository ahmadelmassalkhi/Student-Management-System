/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Managers;

import com.mycompany.mavenproject1.Exceptions.InvalidFileExtensionException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author AHMAD
 */
public class ConfigurationManager {

    private static final String DIR_NAME_CONFIG = "config";
    private static final String DIR_NAME_DATABASES = "databases";
    private static final String DIR_NAME_APP = "StudentManagementSystem";
    private static final String FILE_NAME_PFP = "profile-picture";
    private static final String FILE_NAME_DBCONFIG = "DbConfig.txt";
    private static final String FILE_NAME_DEFAULTDB = "sqlite.db";
    private static final String FILE_NAME_OWNER = "OwnerName.txt";
    private static final String DEFAULT_NAME_OWNER = "Owner";
    
    private ConfigurationManager() {
        this.repairConfiguration();
    }
    
    private static ConfigurationManager manager = null;
    public static ConfigurationManager getManager() {
        if(manager == null) manager = new ConfigurationManager();
        return manager;
    }
    
    private void repairConfiguration() {
        try {
            if(!Files.exists(this.getDirPath_Root())) Files.createDirectory(this.getDirPath_Root());
            if(!Files.exists(this.getDirPath_Config())) Files.createDirectory(this.getDirPath_Config());
            if(!Files.exists(this.getDirPath_Databases())) Files.createDirectory(this.getDirPath_Databases());
            if(!Files.exists(this.getFilePath_DbConfig())) {
                Files.createFile(this.getFilePath_DbConfig());
                Path defaultDbPath = getDirPath_Databases().resolve(FILE_NAME_DEFAULTDB);
                Files.write(this.getFilePath_DbConfig(), defaultDbPath.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            }
            if(!Files.exists(this.getFilePath_OwnerName())) {
                Files.createFile(this.getFilePath_OwnerName());
                Files.write(this.getFilePath_OwnerName(), DEFAULT_NAME_OWNER.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException ex) {
            System.err.print("Failed to create/repair configuration files: " + ex.getMessage());
            System.exit(1);
        }
    }
    
    /*******************************************************************/
    // PUBLIC PATH GETTERS
    
    public Path getDirPath_Root() { return Paths.get(System.getenv("APPDATA") + File.separator + DIR_NAME_APP); }
    public Path getDirPath_Config() { return getDirPath_Root().resolve(DIR_NAME_CONFIG); }
    public Path getDirPath_Databases() { return getDirPath_Root().resolve(DIR_NAME_DATABASES); }

    public Path getFilePath_ProfilePicture() { return getDirPath_Root().resolve(FILE_NAME_PFP); }
    public Path getFilePath_DbConfig() { return getDirPath_Config().resolve(FILE_NAME_DBCONFIG); }
    public Path getFilePath_OwnerName() { return getDirPath_Config().resolve(FILE_NAME_OWNER); }

    /*******************************************************************/
    // CONFIGURATION RELATED-OPERATIONS
    
    private Path currentDatabasePath = null;
    public Path getCurrentDatabasePath() throws IOException {
        if(currentDatabasePath != null) return currentDatabasePath;
        
        BufferedReader reader;
        try {
            // Open file for reading
            reader = Files.newBufferedReader(getFilePath_DbConfig());
        } catch (IOException ex) { // getDbConfigPath() returned a path to a file that doesn't exist
            // repair
            this.repairConfiguration();
            // proceed
            reader = Files.newBufferedReader(getFilePath_DbConfig());
        }
        
        // read and return first line
        return (currentDatabasePath = Paths.get(reader.readLine().trim()));
    }
    
    public void updateDbConfigFile(Path backupDbPath) throws IOException, FileNotFoundException, InvalidFileExtensionException, IllegalArgumentException {
        // check if file exists
        if(!Files.exists(backupDbPath)) throw new FileNotFoundException();
        // validate file extension
        if(!FileManager.isPath_Db(backupDbPath)) throw new InvalidFileExtensionException(String.format("File `%s` is not recognized as a valid `%s` file !", backupDbPath.getFileName().toString(), FileManager.FILE_EXTENSION_DB));

        BufferedWriter writer;
        try {
            // Initialize BufferedWriter
            writer = Files.newBufferedWriter(getFilePath_DbConfig());
        } catch (IOException ex) {
            // getDbConfigPath() returned a path to a file that doesn't exist
            this.repairConfiguration();
            writer = Files.newBufferedWriter(getFilePath_DbConfig());
        }
        
        // Write the backupDb Path to the file
        writer.write(backupDbPath.toString());
        writer.flush();
        writer.close();
        
        // set new path
        currentDatabasePath = backupDbPath;
    }
    
    public void updateProfilePicture(Path picture) throws FileNotFoundException, IOException, IllegalArgumentException {
        // check if file exists
        if(picture == null || !Files.exists(picture)) throw new FileNotFoundException(
                String.format("File at `%s` was not found", picture)
        );
        
        // validate file extension
        if(!FileManager.isPath_AnImage(picture)) throw new InvalidFileExtensionException(String.format("File `%s` is not recognized as a valid image file !", picture.getFileName().toString()));
        
        // copy picture into profile-picture path
        FileManager.copyFile(picture, getFilePath_ProfilePicture());
    }
    
    /*******************************************************************/
    
    private String ownerName = null;
    public String getOwnerName() throws IOException {
        if(ownerName != null) return ownerName;
        
        BufferedReader reader;
        try {
            // Open file for reading
            reader = Files.newBufferedReader(getFilePath_OwnerName());
        } catch (IOException ex) {
            // parent directory doesn't exist
            // repair
            this.repairConfiguration();
            // proceed
            reader = Files.newBufferedReader(getFilePath_OwnerName());
        }
        
        // extract and validate
        String extractedName = reader.readLine();
        if(extractedName == null || extractedName.trim().isEmpty()) {
            this.updateOwnerName(DEFAULT_NAME_OWNER);
            this.ownerName = DEFAULT_NAME_OWNER;
        }
   
        return (ownerName = extractedName);
    }
    
    public void updateOwnerName(String OwnerName) throws IOException, IllegalArgumentException {
        if(OwnerName == null || OwnerName.isEmpty()) throw new IllegalArgumentException("Owner name must not be null or empty !");

        BufferedWriter writer;
        try {
            // Initialize BufferedWriter
            writer = Files.newBufferedWriter(getFilePath_OwnerName());
        } catch (IOException ex) {
            // parent directory doesn't exist
            // repair
            this.repairConfiguration();
            // proceed
            writer = Files.newBufferedWriter(getFilePath_OwnerName());
        }
        
        // Write the new owner name to the file
        writer.write(OwnerName);
        writer.flush();
        writer.close();
        
        // set it to cache
        ownerName = OwnerName;
    }
    
    /*******************************************************************/
}
