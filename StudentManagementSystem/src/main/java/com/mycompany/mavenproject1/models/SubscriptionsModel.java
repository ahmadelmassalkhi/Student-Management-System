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
import java.util.Arrays;
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
    protected void CreateTable() throws SQLException, IOException {
        // Create tables
        String query = String.format(
            "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s INTEGER NOT NULL, %s TEXT)",
            TABLE,
            COL_ID,
            COL_STATUS,
            COL_EXPIRATION_DATE
        );
        // execute
        (new Query(query, null)).execute(database.connect());
        
        // deactive expired subscriptions
        this.deActivateExpiredSubscriptions();
        
        // print out a message
        System.out.println(TABLE + " table created successfully.");
    }
    
    public static final String FORMAT_SQLITE_EXPIRATION_DATE = "%Y-%m-%d";
    private void deActivateExpiredSubscriptions() throws SQLException, IOException {
        // create query
        String query = String.format(
                "UPDATE %s SET %s=?, %s=? WHERE %s <= date('now')",
                TABLE,
                COL_STATUS,
                COL_EXPIRATION_DATE,
                COL_EXPIRATION_DATE
        );
        
        // set params
        List<Object> params = Arrays.asList(Subscription.INACTIVE, NULL);
        
        // execute
        (new Query(query, params)).execute(database.connect());
    }
    
    public void Create(Subscription s) throws SQLException, IOException {
        // create query
        String query = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, ?);",
                TABLE, 
                COL_STATUS, 
                COL_EXPIRATION_DATE
        );
        
        // set params
        List<Object> params = Arrays.asList(s.getStatusInt(), s.getDateString());
        
        // execute
        (new Query(query, params)).execute(database.connect());
    }
    
    /*******************************************************************/
    // READ
    
    public List<Subscription> Read(Subscription s) throws SQLException, IOException { return Read(s.getId(), s.getStatus(), s.getDate()); }
    public List<Subscription> Read(Long id) throws SQLException, IOException { return Read(id, null, null); }
    public List<Subscription> Read(Boolean status) throws SQLException, IOException { return Read(null, status, null); }
    public List<Subscription> Read(LocalDate expirationDate) throws SQLException, IOException { return Read(null, null, expirationDate); }
    public List<Subscription> Read(
            Long id, 
            Boolean status, 
            LocalDate expirationDate) throws SQLException, IOException {
        
        // prepare query
        Query query = this.queryFilter("SELECT * FROM " + TABLE, id, status, expirationDate);
        
        ResultSet result = query.execute(database.connect());
        
        // fetch & return results
        List<Subscription> subscriptions = new ArrayList<>();
        while(result.next()) {
            Subscription s = new Subscription();
            s.setId(result.getLong(COL_ID));
            s.setDate(result.getString(COL_EXPIRATION_DATE));
            s.setStatus(result.getInt(COL_STATUS));
            subscriptions.add(s);
        }
        
        // close resources
        result.close();
        
        // return
        return subscriptions;
    }
    public long GetLastInsertedRowId() throws SQLException, IOException {
        String query = "SELECT last_insert_rowid() AS subscription_id";
        return (new Query(query, null)).execute(database.connect()).getLong(COL_ID);
    }
    public long GetAllActiveSubscriptions() throws SQLException, IOException {
        String query = String.format("SELECT COUNT(*) AS subsCount FROM %s WHERE %s = ?", TABLE, COL_STATUS);
        List<Object> params = Arrays.asList(Subscription.ACTIVE);
        return (new Query(query, params)).execute(database.connect()).getLong("subsCount");
    }
    
    /*******************************************************************/
    // UPDATE
    
    public void Update(Subscription oldS, Subscription newS) throws SQLException, IOException {
        // create query
        String query = String.format(
                "UPDATE %s SET %s=?, %s=? WHERE %s = ?", 
                TABLE, 
                COL_STATUS,
                COL_EXPIRATION_DATE,
                COL_ID
        );
        
        // set params
        List<Object> params = new ArrayList<>();
        params.add(newS.getStatusInt());
        params.add(newS.getDateString());
        params.add(oldS.getId());
        
        // execute
        (new Query(query, params)).execute(database.connect());
    }
    
    /*******************************************************************/
    // DELETE
    
    public void Delete(Subscription s) throws SQLException, IOException { Delete(s.getId(), s.getStatus(), s.getDate()); }
    public void Delete(Long id) throws SQLException, IOException { Delete(id, null, null); }
    public void Delete(Boolean status) throws SQLException, IOException { Delete(null, status, null); }
    public void Delete(LocalDate expirationDate) throws SQLException, IOException { Delete(null, null, expirationDate); }
    public void Delete(
            Long id, 
            Boolean status, 
            LocalDate expirationDate) throws SQLException, IOException {
        
        // prepare query
        Query query = this.queryFilter("DELETE FROM %s" + TABLE, id, status, expirationDate);
        
        // execute
        query.execute(database.connect());
    }
    
    /*******************************************************************/
    
    private Query queryFilter(
            String query,
            Long id,
            Boolean status, 
            LocalDate expirationDate) throws SQLException {
        
        List<Object> params = new ArrayList<>();
        if(id != null) {
            query += this.whereEqual(COL_ID);
            params.add(id);
            return new Query(query, params);
        }
        
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
        
        return new Query(query, params);
    }
}
