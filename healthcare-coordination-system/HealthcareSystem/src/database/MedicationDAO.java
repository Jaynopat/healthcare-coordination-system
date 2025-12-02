package database;

import model.Medication;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MedicationDAO - Data Access Object for Medication operations
 * Handles medications and pharmacy inventory
 */
public class MedicationDAO {
    
    /**
     * CREATE - Add new medication to catalog
     */
    public boolean createMedication(Medication medication) {
        String sql = "INSERT INTO medications (medication_name, generic_name, category, " +
                     "dosage_form, strength, manufacturer, unit_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, medication.getMedicationName());
            stmt.setString(2, medication.getGenericName());
            stmt.setString(3, medication.getCategory());
            stmt.setString(4, medication.getDosageForm());
            stmt.setString(5, medication.getStrength());
            stmt.setString(6, medication.getManufacturer());
            stmt.setBigDecimal(7, medication.getUnitPrice());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    medication.setMedicationId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Medication created: " + medication.getMedicationName());
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating medication:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * READ - Get all medications
     */
    public List<Medication> getAllMedications() {
        List<Medication> medications = new ArrayList<>();
        String sql = "SELECT * FROM medications ORDER BY medication_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medication medication = extractMedicationFromResultSet(rs);
                medications.add(medication);
            }
            
            System.out.println("✅ Retrieved " + medications.size() + " medications");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving medications:");
            e.printStackTrace();
        }
        
        return medications;
    }
    
    /**
     * READ - Get medication by ID
     */
    public Medication getMedicationById(int medicationId) {
        String sql = "SELECT * FROM medications WHERE medication_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, medicationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractMedicationFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving medication:");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * READ - Search medications by name or category
     */
    public List<Medication> searchMedications(String searchTerm) {
        List<Medication> medications = new ArrayList<>();
        String sql = "SELECT * FROM medications WHERE medication_name LIKE ? OR category LIKE ? " +
                     "ORDER BY medication_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Medication medication = extractMedicationFromResultSet(rs);
                medications.add(medication);
            }
            
            System.out.println("✅ Found " + medications.size() + " medications matching: " + searchTerm);
            
        } catch (SQLException e) {
            System.err.println("❌ Error searching medications:");
            e.printStackTrace();
        }
        
        return medications;
    }
    
    /**
     * READ - Get pharmacy inventory with stock levels
     * Work Request #3: Check medication availability
     */
    public List<Medication> getMedicationsWithInventory() {
        List<Medication> medications = new ArrayList<>();
        String sql = "SELECT m.*, pi.quantity_available, pi.reorder_level " +
                     "FROM medications m " +
                     "LEFT JOIN pharmacy_inventory pi ON m.medication_id = pi.medication_id " +
                     "ORDER BY m.medication_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medication medication = extractMedicationFromResultSet(rs);
                // Note: quantity_available and reorder_level would need additional fields in Medication model
                // For now, we just get the medication info
                medications.add(medication);
            }
            
            System.out.println("✅ Retrieved " + medications.size() + " medications with inventory");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving inventory:");
            e.printStackTrace();
        }
        
        return medications;
    }
    
    /**
     * READ - Check if medication is in stock
     */
    public int getMedicationStock(int medicationId) {
        String sql = "SELECT quantity_available FROM pharmacy_inventory WHERE medication_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, medicationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int stock = rs.getInt("quantity_available");
                System.out.println("✅ Stock level for medication ID " + medicationId + ": " + stock);
                return stock;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking stock:");
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * UPDATE - Update medication stock after prescription filled
     */
    public boolean updateMedicationStock(int medicationId, int quantityChange) {
        String sql = "UPDATE pharmacy_inventory SET quantity_available = quantity_available + ? " +
                     "WHERE medication_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantityChange); // Can be negative when dispensing
            stmt.setInt(2, medicationId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Stock updated for medication ID " + medicationId);
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating stock:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * UPDATE - Update medication information
     */
    public boolean updateMedication(Medication medication) {
        String sql = "UPDATE medications SET medication_name = ?, generic_name = ?, category = ?, " +
                     "dosage_form = ?, strength = ?, manufacturer = ?, unit_price = ? " +
                     "WHERE medication_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, medication.getMedicationName());
            stmt.setString(2, medication.getGenericName());
            stmt.setString(3, medication.getCategory());
            stmt.setString(4, medication.getDosageForm());
            stmt.setString(5, medication.getStrength());
            stmt.setString(6, medication.getManufacturer());
            stmt.setBigDecimal(7, medication.getUnitPrice());
            stmt.setInt(8, medication.getMedicationId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Medication updated: " + medication.getMedicationName());
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating medication:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * DELETE - Delete medication
     */
    public boolean deleteMedication(int medicationId) {
        String sql = "DELETE FROM medications WHERE medication_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, medicationId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Medication deleted successfully");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting medication:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to extract Medication object from ResultSet
     */
    private Medication extractMedicationFromResultSet(ResultSet rs) throws SQLException {
        Medication medication = new Medication();
        medication.setMedicationId(rs.getInt("medication_id"));
        medication.setMedicationName(rs.getString("medication_name"));
        medication.setGenericName(rs.getString("generic_name"));
        medication.setCategory(rs.getString("category"));
        medication.setDosageForm(rs.getString("dosage_form"));
        medication.setStrength(rs.getString("strength"));
        medication.setManufacturer(rs.getString("manufacturer"));
        medication.setUnitPrice(rs.getBigDecimal("unit_price"));
        return medication;
    }
    
    /**
     * TEST METHOD
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Testing MedicationDAO Operations");
        System.out.println("========================================\n");
        
        MedicationDAO medicationDAO = new MedicationDAO();
        
        // Test 1: Get all medications
        System.out.println("TEST 1: Get all medications");
        List<Medication> allMeds = medicationDAO.getAllMedications();
        System.out.println("Found " + allMeds.size() + " medications:");
        for (Medication m : allMeds) {
            System.out.println("   - " + m.getMedicationName() + " " + m.getStrength() + 
                             " | Category: " + m.getCategory() + " | Price: $" + m.getUnitPrice());
        }
        System.out.println();
        
        // Test 2: Check stock level
        System.out.println("TEST 2: Check stock for Amoxicillin (ID=1)");
        int stock = medicationDAO.getMedicationStock(1);
        System.out.println("Current stock: " + stock + " units");
        System.out.println();
        
        // Test 3: Search medications
        System.out.println("TEST 3: Search for 'Tablet'");
        List<Medication> searchResults = medicationDAO.searchMedications("Tablet");
        System.out.println("Found " + searchResults.size() + " tablet medications");
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("All MedicationDAO tests completed!");
        System.out.println("========================================");
    }
}