package model;

import java.math.BigDecimal;

/**
 * Medication model class - represents medications table in database
 */
public class Medication {
    private int medicationId;
    private String medicationName;
    private String genericName;
    private String category;
    private String dosageForm;
    private String strength;
    private String manufacturer;
    private BigDecimal unitPrice;
    
    // Empty constructor
    public Medication() {
    }
    
    // Constructor with main fields
    public Medication(String medicationName, String category, String strength) {
        this.medicationName = medicationName;
        this.category = category;
        this.strength = strength;
    }

    // Getters and Setters
    public int getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    // toString for display
    @Override
    public String toString() {
        return medicationName + " " + strength;
    }
}