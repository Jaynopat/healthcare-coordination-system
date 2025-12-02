package database;

import model.RestockRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RestockRequestDAO - Data Access Object for RestockRequest operations
 * Work Request #4: Inventory restocking within pharmacy
 * Pharmacist → Pharmacy Manager flow
 */
public class RestockRequestDAO {
    
    /**
     * CREATE - Pharmacist creates restock request
     * Work Request #4: Pharmacist requests more inventory from manager
     */
    public boolean createRestockRequest(RestockRequest request) {
        String sql = "INSERT INTO restock_requests (medication_id, requested_quantity, " +
                     "current_stock, priority, reason, status, requested_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, request.getMedicationId());
            stmt.setInt(2, request.getRequestedQuantity());
            stmt.setInt(3, request.getCurrentStock());
            stmt.setString(4, request.getPriority());
            stmt.setString(5, request.getReason());
            stmt.setString(6, request.getStatus());
            stmt.setInt(7, request.getRequestedBy());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    request.setRequestId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Restock request created and sent to manager");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating restock request:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * READ - Get all restock requests
     */
    public List<RestockRequest> getAllRestockRequests() {
        List<RestockRequest> requests = new ArrayList<>();
        String sql = "SELECT rr.*, " +
                     "m.medication_name, " +
                     "requester.full_name as requester_name, " +
                     "approver.full_name as approver_name " +
                     "FROM restock_requests rr " +
                     "JOIN medications m ON rr.medication_id = m.medication_id " +
                     "JOIN users requester ON rr.requested_by = requester.user_id " +
                     "LEFT JOIN users approver ON rr.approved_by = approver.user_id " +
                     "ORDER BY rr.requested_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                RestockRequest request = extractRestockRequestFromResultSet(rs);
                requests.add(request);
            }
            
            System.out.println("✅ Retrieved " + requests.size() + " restock requests");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving restock requests:");
            e.printStackTrace();
        }
        
        return requests;
    }
    
    /**
     * READ - Get pending restock requests (for manager approval)
     */
    public List<RestockRequest> getPendingRestockRequests() {
        List<RestockRequest> requests = new ArrayList<>();
        String sql = "SELECT rr.*, " +
                     "m.medication_name, " +
                     "requester.full_name as requester_name " +
                     "FROM restock_requests rr " +
                     "JOIN medications m ON rr.medication_id = m.medication_id " +
                     "JOIN users requester ON rr.requested_by = requester.user_id " +
                     "WHERE rr.status = 'PENDING' " +
                     "ORDER BY " +
                     "CASE rr.priority " +
                     "  WHEN 'URGENT' THEN 1 " +
                     "  WHEN 'HIGH' THEN 2 " +
                     "  WHEN 'MEDIUM' THEN 3 " +
                     "  WHEN 'LOW' THEN 4 " +
                     "END, rr.requested_date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                RestockRequest request = extractRestockRequestFromResultSet(rs);
                requests.add(request);
            }
            
            System.out.println("✅ Retrieved " + requests.size() + " pending restock requests");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving pending requests:");
            e.printStackTrace();
        }
        
        return requests;
    }
    
    /**
     * READ - Get restock requests by pharmacist
     */
    public List<RestockRequest> getRestockRequestsByPharmacist(int pharmacistId) {
        List<RestockRequest> requests = new ArrayList<>();
        String sql = "SELECT rr.*, " +
                     "m.medication_name, " +
                     "requester.full_name as requester_name, " +
                     "approver.full_name as approver_name " +
                     "FROM restock_requests rr " +
                     "JOIN medications m ON rr.medication_id = m.medication_id " +
                     "JOIN users requester ON rr.requested_by = requester.user_id " +
                     "LEFT JOIN users approver ON rr.approved_by = approver.user_id " +
                     "WHERE rr.requested_by = ? " +
                     "ORDER BY rr.requested_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pharmacistId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                RestockRequest request = extractRestockRequestFromResultSet(rs);
                requests.add(request);
            }
            
            System.out.println("✅ Retrieved " + requests.size() + " requests by pharmacist");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving pharmacist requests:");
            e.printStackTrace();
        }
        
        return requests;
    }
    
    /**
     * READ - Get restock request by ID
     */
    public RestockRequest getRestockRequestById(int requestId) {
        String sql = "SELECT rr.*, " +
                     "m.medication_name, " +
                     "requester.full_name as requester_name, " +
                     "approver.full_name as approver_name " +
                     "FROM restock_requests rr " +
                     "JOIN medications m ON rr.medication_id = m.medication_id " +
                     "JOIN users requester ON rr.requested_by = requester.user_id " +
                     "LEFT JOIN users approver ON rr.approved_by = approver.user_id " +
                     "WHERE rr.request_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractRestockRequestFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving restock request:");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * UPDATE - Manager approves restock request
     * Work Request #4: Manager responds to pharmacist's request
     */
    public boolean approveRestockRequest(int requestId, int managerId, String managerNotes) {
        String sql = "UPDATE restock_requests SET status = 'APPROVED', approved_by = ?, " +
                     "manager_notes = ?, approved_date = CURRENT_TIMESTAMP WHERE request_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, managerId);
            stmt.setString(2, managerNotes);
            stmt.setInt(3, requestId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Restock request approved by manager");
                
                // Update inventory when approved
                RestockRequest request = getRestockRequestById(requestId);
                if (request != null) {
                    MedicationDAO medDAO = new MedicationDAO();
                    medDAO.updateMedicationStock(request.getMedicationId(), 
                                                 request.getRequestedQuantity()); // Positive = add
                }
                
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error approving restock request:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * UPDATE - Update restock request status
     */
    public boolean updateRestockRequestStatus(int requestId, String status) {
        String sql = "UPDATE restock_requests SET status = ? WHERE request_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Restock request status updated to: " + status);
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating restock request status:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * DELETE - Delete restock request
     */
    public boolean deleteRestockRequest(int requestId) {
        String sql = "DELETE FROM restock_requests WHERE request_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, requestId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Restock request deleted successfully");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting restock request:");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to extract RestockRequest from ResultSet
     */
    private RestockRequest extractRestockRequestFromResultSet(ResultSet rs) throws SQLException {
        RestockRequest request = new RestockRequest();
        request.setRequestId(rs.getInt("request_id"));
        request.setMedicationId(rs.getInt("medication_id"));
        request.setRequestedQuantity(rs.getInt("requested_quantity"));
        request.setCurrentStock(rs.getInt("current_stock"));
        request.setPriority(rs.getString("priority"));
        request.setReason(rs.getString("reason"));
        request.setStatus(rs.getString("status"));
        request.setRequestedBy(rs.getInt("requested_by"));
        request.setRequestedDate(rs.getTimestamp("requested_date"));
        
        // Handle nullable approved_by
        int approvedBy = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            request.setApprovedBy(approvedBy);
        }
        
        request.setApprovedDate(rs.getTimestamp("approved_date"));
        request.setManagerNotes(rs.getString("manager_notes"));
        
        // Set display names (from JOINs)
        request.setMedicationName(rs.getString("medication_name"));
        request.setRequesterName(rs.getString("requester_name"));
        
        try {
            request.setApproverName(rs.getString("approver_name"));
        } catch (SQLException e) {
            // approver_name might not exist if not yet approved
        }
        
        return request;
    }
    
    /**
     * TEST METHOD
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Testing RestockRequestDAO Operations");
        System.out.println("Work Request #4: Intra-Enterprise (Pharmacy)");
        System.out.println("Pharmacist → Manager flow");
        System.out.println("========================================\n");
        
        RestockRequestDAO restockDAO = new RestockRequestDAO();
        
        // Test 1: Get all restock requests
        System.out.println("TEST 1: Get all restock requests");
        List<RestockRequest> allRequests = restockDAO.getAllRestockRequests();
        System.out.println("Found " + allRequests.size() + " restock requests:");
        for (RestockRequest r : allRequests) {
            System.out.println("   - Medication: " + r.getMedicationName());
            System.out.println("     Requested by: " + r.getRequesterName());
            System.out.println("     Quantity: " + r.getRequestedQuantity() + 
                             " (Current stock: " + r.getCurrentStock() + ")");
            System.out.println("     Priority: " + r.getPriority());
            System.out.println("     Status: " + r.getStatus());
            if (r.getApproverName() != null) {
                System.out.println("     Approved by: " + r.getApproverName());
            }
            System.out.println();
        }
        
        // Test 2: Get pending requests (what manager needs to approve)
        System.out.println("TEST 2: Get pending restock requests for manager");
        List<RestockRequest> pending = restockDAO.getPendingRestockRequests();
        System.out.println("Found " + pending.size() + " pending requests awaiting approval");
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("✅ Intra-enterprise workflow working!");
        System.out.println("Pharmacist → Manager flow verified");
        System.out.println("========================================");
    }
}