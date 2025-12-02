package database;

import model.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentDAO - Data Access Object for Appointment operations
 * Work Request #1: Appointment booking within clinic
 */
public class AppointmentDAO {
    
    /**
     * CREATE - Schedule new appointment
     * Work Request #1: Patient books appointment with doctor
     */
    public boolean createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, " +
                     "appointment_time, reason, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, appointment.getAppointmentDate());
            stmt.setTime(4, appointment.getAppointmentTime());
            stmt.setString(5, appointment.getReason());
            stmt.setString(6, appointment.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    appointment.setAppointmentId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Appointment scheduled successfully");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating appointment:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * READ - Get all appointments with patient and doctor names
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, " +
                     "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                     "u.full_name as doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN users u ON a.doctor_id = u.user_id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Appointment appointment = extractAppointmentFromResultSet(rs);
                appointments.add(appointment);
            }
            
            System.out.println("✅ Retrieved " + appointments.size() + " appointments");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving appointments:");
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * READ - Get appointments for specific doctor
     */
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, " +
                     "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                     "u.full_name as doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN users u ON a.doctor_id = u.user_id " +
                     "WHERE a.doctor_id = ? " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = extractAppointmentFromResultSet(rs);
                appointments.add(appointment);
            }
            
            System.out.println("✅ Retrieved " + appointments.size() + " appointments for doctor");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving doctor appointments:");
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * READ - Get appointments for specific patient
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, " +
                     "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                     "u.full_name as doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN users u ON a.doctor_id = u.user_id " +
                     "WHERE a.patient_id = ? " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = extractAppointmentFromResultSet(rs);
                appointments.add(appointment);
            }
            
            System.out.println("✅ Retrieved " + appointments.size() + " appointments for patient");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving patient appointments:");
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * READ - Get appointment by ID
     */
    public Appointment getAppointmentById(int appointmentId) {
        String sql = "SELECT a.*, " +
                     "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                     "u.full_name as doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN users u ON a.doctor_id = u.user_id " +
                     "WHERE a.appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAppointmentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving appointment:");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * UPDATE - Complete appointment with diagnosis
     */
    public boolean completeAppointment(int appointmentId, String diagnosis, String notes) {
        String sql = "UPDATE appointments SET status = 'COMPLETED', diagnosis = ?, notes = ? " +
                     "WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, diagnosis);
            stmt.setString(2, notes);
            stmt.setInt(3, appointmentId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Appointment completed with diagnosis");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error completing appointment:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * UPDATE - Update appointment status
     */
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Appointment status updated to: " + status);
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating appointment status:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * UPDATE - Update appointment details
     */
    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET appointment_date = ?, appointment_time = ?, " +
                     "reason = ?, status = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, appointment.getAppointmentDate());
            stmt.setTime(2, appointment.getAppointmentTime());
            stmt.setString(3, appointment.getReason());
            stmt.setString(4, appointment.getStatus());
            stmt.setInt(5, appointment.getAppointmentId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Appointment updated successfully");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating appointment:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * DELETE - Cancel/delete appointment
     */
    public boolean deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, appointmentId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Appointment deleted successfully");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting appointment:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to extract Appointment from ResultSet
     */
    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date"));
        appointment.setAppointmentTime(rs.getTime("appointment_time"));
        appointment.setReason(rs.getString("reason"));
        appointment.setStatus(rs.getString("status"));
        appointment.setDiagnosis(rs.getString("diagnosis"));
        appointment.setNotes(rs.getString("notes"));
        appointment.setCreatedDate(rs.getTimestamp("created_date"));
        
        // Set display names (from JOIN)
        appointment.setPatientName(rs.getString("patient_name"));
        appointment.setDoctorName(rs.getString("doctor_name"));
        
        return appointment;
    }
    
    /**
     * TEST METHOD
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Testing AppointmentDAO Operations");
        System.out.println("========================================\n");
        
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        
        // Test 1: Get all appointments
        System.out.println("TEST 1: Get all appointments");
        List<Appointment> allAppts = appointmentDAO.getAllAppointments();
        System.out.println("Found " + allAppts.size() + " appointments:");
        for (Appointment a : allAppts) {
            System.out.println("   - " + a.getPatientName() + " → " + a.getDoctorName() + 
                             " | " + a.getAppointmentDate() + " " + a.getAppointmentTime() +
                             " | Status: " + a.getStatus());
        }
        System.out.println();
        
        // Test 2: Get appointments for doctor ID 1
        System.out.println("TEST 2: Get appointments for Doctor ID=1");
        List<Appointment> doctorAppts = appointmentDAO.getAppointmentsByDoctor(1);
        System.out.println("Found " + doctorAppts.size() + " appointments for this doctor");
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("All AppointmentDAO tests completed!");
        System.out.println("========================================");
    }
}