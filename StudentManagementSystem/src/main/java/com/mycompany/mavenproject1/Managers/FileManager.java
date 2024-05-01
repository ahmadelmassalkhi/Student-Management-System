/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Managers;

import com.mycompany.mavenproject1.Exceptions.NoFileExtensionException;
import com.mycompany.mavenproject1.Exceptions.UserCancelledFileChooserException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author AHMAD
 */
public class FileManager {
    
    public static final String FILE_EXTENSION_DB = ".db";
    public static final String FILE_DESCRIPTION_DB = String.format("Database Files (*%s)", FILE_EXTENSION_DB);
    
    public static final String FILE_EXTENSION_PDF = ".pdf";
    public static final String FILE_DESCRIPTION_PDF = String.format("PDF files (*%s)", FILE_EXTENSION_PDF);
    
    public static final String FILE_DESCRIPTION_IMAGE = "Image Files";
    public static final Map<String, String> FILE_EXTENSION_FORMAT_IMAGE = new HashMap<>();
    static {
        FILE_EXTENSION_FORMAT_IMAGE.put("PNG files", "*.png");
        FILE_EXTENSION_FORMAT_IMAGE.put("JPG files", "*.jpg");
        FILE_EXTENSION_FORMAT_IMAGE.put("GIF files", "*.gif");
    }
    
    /*******************************************************************/
    
    /* OPERATIONS FOR PDF FILES */
    public static boolean isPath_PDF(Path path) throws NoFileExtensionException {
        return FILE_EXTENSION_PDF.equals(getFileExtension(path));
    }
    public static Path chooseSavePathOf_PDF(Stage stage) throws UserCancelledFileChooserException {
        return chooseSavePath(
                stage, 
                FILE_EXTENSION_PDF, 
                new String[]{"*" + FILE_EXTENSION_PDF},
                new File(System.getProperty("user.home") + File.separator + "Documents"));
    }
    
    /*******************************************************************/
    
    /* OPERATIONS FOR IMAGE FILES */
    public static boolean isPath_AnImage(Path path) throws NoFileExtensionException {
        return FILE_EXTENSION_FORMAT_IMAGE.containsValue("*" + getFileExtension(path));
    }
    public static Path chooseOpenPathOf_Image(Stage stage) throws UserCancelledFileChooserException {
        return chooseOpenPath(
                stage, 
                FILE_DESCRIPTION_IMAGE, 
                // Convert the key set of FILE_EXTENSION_IMAGE to a String array
                FILE_EXTENSION_FORMAT_IMAGE.values().toArray(new String[0]),
                new File(System.getProperty("user.home") + File.separator + "Pictures"));
    }
    
    /*******************************************************************/
    
    /* OPERATIONS FOR DB FILES */
    public static boolean isPath_Db(Path path) throws NoFileExtensionException {
        return FILE_EXTENSION_DB.equals(getFileExtension(path));
    }
    public static Path chooseOpenPathOf_Db(Stage stage) throws UserCancelledFileChooserException {
        return chooseOpenPath(
                stage, 
                FILE_DESCRIPTION_DB, 
                new String[]{"*" + FILE_EXTENSION_DB}, 
                ConfigurationManager.getManager().getDirPath_Databases().toFile());
    }
    public static Path chooseSavePathOf_Db(Stage stage) throws UserCancelledFileChooserException {
        return chooseSavePath(
                stage, 
                FILE_DESCRIPTION_DB, 
                new String[]{"*" + FILE_EXTENSION_DB}, 
                ConfigurationManager.getManager().getDirPath_Databases().toFile());
    }
    
    /*******************************************************************/
    // HELPERS
    
    public static void copyFile(Path source, Path destination) throws IOException  {
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }
    
    public static String getFileExtension(Path path) throws NoFileExtensionException {
        // extract file extension
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < fileName.length() - 1) return fileName.substring(dotIndex).toLowerCase();

        // file has no extension 'fileName' | 'fileName.'
        throw new NoFileExtensionException(
                String.format("File `%s` has no extension !", fileName)
        );
    }
    
    /*******************************************************************/
    
    // OVERLOADS
    private static Path chooseSavePath(
            Stage stage,
            String description,
            String[] extensionFormat) throws UserCancelledFileChooserException {
        return chooseSavePath(stage, description, extensionFormat, null);
    }
    private static Path chooseOpenPath(
            Stage stage,
            String description,
            String[] extensionFormat) throws UserCancelledFileChooserException {
        return chooseOpenPath(stage, description, extensionFormat, null);
    }
    
    private static Path chooseOpenPath(
            Stage stage, 
            String description, 
            String[] extensionFormat,
            File initialDir) throws UserCancelledFileChooserException {
        // Create a file chooser
        FileChooser fileChooser = new FileChooser();

        if(initialDir != null) fileChooser.setInitialDirectory(initialDir);

        // Add a filter to only show .db files
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter(description, extensionFormat);
        fileChooser.getExtensionFilters().add(dbFilter);

        File file = fileChooser.showOpenDialog(stage);
        if(file == null) throw new UserCancelledFileChooserException();

        // Show the file chooser dialog
        return file.toPath();
    }
    
    private static Path chooseSavePath(
            Stage stage,
            String description,
            String[] extensionFormat,
            File initialDir) throws UserCancelledFileChooserException {
        // Create a file chooser
        FileChooser fileChooser = new FileChooser();

        // set initial directory
        if(initialDir != null) fileChooser.setInitialDirectory(initialDir);

        // Add a filter to only show .db files
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter(description, extensionFormat);
        fileChooser.getExtensionFilters().add(dbFilter);

        // Show the file chooser dialog
        File file = fileChooser.showSaveDialog(stage);
        if(file == null) throw new UserCancelledFileChooserException();

        // return path
        return file.toPath();
    }
    
    /*******************************************************************/
}
