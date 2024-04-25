/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.models;

import com.mycompany.mavenproject1.Exceptions.PhoneAlreadyExistsException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AHMAD
 */
public class StudentsModel {
    // TABLE ATTRIBUTES
    private static final String TABLE = "students";
    private static final String ID = "student_id";
    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String PHONE = "phone";
    private static final String GRADE = "grade";
    private static final String LANGUAGE = "language";
    private static final String SUBSCRIPTION = "subscriptionStatus";
    private static final String MARK = "mark";
    
    // restrict to one object (no need of more)
    private static StudentsModel model = null;
    public static StudentsModel getModel() throws SQLException {
        if(model == null) model = new StudentsModel();
        return model;
    }
    
    private static DatabaseConnectionManager database;
    private StudentsModel() throws SQLException {
        this.database = new DatabaseConnectionManager();
        this.CreateStudentsTable();
    }
    
    private void CreateStudentsTable() throws SQLException {
        // Create tables
        String query = String.format(
            "CREATE TABLE IF NOT EXISTS %s "
                    + "(%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT UNIQUE NOT NULL, %s INTEGER NOT NULL, %s TEXT NOT NULL, %s INTEGER NOT NULL, %s FLOAT)",
            TABLE,
            ID,
            FIRSTNAME,
            LASTNAME,
            PHONE,
            GRADE,
            LANGUAGE,
            SUBSCRIPTION,
            MARK
        );
        
        // execute
        database.executeQuery(query, new Object[] {});
        
        // print out a message
        System.out.println("Database initialized successfully.");
    }

    /*******************************************************************/
    // CRUD OPERATIONS
    
    // CREATE
    public void addStudent(Student s) throws PhoneAlreadyExistsException, SQLException {
        // check if student already exists
        if(this.existsStudent(s)) throw new PhoneAlreadyExistsException();
        
        // prepare query & its parameters
        String query = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);",
                TABLE, 
                FIRSTNAME,
                LASTNAME,
                PHONE,
                GRADE,
                LANGUAGE,
                SUBSCRIPTION
        );
        
        List<Object> params = new ArrayList<>();
        params.add(s.getFirstName());
        params.add(s.getLastName());
        params.add(s.getPhone());
        params.add(s.getGrade());
        params.add(s.getLanguage());
        params.add(s.getSubscriptionStatusInt());
        
        // execute query
        database.executeQuery(query, params.toArray());
    }
    
    // READ
    public List<Student> getAllStudents() throws SQLException { return searchStudents("", "", "", "", "", ""); }
    public List<Student> searchStudents(
            String firstName, 
            String lastName, 
            String phone, 
            String grade, 
            String language, 
            String subscription) throws SQLException {
        String query = "SELECT * FROM " + TABLE;
        List<Object> params = new ArrayList<>();
        
        boolean first = true;
        if(!firstName.isEmpty()) {
            query += this.whereClauseHelper(FIRSTNAME);
            params.add("%" + firstName + "%");
            first = false;
        }
        if(!lastName.isEmpty()) {
            if(first) query += this.whereClauseHelper(LASTNAME);
            else query += this.andClauseHelper(LASTNAME);
            params.add("%" + lastName + "%");
            first = false;
        }
        if(!phone.isEmpty()) {
            if(first) query += this.whereClauseHelper(PHONE);
            else query += this.andClauseHelper(PHONE);
            params.add("%" + phone + "%");
            first = false;
        }
        if(!grade.isEmpty() && !grade.equals("Any")) {
            if(first) query += String.format(" WHERE %s = ?", GRADE);
            else query += String.format(" AND %s = ?", GRADE);
            params.add(Integer.valueOf(grade));
            first = false;
        }
        if(!language.isEmpty() && !language.equals("Any")) {
            if(first) query += this.whereClauseHelper(LANGUAGE);
            else query += this.andClauseHelper(LANGUAGE);
            params.add("%" + language + "%");
            first = false;
        }
        if(!subscription.isEmpty() && !subscription.equals("Any")) {
            if(first) query += String.format(" WHERE %s = ?", SUBSCRIPTION);
            else query += String.format(" AND %s = ?", SUBSCRIPTION);
            params.add(Student.getSubscriptionStatusInt(subscription));
        }
        
        // execute query
        database.executeQuery(query, params.toArray());
        
        // fetch and return results
        return fetchAllRows();
    }
    
    // UPDATE
    public void updateStudent(Student oldS, Student updatedS) throws SQLException, PhoneAlreadyExistsException {
        
        // check if updated phone number
        if(oldS.getPhone().equals(updatedS.getPhone()) == false) {
            // check if the updated phone number already exists in the database
            if(this.existsStudent(updatedS)) {
                throw new PhoneAlreadyExistsException(); // (phone is the only unique attribute besides ID)
            }
        }
        
        /* update student information */
        // prepare query
        String query = String.format(
                "UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?;",
                TABLE,
                FIRSTNAME,
                LASTNAME,
                PHONE,
                GRADE,
                LANGUAGE,
                SUBSCRIPTION,
                ID
        );
        
        // set parameters
        Object[] params = new Object[] {
            updatedS.getFirstName(),
            updatedS.getLastName(),
            updatedS.getPhone(),
            updatedS.getGrade(),
            updatedS.getLanguage(),
            updatedS.getSubscriptionStatusInt(),
            oldS.getId()
        };
        
        // execute query
        database.executeQuery(query, params);
    }
    
    // DELETE
    public void deleteStudentsByIDs(List<Integer> IDs) throws SQLException {
        // Check if the list is empty
        if (IDs.isEmpty()) return;

        // Construct the IN clause with properly formatted phone numbers
        StringBuilder idListString = new StringBuilder();
        for (int id : IDs) idListString.append("?,");

        // Remove the trailing comma
        idListString.deleteCharAt(idListString.length() - 1);

        // Construct the SQL query
        String query = String.format("DELETE FROM %s WHERE %s IN (%s)", TABLE, ID, idListString.toString());

        // execute query
        database.executeQuery(query, IDs.toArray());
    }
    
    /*******************************************************************/
    // HELPERS

    private String whereClauseHelper(String colName) { return String.format(" WHERE %s LIKE ?", colName); }
    private String andClauseHelper(String colName) { return String.format(" AND %s LIKE ?", colName); }
    
    private List<Student> fetchAllRows() throws SQLException {
        List<Student> students = new ArrayList<>();
        while(database.result.next()) {
            // create student
            Student s = new Student();
            s.setId(database.result.getInt(ID));
            s.setFirstName(database.result.getString(FIRSTNAME));
            s.setLastName(database.result.getString(LASTNAME));
            s.setPhone(database.result.getString(PHONE));
            s.setGrade(database.result.getInt(GRADE));
            s.setLanguage(database.result.getString(LANGUAGE));
            s.setSubscriptionStatus(database.result.getInt(SUBSCRIPTION));
            
            // add student to list
            students.add(s);
        }
        // close resources
        database.closeResult();
        // return
        return students;
    }
    
    /*******************************************************************/

    public boolean existsStudent(Student s) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?", TABLE, PHONE);
        database.executeQuery(query, new Object[] {s.getPhone()});
        return database.result.next();
    }
    
    // time complexity = O(1)
    public int getNumberOfStudents() throws SQLException {
        String query = "SELECT COUNT(*) AS rowCount FROM " + TABLE;
        database.executeQuery(query, new Object[] {});
        return database.result.getInt("rowCount");
    }
    
    public int getNumberOfSubscriptions() throws SQLException {
        String query = String.format("SELECT COUNT(*) AS subsCount FROM %s WHERE %s = 1", TABLE, SUBSCRIPTION);
        database.executeQuery(query, new Object[] {});
        return database.result.getInt("subsCount");
    }
    
    /*******************************************************************/
}
