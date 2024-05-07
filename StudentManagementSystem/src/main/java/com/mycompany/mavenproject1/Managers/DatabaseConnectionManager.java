/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Managers;

// other imports
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author AHMAD
 */
public final class DatabaseConnectionManager {
    
    private static DatabaseConnectionManager manager = null;
    public static DatabaseConnectionManager getManager() throws SQLException, IOException {
        if(manager == null) manager = new DatabaseConnectionManager();
        return manager;
    }
    
    private DatabaseConnectionManager() throws SQLException, IOException {
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
        if (connection != null && !connection.isClosed()) connection.close();
        connection = null;
    }
    
    public ResultSet getSchemaInfo() throws SQLException { return getSchemaInfo(connection); }
    
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
