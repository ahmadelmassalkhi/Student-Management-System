/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.models;

import com.mycompany.mavenproject1.Managers.DatabaseConnectionManager;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author AHMAD
 */
public abstract class Model {
    
    protected final DatabaseConnectionManager database;
    public Model() throws SQLException, IOException {
        this.database = DatabaseConnectionManager.getManager();
    }
    
    public void reconnect() throws SQLException, IOException {
        database.disconnect();
        database.connect();
    }
    
    public static final String NULL = "NULL";
    protected abstract void CreateTable() throws Exception;
    
    /*******************************************************************/
    // QUERY HELPERS
    
    protected String whereLike(String colName) { return String.format(" WHERE %s LIKE ?", colName); }
    protected String whereEqual(String colName) { return String.format(" WHERE %s = ?", colName); }
    protected String whereLessEqual(String colName) { return String.format(" WHERE %s <= ?", colName); }
    protected String whereGreaterEqual(String colName) { return String.format(" WHERE %s >= ?", colName); }
    protected String whereBetween(String colName) { return String.format(" WHERE %s BETWEEN ? AND ?", colName); }

    protected String andLike(String colName) { return String.format(" AND %s LIKE ?", colName); }
    protected String andEqual(String colName) { return String.format(" AND %s = ?", colName); }
    protected String andLessEqual(String colName) { return String.format(" AND %s <= ?", colName); }
    protected String andGreaterEqual(String colName) { return String.format(" AND %s >= ?", colName); }
    protected String andBetween(String colName) { return String.format(" AND %s BETWEEN ? AND ?", colName); }
    
    protected String orderByASC(String colName) { return String.format(" ORDER BY %s ASC", colName); }
    protected String orderByDESC(String colName) { return String.format(" ORDER BY %s DESC", colName); }

    /*******************************************************************/
}
