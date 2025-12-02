package database;

import model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PatientDAO - Data Access Object for Patient operations
 * Handles all database operations for patients table
 */
public class PatientDAO {
    
    /**
     * CREATE - Add new patient to database
     */
    public boolean createPatient(Patient patient) {
        String sql = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, " +
                     "phone, email, address, blood_group, allergies) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, patient.getDateOfBirth());
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getBloodGroup());
            stmt.setString(9, patient.getAllergies());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the auto-generated patient_id
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    patient.setPatientId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Patient created: " + patient.getFullName());
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating patient:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * READ - Get all patients
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY last_name, first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Patient patient = extractPatientFromResultSet(rs);
                patients.add(patient);
            }
            
            System.out.println("✅ Retrieved " + patients.size() + " patients");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving patients:");
            e.printStackTrace();
        }
        
        return patients;
    }
    
    /**
     * READ - Get patient by ID
     */
    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPatientFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving patient:");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * READ - Search patients by name
     */
    public List<Patient> searchPatientsByName(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? " +
                     "ORDER BY last_name, first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Patient patient = extractPatientFromResultSet(rs);
                patients.add(patient);
            }
            
            System.out.println("✅ Found " + patients.size() + " patients matching: " + searchTerm);
            
        } catch (SQLException e) {
            System.err.println("❌ Error searching patients:");
            e.printStackTrace();
        }
        
        return patients;
    }
    
    /**
     * UPDATE - Update patient information
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                     "gender = ?, phone = ?, email = ?, address = ?, blood_group = ?, allergies = ? " +
                     "WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, patient.getDateOfBirth());
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getBloodGroup());
            stmt.setString(9, patient.getAllergies());
            stmt.setInt(10, patient.getPatientId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Patient updated: " + patient.getFullName());
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating patient:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * DELETE - Delete patient by ID
     */
    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Patient deleted successfully");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting patient:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to extract Patient object from ResultSet
     */
    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth"));
        patient.setGender(rs.getString("gender"));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setAllergies(rs.getString("allergies"));
        patient.setCreatedDate(rs.getTimestamp("created_date"));
        return patient;
    }
    
    /**
     * TEST METHOD - Test PatientDAO operations
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Testing PatientDAO Operations");
        System.out.println("========================================\n");
        
        PatientDAO patientDAO = new PatientDAO();
        
        // Test 1: Get all patients
        System.out.println("TEST 1: Get all patients");
        List<Patient> allPatients = patientDAO.getAllPatients();
        System.out.println("Found " + allPatients.size() + " patients:");
        for (Patient p : allPatients) {
            System.out.println("   - " + p.getFullName() + " | " + p.getGender() + 
                             " | Blood: " + p.getBloodGroup());
        }
        System.out.println();
        
        // Test 2: Search for a patient
        System.out.println("TEST 2: Search for 'John'");
        List<Patient> searchResults = patientDAO.searchPatientsByName("John");
        System.out.println("Found " + searchResults.size() + " patient(s):");
        for (Patient p : searchResults) {
            System.out.println("   - " + p.getFullName() + " | Phone: " + p.getPhone());
        }
        System.out.println();
        
        // Test 3: Get patient by ID
        System.out.println("TEST 3: Get patient with ID = 1");
        Patient patient = patientDAO.getPatientById(1);
        if (patient != null) {
            System.out.println("✅ Patient found:");
            System.out.println("   Name: " + patient.getFullName());
            System.out.println("   Email: " + patient.getEmail());
            System.out.println("   Allergies: " + patient.getAllergies());
        }
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("All PatientDAO tests completed!");
        System.out.println("========================================");
    }
}