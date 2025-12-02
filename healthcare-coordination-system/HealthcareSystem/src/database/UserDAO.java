package database;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Data Access Object for User operations
 * Handles all database operations for users table
 */
public class UserDAO {
    
    /**
     * LOGIN - Authenticate user by username and password
     * This is used for login screen
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // User found - create User object from database data
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setEnterpriseType(rs.getString("enterprise_type"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setCreatedDate(rs.getTimestamp("created_date"));
                
                System.out.println("✅ Login successful: " + user.getFullName());
                return user;
            } else {
                System.out.println("❌ Login failed: Invalid username or password");
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error during login:");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * CREATE - Add new user to database
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, role, enterprise_type, email, phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getEnterpriseType());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPhone());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ User created successfully: " + user.getFullName());
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating user:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * READ - Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setEnterpriseType(rs.getString("enterprise_type"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setCreatedDate(rs.getTimestamp("created_date"));
                
                users.add(user);
            }
            
            System.out.println("✅ Retrieved " + users.size() + " users from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving users:");
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * READ - Get users by role (e.g., all doctors or all pharmacists)
     */
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setEnterpriseType(rs.getString("enterprise_type"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                
                users.add(user);
            }
            
            System.out.println("✅ Retrieved " + users.size() + " users with role: " + role);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving users by role:");
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * READ - Get single user by ID
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setEnterpriseType(rs.getString("enterprise_type"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setCreatedDate(rs.getTimestamp("created_date"));
                
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving user by ID:");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * UPDATE - Update existing user
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setInt(4, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ User updated successfully: " + user.getFullName());
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating user:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * DELETE - Delete user by ID
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ User deleted successfully");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting user:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * TEST METHOD - Test all UserDAO operations
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Testing UserDAO Operations");
        System.out.println("========================================\n");
        
        UserDAO userDAO = new UserDAO();
        
        // Test 1: Login with valid credentials
        System.out.println("TEST 1: Login with valid credentials");
        User loggedInUser = userDAO.login("dr.smith", "pass123");
        if (loggedInUser != null) {
            System.out.println("✅ Login test passed!");
            System.out.println("   Logged in as: " + loggedInUser.getFullName());
            System.out.println("   Role: " + loggedInUser.getRole());
        } else {
            System.out.println("❌ Login test failed!");
        }
        System.out.println();
        
        // Test 2: Login with invalid credentials
        System.out.println("TEST 2: Login with invalid credentials");
        User invalidUser = userDAO.login("wronguser", "wrongpass");
        if (invalidUser == null) {
            System.out.println("✅ Invalid login test passed (correctly rejected)!");
        } else {
            System.out.println("❌ Invalid login test failed!");
        }
        System.out.println();
        
        // Test 3: Get all users
        System.out.println("TEST 3: Get all users");
        List<User> allUsers = userDAO.getAllUsers();
        System.out.println("✅ Found " + allUsers.size() + " users in database");
        for (User u : allUsers) {
            System.out.println("   - " + u.getFullName() + " (" + u.getRole() + ")");
        }
        System.out.println();
        
        // Test 4: Get users by role
        System.out.println("TEST 4: Get all doctors");
        List<User> doctors = userDAO.getUsersByRole("DOCTOR");
        System.out.println("✅ Found " + doctors.size() + " doctors");
        for (User doc : doctors) {
            System.out.println("   - " + doc.getFullName());
        }
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("All UserDAO tests completed!");
        System.out.println("========================================");
    }
}
