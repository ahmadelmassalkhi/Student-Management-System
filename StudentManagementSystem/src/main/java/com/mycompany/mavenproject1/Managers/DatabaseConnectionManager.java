/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Managers;

// other imports
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;

/**
 *
 * @author AHMAD
 */
public class DatabaseConnectionManager {
    
    private static DatabaseConnectionManager manager = null;
    public static DatabaseConnectionManager getManager() throws SQLException, IOException {
        if(manager == null) manager = new DatabaseConnectionManager();
        return manager;
    }
    
    // so that classes in same package/directory can create more objects (for connection testing)
    protected DatabaseConnectionManager() throws SQLException, IOException {
        connection = this.connect();
    }
    
    /*******************************************************/

    private Connection connection;
    public Connection connect() throws SQLException, IOException {
        if(connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + ConfigurationManager.getManager().getCurrentDatabasePath());
        }
        return connection;
    }
    
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }
    
    public ResultSet getSchemaInfo() throws SQLException {
        return getSchemaInfo(connection);
    }
    
    /*******************************************************/
    // QUERY EXECUTION

    // helper function to process prepared-statement parameters
    private String processParameters(Object[] params) throws IllegalArgumentException {
        StringBuilder types = new StringBuilder();
        for (Object param : params) {
            if(param == null) continue; // for nullable columns
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
            } else if (param instanceof LocalDate) {
                types.append("t"); // Local Date (time)
            } else throw new IllegalArgumentException("Unknown or invalid type encountered while processing query parameters: " + param.getClass().getName());
        }
        return types.toString();
    }
    
    public ResultSet executeQuery(String query, Object... params) throws SQLException, IllegalArgumentException {
        
        // Create a statement
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        
        // Bind parameters if provided
        if (params != null && params.length > 0) {
            String types = processParameters(params);
            for (int i = 0; i < types.length(); i++) {
                if(params[i] == null) continue;
                switch (types.charAt(i)) {
                    case 'i' -> preparedStatement.setInt(i+1, (int) params[i]);
                    case 'f' -> preparedStatement.setFloat(i+1, (float) params[i]);
                    case 'd' -> preparedStatement.setDouble(i+1, (double) params[i]);
                    case 's' -> preparedStatement.setString(i+1, (String) params[i]);
                    case 'l' -> preparedStatement.setLong(i+1, (long) params[i]);
                    case 'b' -> preparedStatement.setBlob(i+1, (Blob) params[i]);
                    case 't' -> preparedStatement.setDate(i+1, Date.valueOf((LocalDate) params[i])); // (LocalDate is newer version of Date, not subclass)
                    default -> throw new IllegalArgumentException(String.format("Unknown or invalid type encountered while processing query parameters: `%s`", types.charAt(i)));
                }
            }
        }
        
        // Execute query
        preparedStatement.execute();

        // get & return ResultSet
        return preparedStatement.getResultSet();
    }
    
    /*******************************************************/

    // Method to retrieve schema information for a given database connection
    private static ResultSet getSchemaInfo(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery("PRAGMA table_info;");
    }

    // Method to compare the schema of two databases
    public static boolean compareDatabaseSchemas(
            Connection connectionA, 
            Connection connectionB) throws SQLException {
        
        ResultSet schemaA = getSchemaInfo(connectionA);
        ResultSet schemaB = getSchemaInfo(connectionB);

        // Compare the schema information obtained for each database
        while (schemaA.next() && schemaB.next()) {
            // Compare table name, column name, and column type
            String tableNameA = schemaA.getString("name");
            String tableNameB = schemaB.getString("name");
            if (!tableNameA.equals(tableNameB)) return false; // Table names are different

            String columnNameA = schemaA.getString("name");
            String columnNameB = schemaB.getString("name");
            if (!columnNameA.equals(columnNameB)) return false; // Column names are different

            String columnTypeA = schemaA.getString("type");
            String columnTypeB = schemaB.getString("type");
            if (!columnTypeA.equals(columnTypeB)) return false; // Column types are different
        }

        return !schemaA.next() && !schemaB.next();
    }
}
