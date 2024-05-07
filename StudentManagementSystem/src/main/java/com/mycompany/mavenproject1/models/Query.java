/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.models;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AHMAD
 */
public class Query {
    private String query;
    private List<Object> params;
    
    public Query(String query, List<Object> params) throws IllegalArgumentException {
        if(query == null) throw new IllegalArgumentException("Please provide valid attributes for Query object !");
        this.query = query;
        this.params = params;
    }
    
    // GETTERS
    public String getString() { return query; }
    public List<Object> getParams() { return params; }
    
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
    private List<Integer> processParameters(List<Object> params) throws IllegalArgumentException {
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
    
    public ResultSet execute(Connection connection) throws SQLException, IllegalArgumentException {
        if(connection == null) throw new IllegalArgumentException("Connection for query execution can NOT be null !");
        
        // Create a statement
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        
        // Bind parameters if provided
        if (params != null && !params.isEmpty()) {
            List<Integer> types = processParameters(params);
            for (int i = 0; i < types.size(); i++) {
                switch (types.get(i)) {
                    case TYPE_NULL -> preparedStatement.setNull(i+1, java.sql.Types.NULL);
                    case TYPE_INTEGER -> preparedStatement.setInt(i+1, (int) params.get(i));
                    case TYPE_FLOAT -> preparedStatement.setFloat(i+1, (float) params.get(i));
                    case TYPE_DOUBLE -> preparedStatement.setDouble(i+1, (double) params.get(i));
                    case TYPE_STRING -> preparedStatement.setString(i+1, (String) params.get(i));
                    case TYPE_LONG -> preparedStatement.setLong(i+1, (long) params.get(i));
                    case TYPE_BLOB -> preparedStatement.setBlob(i+1, (Blob) params.get(i));
                }
            }
        }
        
        // Execute query
        preparedStatement.execute();

        // get & return ResultSet
        return preparedStatement.getResultSet();
    }
    
    /*******************************************************/
}
