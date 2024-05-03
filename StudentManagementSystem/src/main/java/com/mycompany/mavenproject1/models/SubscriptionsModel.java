/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.models;

import com.mycompany.mavenproject1.ModelObjects.Subscription;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AHMAD
 */
public final class SubscriptionsModel extends Model {
    
    public static final String TABLE = "subscriptions";
    public static final String COL_ID = "subscription_id";
    public static final String COL_STATUS = "status";
    public static final String COL_EXPIRATION_DATE = "expiration_date";
    
    // restrict to one object (no need of more)
    private static SubscriptionsModel model = null;
    public static SubscriptionsModel getModel() throws SQLException, IOException {
        if(model == null) model = new SubscriptionsModel();
        return model;
    }
    
    private SubscriptionsModel() throws SQLException, IOException {
        this.CreateTable();
    }
    
    /*******************************************************************/
    // CREATE
    
    @Override
    protected void CreateTable() throws SQLException {
        // Create tables
        String query = String.format(
            "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s INTEGER NOT NULL, %s TEXT)",
            TABLE,
            COL_ID,
            COL_STATUS,
            COL_EXPIRATION_DATE
        );
        // execute
        database.executeQuery(query, new Object[] {});
        
        // deactive expired subscriptions
        this.deActivateExpiredSubscriptions();
        
        // print out a message
        System.out.println(TABLE + " table created successfully.");
    }
    
    public static final String FORMAT_SQLITE_EXPIRATION_DATE = "%Y-%m-%d";
    private void deActivateExpiredSubscriptions() throws SQLException {
        String query = String.format(
                "UPDATE %s SET %s=?, %s=? WHERE %s <= date('now')",
                TABLE,
                COL_STATUS,
                COL_EXPIRATION_DATE,
                COL_EXPIRATION_DATE
        );
        database.executeQuery(query, new Object[] {Subscription.INACTIVE, NULL});
    }
    
    public void Create(Subscription s) throws SQLException {
        String query = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, ?);",
                TABLE, 
                COL_STATUS, 
                COL_EXPIRATION_DATE
        );
        
        // execute
        database.executeQuery(query, new Object[] {
            s.getStatus() ? Subscription.ACTIVE : Subscription.INACTIVE, 
            s.getDateString()
        });
    }
    
    /*******************************************************************/
    // READ
    
    public List<Subscription> Read(Subscription s) throws SQLException { return Read(s.getId(), s.getStatus(), s.getDate()); }
    public List<Subscription> Read(Long id) throws SQLException { return Read(id, null, null); }
    public List<Subscription> Read(Boolean status) throws SQLException { return Read(null, status, null); }
    public List<Subscription> Read(LocalDate expirationDate) throws SQLException { return Read(null, null, expirationDate); }
    public List<Subscription> Read(
            Long id, 
            Boolean status, 
            LocalDate expirationDate) throws SQLException {
        
        // prepare query
        String query = "SELECT * FROM " + TABLE;
        List<Object> params = new ArrayList<>();
        
        // build query
        boolean first = true;
        if(id != null) {
            query += this.whereEqual(COL_ID);
            params.add(id);
            first = false;
        }
        if(status != null) {
            if(first) query += this.whereEqual(COL_STATUS);
            else query += this.andEqual(COL_STATUS);
            params.add(status ? Subscription.ACTIVE : Subscription.INACTIVE);
            first = false;
        }
        if(expirationDate != null) {
            if(first) query += this.whereEqual(COL_EXPIRATION_DATE);
            else query += this.andEqual(COL_EXPIRATION_DATE);
            params.add(expirationDate.toString());
            first = false;
        }
        
        // execute query and get resultSet
        ResultSet result = database.executeQuery(query, params.toArray());
        
        // fetch & return results
        List<Subscription> subscriptions = new ArrayList<>();
        while(result.next()) {
            Subscription s = new Subscription();
            s.setId(result.getLong(COL_ID));
            s.setDate(result.getString(COL_EXPIRATION_DATE));
            s.setStatus(result.getInt(COL_STATUS) == Subscription.ACTIVE);
            subscriptions.add(s);
        }
        
        // close resources
        result.close();
        
        // return
        return subscriptions;
    }
    public long GetLastInsertedRowId() throws SQLException {
        String query = "SELECT last_insert_rowid() AS subscription_id";
        return database.executeQuery(query, new Object[] {}).getLong(COL_ID);
    }
    public long GetAllActiveSubscriptions() throws SQLException {
        String query = String.format("SELECT COUNT(*) AS subsCount FROM %s WHERE %s = ?", TABLE, COL_STATUS);
        return database.executeQuery(query, new Object[] {Subscription.ACTIVE}).getLong("subsCount");
    }
    
    /*******************************************************************/
    // UPDATE
    
    public void Update(Subscription oldS, Subscription newS) throws SQLException {
        String query = String.format(
                "UPDATE %s SET %s=?, %s=? WHERE %s = ?", 
                TABLE, 
                COL_STATUS,
                COL_EXPIRATION_DATE,
                COL_ID
        );
        
        database.executeQuery(query, new Object[] {
            newS.getStatus() ? Subscription.ACTIVE : Subscription.INACTIVE , 
            newS.getDateString(), 
            oldS.getId()
        });
    }
    
    /*******************************************************************/
    // DELETE
    
    public void Delete(Subscription s) throws SQLException { Delete(s.getId(), s.getStatus(), s.getDate()); }
    public void Delete(Long id) throws SQLException { Delete(id, null, null); }
    public void Delete(Boolean status) throws SQLException { Delete(null, status, null); }
    public void Delete(LocalDate expirationDate) throws SQLException { Delete(null, null, expirationDate); }
    public void Delete(
            Long id, 
            Boolean status, 
            LocalDate expirationDate) throws SQLException {
        
        // prepare query
        String query = "DELETE FROM %s" + TABLE;
        List<Object> params = new ArrayList<>();
        
        // build query
        boolean first = true;
        if(id != null) {
            query += this.whereEqual(COL_ID);
            params.add(id);
            first = false;
        }
        if(status != null) {
            if(first) query += this.whereEqual(COL_STATUS);
            else query += this.andEqual(COL_STATUS);
            params.add(status ? Subscription.ACTIVE : Subscription.INACTIVE);
            first = false;
        }
        if(expirationDate != null) {
            if(first) query += this.whereEqual(COL_EXPIRATION_DATE);
            else query += this.andEqual(COL_EXPIRATION_DATE);
            params.add(expirationDate.toString());
            first = false;
        }
        
        // execute query
        database.executeQuery(query, params);
    }
    
    /*******************************************************************/
}
