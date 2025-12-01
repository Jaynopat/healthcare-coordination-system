package model;

import java.sql.Timestamp;

/**
 * Prescription model class - represents prescriptions table in database
 * Work Request #2: Inter-enterprise communication (Clinic → Pharmacy)
 */
public class Prescription {
    private int prescriptionId;
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private int medicationId;
    private String dosageInstructions;
    private int quantity;
    private int refills;
    private String status;  // PENDING, FILLED, READY_FOR_PICKUP, COMPLETED
    private Timestamp issuedDate;
    private Timestamp filledDate;
    private Integer pharmacistId;
    private String pharmacistNotes;
    
    // For display purposes
    private String patientName;
    private String doctorName;
    private String medicationName;
    private String pharmacistName;
    
    // Empty constructor
    public Prescription() {
    }
    
    // Constructor with main fields
    public Prescription(int appointmentId, int patientId, int doctorId, int medicationId, 
                       String dosageInstructions, int quantity) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.medicationId = medicationId;
        this.dosageInstructions = dosageInstructions;
        this.quantity = quantity;
        this.status = "PENDING"; // Default status
        this.refills = 0;
    }

    // Getters and Setters
    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public String getDosageInstructions() {
        return dosageInstructions;
    }

    public void setDosageInstructions(String dosageInstructions) {
        this.dosageInstructions = dosageInstructions;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRefills() {
        return refills;
    }

    public void setRefills(int refills) {
        this.refills = refills;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Timestamp issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Timestamp getFilledDate() {
        return filledDate;
    }

    public void setFilledDate(Timestamp filledDate) {
        this.filledDate = filledDate;
    }

    public Integer getPharmacistId() {
        return pharmacistId;
    }

    public void setPharmacistId(Integer pharmacistId) {
        this.pharmacistId = pharmacistId;
    }

    public String getPharmacistNotes() {
        return pharmacistNotes;
    }

    public void setPharmacistNotes(String pharmacistNotes) {
        this.pharmacistNotes = pharmacistNotes;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getPharmacistName() {
        return pharmacistName;
    }

    public void setPharmacistName(String pharmacistName) {
        this.pharmacistName = pharmacistName;
    }
}