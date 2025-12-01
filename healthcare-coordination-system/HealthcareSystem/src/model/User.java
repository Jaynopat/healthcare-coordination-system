package model;

import java.sql.Timestamp;

/**
 * User model class - represents users table in database
 * This includes doctors, pharmacists, clinic admins, and pharmacy managers
 */
public class User {
    // Fields - these match the columns in the 'users' table
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role;  // DOCTOR, PHARMACIST, CLINIC_ADMIN, PHARMACY_MANAGER
    private String enterpriseType;  // CLINIC or PHARMACY
    private String email;
    private String phone;
    private Timestamp createdDate;
    
    // Empty constructor - needed for creating new User objects
    public User() {
    }
    
    // Constructor with main fields - for creating new users
    public User(String username, String password, String fullName, String role, String enterpriseType) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.enterpriseType = enterpriseType;
    }
    
    // Getters and Setters
    // These allow other classes to read and modify the fields
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    
    // toString method - useful for displaying user in dropdowns and lists
    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}