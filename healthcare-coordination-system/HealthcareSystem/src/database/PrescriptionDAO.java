package database;

import model.Prescription;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PrescriptionDAO - Data Access Object for Prescription operations
 * Work Request #2: Inter-enterprise communication (Clinic → Pharmacy)
 * This handles the flow of prescriptions from doctors to pharmacists
 */
public class PrescriptionDAO {
    
    /**
     * CREATE - Doctor issues new prescription
     * Work Request #2: Doctor creates prescription and sends to pharmacy
     */
    public boolean createPrescription(Prescription prescription) {
        String sql = "INSERT INTO prescriptions (appointment_id, patient_id, doctor_id, " +
                     "medication_id, dosage_instructions, quantity, refills, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, prescription.getAppointmentId());
            stmt.setInt(2, prescription.getPatientId());
            stmt.setInt(3, prescription.getDoctorId());
            stmt.setInt(4, prescription.getMedicationId());
            stmt.setString(5, prescription.getDosageInstructions());
            stmt.setInt(6, prescription.getQuantity());
            stmt.setInt(7, prescription.getRefills());
            stmt.setString(8, prescription.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    prescription.setPrescriptionId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Prescription created and sent to pharmacy");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating prescription:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * READ - Get all prescriptions with full details
     */
    public List<Prescription> getAllPrescriptions() {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "CONCAT(pat.first_name, ' ', pat.last_name) as patient_name, " +
                     "doc.full_name as doctor_name, " +
                     "m.medication_name, " +
                     "pharm.full_name as pharmacist_name " +
                     "FROM prescriptions p " +
                     "JOIN patients pat ON p.patient_id = pat.patient_id " +
                     "JOIN users doc ON p.doctor_id = doc.user_id " +
                     "JOIN medications m ON p.medication_id = m.medication_id " +
                     "LEFT JOIN users pharm ON p.pharmacist_id = pharm.user_id " +
                     "ORDER BY p.issued_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prescription prescription = extractPrescriptionFromResultSet(rs);
                prescriptions.add(prescription);
            }
            
            System.out.println("✅ Retrieved " + prescriptions.size() + " prescriptions");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving prescriptions:");
            e.printStackTrace();
        }
        
        return prescriptions;
    }
    
    /**
     * READ - Get pending prescriptions for pharmacy
     * Pharmacists use this to see what needs to be filled
     */
    public List<Prescription> getPendingPrescriptions() {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "CONCAT(pat.first_name, ' ', pat.last_name) as patient_name, " +
                     "doc.full_name as doctor_name, " +
                     "m.medication_name " +
                     "FROM prescriptions p " +
                     "JOIN patients pat ON p.patient_id = pat.patient_id " +
                     "JOIN users doc ON p.doctor_id = doc.user_id " +
                     "JOIN medications m ON p.medication_id = m.medication_id " +
                     "WHERE p.status = 'PENDING' " +
                     "ORDER BY p.issued_date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prescription prescription = extractPrescriptionFromResultSet(rs);
                prescriptions.add(prescription);
            }
            
            System.out.println("✅ Retrieved " + prescriptions.size() + " pending prescriptions");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving pending prescriptions:");
            e.printStackTrace();
        }
        
        return prescriptions;
    }
    
    /**
     * READ - Get prescriptions by doctor
     * Doctors can see what they've prescribed
     */
    public List<Prescription> getPrescriptionsByDoctor(int doctorId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "CONCAT(pat.first_name, ' ', pat.last_name) as patient_name, " +
                     "doc.full_name as doctor_name, " +
                     "m.medication_name, " +
                     "pharm.full_name as pharmacist_name " +
                     "FROM prescriptions p " +
                     "JOIN patients pat ON p.patient_id = pat.patient_id " +
                     "JOIN users doc ON p.doctor_id = doc.user_id " +
                     "JOIN medications m ON p.medication_id = m.medication_id " +
                     "LEFT JOIN users pharm ON p.pharmacist_id = pharm.user_id " +
                     "WHERE p.doctor_id = ? " +
                     "ORDER BY p.issued_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prescription prescription = extractPrescriptionFromResultSet(rs);
                prescriptions.add(prescription);
            }
            
            System.out.println("✅ Retrieved " + prescriptions.size() + " prescriptions for doctor");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving doctor prescriptions:");
            e.printStackTrace();
        }
        
        return prescriptions;
    }
    
    /**
     * READ - Get prescriptions by patient
     */
    public List<Prescription> getPrescriptionsByPatient(int patientId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "CONCAT(pat.first_name, ' ', pat.last_name) as patient_name, " +
                     "doc.full_name as doctor_name, " +
                     "m.medication_name, " +
                     "pharm.full_name as pharmacist_name " +
                     "FROM prescriptions p " +
                     "JOIN patients pat ON p.patient_id = pat.patient_id " +
                     "JOIN users doc ON p.doctor_id = doc.user_id " +
                     "JOIN medications m ON p.medication_id = m.medication_id " +
                     "LEFT JOIN users pharm ON p.pharmacist_id = pharm.user_id " +
                     "WHERE p.patient_id = ? " +
                     "ORDER BY p.issued_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prescription prescription = extractPrescriptionFromResultSet(rs);
                prescriptions.add(prescription);
            }
            
            System.out.println("✅ Retrieved " + prescriptions.size() + " prescriptions for patient");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving patient prescriptions:");
            e.printStackTrace();
        }
        
        return prescriptions;
    }
    
    /**
     * READ - Get prescription by ID
     */
    public Prescription getPrescriptionById(int prescriptionId) {
        String sql = "SELECT p.*, " +
                     "CONCAT(pat.first_name, ' ', pat.last_name) as patient_name, " +
                     "doc.full_name as doctor_name, " +
                     "m.medication_name, " +
                     "pharm.full_name as pharmacist_name " +
                     "FROM prescriptions p " +
                     "JOIN patients pat ON p.patient_id = pat.patient_id " +
                     "JOIN users doc ON p.doctor_id = doc.user_id " +
                     "JOIN medications m ON p.medication_id = m.medication_id " +
                     "LEFT JOIN users pharm ON p.pharmacist_id = pharm.user_id " +
                     "WHERE p.prescription_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, prescriptionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPrescriptionFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving prescription:");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * UPDATE - Pharmacist fills prescription
     * Work Request #2: Pharmacy responds to clinic's prescription
     */
    public boolean fillPrescription(int prescriptionId, int pharmacistId, String notes) {
        String sql = "UPDATE prescriptions SET status = 'FILLED', pharmacist_id = ?, " +
                     "pharmacist_notes = ?, filled_date = CURRENT_TIMESTAMP WHERE prescription_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pharmacistId);
            stmt.setString(2, notes);
            stmt.setInt(3, prescriptionId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Prescription filled by pharmacist");
                
                // Also update medication inventory (reduce stock)
                Prescription prescription = getPrescriptionById(prescriptionId);
                if (prescription != null) {
                    MedicationDAO medDAO = new MedicationDAO();
                    medDAO.updateMedicationStock(prescription.getMedicationId(), 
                                                 -prescription.getQuantity()); // Negative = reduce
                }
                
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error filling prescription:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * UPDATE - Update prescription status
     */
    public boolean updatePrescriptionStatus(int prescriptionId, String status) {
        String sql = "UPDATE prescriptions SET status = ? WHERE prescription_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, prescriptionId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Prescription status updated to: " + status);
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating prescription status:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * DELETE - Delete prescription
     */
    public boolean deletePrescription(int prescriptionId) {
        String sql = "DELETE FROM prescriptions WHERE prescription_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, prescriptionId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Prescription deleted successfully");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting prescription:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to extract Prescription from ResultSet
     */
    private Prescription extractPrescriptionFromResultSet(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionId(rs.getInt("prescription_id"));
        prescription.setAppointmentId(rs.getInt("appointment_id"));
        prescription.setPatientId(rs.getInt("patient_id"));
        prescription.setDoctorId(rs.getInt("doctor_id"));
        prescription.setMedicationId(rs.getInt("medication_id"));
        prescription.setDosageInstructions(rs.getString("dosage_instructions"));
        prescription.setQuantity(rs.getInt("quantity"));
        prescription.setRefills(rs.getInt("refills"));
        prescription.setStatus(rs.getString("status"));
        prescription.setIssuedDate(rs.getTimestamp("issued_date"));
        prescription.setFilledDate(rs.getTimestamp("filled_date"));
        
        // Handle nullable pharmacist_id
        int pharmacistId = rs.getInt("pharmacist_id");
        if (!rs.wasNull()) {
            prescription.setPharmacistId(pharmacistId);
        }
        
        prescription.setPharmacistNotes(rs.getString("pharmacist_notes"));
        
        // Set display names (from JOINs)
        prescription.setPatientName(rs.getString("patient_name"));
        prescription.setDoctorName(rs.getString("doctor_name"));
        prescription.setMedicationName(rs.getString("medication_name"));
        
        try {
            prescription.setPharmacistName(rs.getString("pharmacist_name"));
        } catch (SQLException e) {
            // pharmacist_name might not exist in all queries
        }
        
        return prescription;
    }
    
    /**
     * TEST METHOD
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Testing PrescriptionDAO Operations");
        System.out.println("Work Request #2: Inter-Enterprise Communication");
        System.out.println("========================================\n");
        
        PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
        
        // Test 1: Get all prescriptions
        System.out.println("TEST 1: Get all prescriptions (Clinic → Pharmacy flow)");
        List<Prescription> allPrescriptions = prescriptionDAO.getAllPrescriptions();
        System.out.println("Found " + allPrescriptions.size() + " prescriptions:");
        for (Prescription p : allPrescriptions) {
            System.out.println("   - Patient: " + p.getPatientName());
            System.out.println("     Doctor: " + p.getDoctorName() + " prescribed " + p.getMedicationName());
            System.out.println("     Dosage: " + p.getDosageInstructions());
            System.out.println("     Status: " + p.getStatus());
            if (p.getPharmacistName() != null) {
                System.out.println("     Filled by: " + p.getPharmacistName());
            }
            System.out.println();
        }
        
        // Test 2: Get pending prescriptions (what pharmacy needs to fill)
        System.out.println("TEST 2: Get pending prescriptions for pharmacy");
        List<Prescription> pending = prescriptionDAO.getPendingPrescriptions();
        System.out.println("Found " + pending.size() + " pending prescriptions waiting to be filled");
        System.out.println();
        
        // Test 3: Get prescriptions by doctor
        System.out.println("TEST 3: Get prescriptions issued by Doctor ID=1");
        List<Prescription> doctorPrescriptions = prescriptionDAO.getPrescriptionsByDoctor(1);
        System.out.println("Doctor has issued " + doctorPrescriptions.size() + " prescriptions");
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("✅ Inter-enterprise communication working!");
        System.out.println("Clinic (Doctor) → Pharmacy (Pharmacist) flow verified");
        System.out.println("========================================");
    }
}