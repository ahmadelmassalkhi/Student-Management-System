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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    
    public static final int TYPE_NULL = -1;
    public static final int TYPE_INTEGER = 0;
    public static final int TYPE_LONG = 1;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_DOUBLE = 3;
    public static final int TYPE_STRING = 4;
    public static final int TYPE_BLOB = 5;

    // helper function to process prepared-statement parameters
    private List<Integer> processParameters(Object[] params) throws IllegalArgumentException {
        List<Integer> types = new ArrayList();
        for (Object param : params) {
            if(param == null) {
                types.add(TYPE_NULL); // null
            } else if (param instanceof Integer) {
                types.add(TYPE_INTEGER); // Integer
            } else if (param instanceof Long) {
                types.add(TYPE_LONG); // Long
            } else if(param instanceof Float) {
                types.add(TYPE_FLOAT); // Float
            } else if (param instanceof Double) {
                types.add(TYPE_DOUBLE); // Double
            } else if (param instanceof String) {
                types.add(TYPE_STRING); // String
            } else if (param instanceof Blob) {
                types.add(TYPE_BLOB); // Blob
            } else throw new IllegalArgumentException("Unknown or invalid type encountered while processing query parameters: " + param.getClass().getName());
        }
        return types;
    }
    
    public ResultSet executeQuery(String query, Object... params) throws SQLException, IllegalArgumentException {
        
        // Create a statement
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        
        // Bind parameters if provided
        if (params != null && params.length > 0) {
            List<Integer> types = processParameters(params);
            for (int i = 0; i < types.size(); i++) {
                switch (types.get(i)) {
                    case TYPE_NULL -> preparedStatement.setNull(i+1, java.sql.Types.NULL);
                    case TYPE_INTEGER -> preparedStatement.setInt(i+1, (int) params[i]);
                    case TYPE_FLOAT -> preparedStatement.setFloat(i+1, (float) params[i]);
                    case TYPE_DOUBLE -> preparedStatement.setDouble(i+1, (double) params[i]);
                    case TYPE_STRING -> preparedStatement.setString(i+1, (String) params[i]);
                    case TYPE_LONG -> preparedStatement.setLong(i+1, (long) params[i]);
                    case TYPE_BLOB -> preparedStatement.setBlob(i+1, (Blob) params[i]);
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
