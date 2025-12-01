package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database credentials - these match your docker-compose.yml
    private static final String URL = "jdbc:mysql://localhost:3306/healthcare_system";
    private static final String USER = "root";
    private static final String PASSWORD = "password123";
    
    private static Connection connection = null;
    
    /**
     * Get database connection
     * This method connects your Java app to MySQL database
     */
    public static Connection getConnection() {
        try {
            // Step 1: Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Step 2: Create connection if it doesn't exist or is closed
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
            System.err.println("Make sure mysql-connector-j JAR is added to Libraries.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Check if Docker MySQL is running with: docker ps");
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test method - Run this to verify database connection works
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Testing Database Connection...");
        System.out.println("========================================");
        
        Connection conn = DatabaseConnection.getConnection();
        
        if (conn != null) {
            System.out.println("✅✅✅ CONNECTION TEST SUCCESSFUL! ✅✅✅");
            System.out.println("Your Java application can communicate with MySQL!");
            System.out.println("You're ready to create DAO classes!");
            closeConnection();
        } else {
            System.out.println("❌❌❌ CONNECTION TEST FAILED! ❌❌❌");
            System.out.println("Troubleshooting steps:");
            System.out.println("1. Check Docker is running: docker ps");
            System.out.println("2. Check MySQL JAR is in Libraries folder");
            System.out.println("3. Check password is 'password123' in docker-compose.yml");
        }
        
        System.out.println("========================================");
    }
}
