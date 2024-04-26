/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.models;

// other imports
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.sql.Blob;

/**
 *
 * @author AHMAD
 */
public class DatabaseConnectionManager {

    public DatabaseConnectionManager() throws SQLException {
        connection = this.connect("sqlite");
    }
    
    /*******************************************************/

    private Connection connection;
    private Connection connect(String fileName) throws SQLException {
        if(connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:./databases/" + fileName + ".db");
        }
        return connection;
    }
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }
    
    /*******************************************************/
    // QUERY EXECUTION

    // helper function to process prepared-statement parameters
    private String processParameters(Object[] params) throws IllegalArgumentException {
        StringBuilder types = new StringBuilder();
        for (Object param : params) {
            if (param instanceof Integer) {
                types.append("i"); // Integer
            } else if(param instanceof Float) {
                types.append("f"); // Float
            } else if (param instanceof Double) {
                types.append("d"); // Double
            } else if (param instanceof String) {
                types.append("s"); // String
            } else if (param instanceof Long) {
                types.append("l"); // Long
            } else if (param instanceof Blob) {
                types.append("b"); // Blob
            } else {
                if(param == null) throw new IllegalArgumentException("Why the fuck is param = null ?");
                throw new IllegalArgumentException("Unknown or invalid type encountered: " + param.getClass().getName());
            }
        }
        return types.toString();
    }
    
    public ResultSet result = null;
    public void executeQuery(String query, Object... params) throws SQLException, IllegalArgumentException {
        
        // Create a statement
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        
        // Bind parameters if provided
        if (params != null && params.length > 0) {
            String types = processParameters(params);
            for (int i = 0; i < params.length; i++) {
                switch (types.charAt(i)) {
                    case 'i' -> preparedStatement.setInt(i+1, (int) params[i]);
                    case 'f' -> preparedStatement.setFloat(i+1, (float) params[i]);
                    case 'd' -> preparedStatement.setDouble(i+1, (double) params[i]);
                    case 's' -> preparedStatement.setString(i+1, (String) params[i]);
                    case 'l' -> preparedStatement.setLong(i+1, (long) params[i]);
                    case 'b' -> preparedStatement.setBlob(i+1, (Blob) params[i]);
                    default -> throw new IllegalArgumentException(String.format("Unknown or invalid type encountered: `%s`", types.charAt(i)));
                }
            }
        }
        
        // Execute query
        preparedStatement.execute();

        // Get result if it's a SELECT query
        if (query.trim().toLowerCase().startsWith("select")) {
            this.result = preparedStatement.getResultSet();
        }
    }
    
    /*******************************************************/
    // RESULT PROCESSING
    
    // function to fetch one row (keeps the resultSet open)
    public Map<String, Object> fetchRow() throws SQLException {
        if (this.result == null) return null;
        if (!this.result.next()) return null;

        // get metaData information
        ResultSetMetaData metaData = this.result.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        // fetch
        Map<String, Object> row = new HashMap<>();
        for (int i = 1; i <= columnCount; i++) {
            String colName = metaData.getColumnName(i);
            Object colValue = this.result.getObject(i);
            row.put(colName, colValue);
        }
        return row;
    }
    
    // function to fetch all rows (closes the resultSet)
    public List<Map<String, Object>> fetchAll() throws SQLException {
        if(this.result == null) return null;
        
        // get metaData information
        ResultSetMetaData metaData = this.result.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        // fetch
        List<Map<String, Object>> rows = new ArrayList<>();
        while(this.result.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = this.result.getObject(i);
                row.put(columnName, columnValue);
            }
            rows.add(row);
        }
        
        // close result
        this.closeResult();
        
        // return
        return rows;
    }
    
    // closes the result
    public void closeResult() throws SQLException {
        if (result != null) result.close();
        this.result = null;
    }
    
    /*******************************************************/
}
