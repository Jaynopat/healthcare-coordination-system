package model;

import java.sql.Timestamp;

/**
 * RestockRequest model class - represents restock_requests table in database
 * Work Request #4: Inventory restocking within pharmacy
 */
public class RestockRequest {
    private int requestId;
    private int medicationId;
    private int requestedQuantity;
    private int currentStock;
    private String priority;  // LOW, MEDIUM, HIGH, URGENT
    private String reason;
    private String status;  // PENDING, APPROVED, ORDERED, RECEIVED
    private int requestedBy;
    private Timestamp requestedDate;
    private Integer approvedBy;
    private Timestamp approvedDate;
    private String managerNotes;
    
    // For display purposes
    private String medicationName;
    private String requesterName;
    private String approverName;
    
    // Empty constructor
    public RestockRequest() {
    }
    
    // Constructor with main fields
    public RestockRequest(int medicationId, int requestedQuantity, int currentStock, 
                         String priority, String reason, int requestedBy) {
        this.medicationId = medicationId;
        this.requestedQuantity = requestedQuantity;
        this.currentStock = currentStock;
        this.priority = priority;
        this.reason = reason;
        this.requestedBy = requestedBy;
        this.status = "PENDING"; // Default status
    }

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(int requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(int requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Timestamp getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Timestamp requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Timestamp getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Timestamp approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getManagerNotes() {
        return managerNotes;
    }

    public void setManagerNotes(String managerNotes) {
        this.managerNotes = managerNotes;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }
}
