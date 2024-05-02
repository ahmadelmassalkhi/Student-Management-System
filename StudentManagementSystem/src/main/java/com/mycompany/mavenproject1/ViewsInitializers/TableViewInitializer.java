/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.ViewsInitializers;

import com.mycompany.mavenproject1.ModelObjects.Student;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author AHMAD
 */
public class TableViewInitializer {

    /*******************************************************************/

    public static void Initialize_ID(TableView table, TableColumn col_ID) { Initialize_Table(table, col_ID, null, null, null, null, null, null, null); }
    public static void Initialize_FullName(TableView table, TableColumn col_FullName) { Initialize_Table(table, null, col_FullName, null, null, null, null, null, null); }
    public static void Initialize_Phone(TableView table, TableColumn col_Phone) { Initialize_Table(table, null, null, col_Phone, null, null, null, null, null); }
    public static void Initialize_Grade(TableView table, TableColumn col_Grade) { Initialize_Table(table, null, null, null, col_Grade, null, null, null, null); }
    public static void Initialize_Language(TableView table, TableColumn col_Language) { Initialize_Table(table, null, null, null, null, col_Language, null, null, null); }
    public static void Initialize_SubscriptionStatus(TableView table, TableColumn col_SubscriptionStatus) { Initialize_Table(table, null, null, null, null, null, col_SubscriptionStatus, null, null); }
    public static void Initialize_SubscriptionExpireDate(TableView table, TableColumn col_SubscriptionExpireDate) { Initialize_Table(table, null, null, null, null, null, null, col_SubscriptionExpireDate, null); }
    public static void Initialize_Mark(TableView table, TableColumn col_Mark) { Initialize_Table(table, null, null, null, null, null, null, null, col_Mark); }
    public static void Initialize_Table(
            TableView table,
            TableColumn col_ID,
            TableColumn col_FullName,
            TableColumn col_Phone,
            TableColumn col_Grade,
            TableColumn col_Language,
            TableColumn col_SubscriptionStatus,
            TableColumn col_SubscriptionExpireDate,
            TableColumn col_Mark) {
        if(table == null) return;
        
        // allow multiple selections
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // associate data to columns
        if(col_ID != null) col_ID.setCellValueFactory(new PropertyValueFactory<Student, String>("id"));
        if(col_FullName != null) col_FullName.setCellValueFactory(new PropertyValueFactory<Student, String>("fullName"));
        if(col_Grade != null) col_Grade.setCellValueFactory(new PropertyValueFactory<Student, String>("grade"));
        if(col_Language != null) col_Language.setCellValueFactory(new PropertyValueFactory<Student, String>("language"));
        if(col_Phone != null) col_Phone.setCellValueFactory(new PropertyValueFactory<Student, String>("phone"));
        if(col_SubscriptionStatus != null) col_SubscriptionStatus.setCellValueFactory(new PropertyValueFactory<Student, String>("subscriptionStatus"));
        if(col_SubscriptionExpireDate != null) col_SubscriptionExpireDate.setCellValueFactory(new PropertyValueFactory<Student, String>("subscriptionDate"));
        if(col_Mark != null) col_Mark.setCellValueFactory(new PropertyValueFactory<Student, String>("mark"));

        // hide ID column by default (made to be interacted with, programmatically)
        if(col_ID != null) col_ID.setVisible(false);
        
        // restrict column widths to keep them fully inside the table
        ObservableList<TableColumn> allColumns = table.getColumns();
        for (TableColumn column : allColumns) {
            ObservableList<TableColumn> subColumns = column.getColumns();
            for(TableColumn subCol : subColumns) setColumnResizeLimitListener(table, subCol, allColumns);
            if(subColumns.isEmpty()) setColumnResizeLimitListener(table, column, allColumns);
        }
    }
    
    public static void setColumnResizeLimitListener(
            TableView table, 
            TableColumn column, 
            ObservableList<TableColumn> allColumns) {
        column.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            // get sum of all currently set widths (widths)
            double widthSum = 0;
            for(TableColumn col : allColumns) {
                ObservableList<TableColumn> subColumns = col.getColumns();
                for(TableColumn subCol : subColumns) widthSum += subCol.getWidth();
                if(subColumns.isEmpty()) widthSum += col.getWidth();
            }

            // if exceeds totalWidth, set the max width to the oldWidth
            if (widthSum > table.getWidth()) column.setMaxWidth(oldWidth.doubleValue());
            else column.setMaxWidth(Double.MAX_VALUE); // reset max width
        });
    }

    /*******************************************************************/
}
