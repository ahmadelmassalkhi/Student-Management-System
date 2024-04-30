/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Common;

// imports from same package
import com.mycompany.mavenproject1.ModelObjects.Student;

// imports from itextpdf
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.mavenproject1.ModelObjects.Subscription;

// imports from javafx
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

// other imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;

/**
 *
 * @author AHMAD
 */
public class StudentsTablePDFExporter {
    
    // parent stage
    private Stage stage = null;
    
    // columns
    private TableColumn col_ID = null;
    private TableColumn col_FullName = null;
    private TableColumn col_Phone = null;
    private TableColumn col_Grade = null;
    private TableColumn col_Language = null;
    private TableColumn col_Subscription = null;
    private TableColumn col_Mark = null;
    
    private TableView studentsTable;
    public StudentsTablePDFExporter(TableView studentsTable) {
        this.studentsTable = studentsTable;
    }
    
    public void setStage(Stage stage) { this.stage = stage; }
    public void setColID(TableColumn col_ID) { this.col_ID = col_ID; }
    public void setColFullName(TableColumn col_FullName) { this.col_FullName = col_FullName; }
    public void setColPhone(TableColumn col_Phone) { this.col_Phone = col_Phone; }
    public void setColGrade(TableColumn col_Grade) { this.col_Grade = col_Grade; }
    public void setColLanguage(TableColumn col_Language) { this.col_Language = col_Language; }
    public void setColMark(TableColumn col_Mark) { this.col_Mark = col_Mark; }
    public void setColSubscription(TableColumn col_Subscription) { this.col_Subscription = col_Subscription; }
    
    /*******************************************************************/

    public void Export() throws FileNotFoundException, DocumentException {
        try {
            Document document = new Document();

            // create the pdf (empty) at the saveLocation
            PdfWriter.getInstance(document, new FileOutputStream(chooseSaveLocation())); // (throws NullPointerException)

            // open the document for writing
            document.open();

            // convert TableView to PdfPTable
            PdfPTable pdfTable = this.createPDFTable();
            this.addRows(pdfTable, studentsTable.getItems());

            // add the PDF table to the document
            document.add(pdfTable);
            document.close();
        } catch (NullPointerException ex) {
            /*
             * if user clicked `Cancel` on FileChooser
             * => no SaveLocation was chosen 
             * => FileChooser finished and closed 
             * => FileOutputStream throws NullPointerException
             * => execution stopped due exception
             */
            // do nothing
        }
    }

    // Prompts the user to choose a save location for the PDF file
    private File chooseSaveLocation() {
        FileChooser fileChooser = new FileChooser();

        // Set the extension filter to only show PDF files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show the save file dialog and return the selected file
        return fileChooser.showSaveDialog(stage);
    }
    
    /*******************************************************************/
    // Create PDF table
    
    private PdfPTable createPDFTable() {
        // get all visible columns (and set their widths)
        List<Float> widths = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        for (TableColumn column : (ObservableList<TableColumn>)studentsTable.getColumns()) {
            if(column.isVisible()) columns.add(column.getText());
            if(column == col_ID) widths.add(2f);
            if(column == col_FullName) widths.add(2f);
            if(column == col_Phone) widths.add(3f);
            if(column == col_Grade) widths.add(2f);
            if(column == col_Language) widths.add(1.5f);
            if(column == col_Mark) widths.add(1.5f);
            if(column == col_Subscription) widths.add(1.5f);
        }
        
        // create the table with its columns
        PdfPTable table = new PdfPTable(columns.size());
        table.setWidthPercentage(100); // set width to 100%
        
        try {
            float[] widthsArray = new float[widths.size()];
            for (int i = 0; i < widths.size(); i++) widthsArray[i] = widths.get(i);
            table.setWidths(widthsArray);
        } catch (DocumentException ex) {
            // do nothing (keep default widths)
        }

        // add column headers with styling
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        for(String col : columns) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY); // set gray background
            header.setBorderWidth(0); // remove border
            header.setHorizontalAlignment(Element.ALIGN_CENTER); // center the header values
            header.setPhrase(new Phrase(col, headerFont)); // set title
            table.addCell(header);
        }
        
        return table;
    }
    
    private void addRows(PdfPTable table, ObservableList<Student> studentList) {
        for (Student s : studentList) {
            if(col_ID != null && col_ID.isVisible()) table.addCell(createPdfPCell(s.getId() + ""));
            if(col_FullName != null && col_FullName.isVisible()) table.addCell(createPdfPCell(s.getFullName()));
            if(col_Phone != null && col_Phone.isVisible()) table.addCell(createPdfPCell(s.getPhone()));
            if(col_Grade != null && col_Grade.isVisible()) table.addCell(createPdfPCell(s.getGrade() + ""));
            if(col_Language != null && col_Language.isVisible()) table.addCell(createPdfPCell(s.getLanguage()));
            if(col_Subscription != null && col_Subscription.isVisible()) table.addCell(createPdfPCell(s.getSubscription().getStatus() ? Subscription.ACTIVE_STRING : Subscription.INACTIVE_STRING));
            if(col_Mark != null && col_Mark.isVisible()) table.addCell(createPdfPCell(s.getMark() + ""));
        }
    }
    
    private PdfPCell createPdfPCell(String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value));
        cell.setBorderColor(BaseColor.LIGHT_GRAY); // set border as gray
        cell.setNoWrap(false); // allow wrap (incase of overflow)
        return cell;
    }
    
    /*******************************************************************/
}
