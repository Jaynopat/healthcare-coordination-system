package ui.pharmacist;

import model.User;
import model.Prescription;
import model.Medication;
import model.RestockRequest;
import database.PrescriptionDAO;
import database.MedicationDAO;
import database.RestockRequestDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PharmacistDashboard - Main screen for pharmacists
 * Work Request #2: Receives and fills prescriptions from doctors (Inter-enterprise)
 * Work Request #4: Creates restock requests for manager (Intra-enterprise)
 */
public class PharmacistDashboard extends JFrame {
    
    private User currentUser;
    
    // DAOs
    private PrescriptionDAO prescriptionDAO;
    private MedicationDAO medicationDAO;
    private RestockRequestDAO restockRequestDAO;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable pendingPrescriptionsTable;
    private JTable allPrescriptionsTable;
    private JTable inventoryTable;
    private JTable myRestockRequestsTable;
    private DefaultTableModel pendingPrescriptionsModel;
    private DefaultTableModel allPrescriptionsModel;
    private DefaultTableModel inventoryModel;
    private DefaultTableModel myRestockRequestsModel;
    
    public PharmacistDashboard(User user) {
        this.currentUser = user;
        this.prescriptionDAO = new PrescriptionDAO();
        this.medicationDAO = new MedicationDAO();
        this.restockRequestDAO = new RestockRequestDAO();
        
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        setTitle("Pharmacist Dashboard - " + currentUser.getFullName());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Tab 1: Pending Prescriptions (Work Request #2 - receiving side)
        tabbedPane.addTab("⚠ Pending Prescriptions", createPendingPrescriptionsPanel());
        
        // Tab 2: All Prescriptions
        tabbedPane.addTab("All Prescriptions", createAllPrescriptionsPanel());
        
        // Tab 3: Medication Inventory
        tabbedPane.addTab("Inventory", createInventoryPanel());
        
        // Tab 4: Request Restock (Work Request #4)
        tabbedPane.addTab("Request Restock", createRestockRequestPanel());
        
        // Tab 5: My Restock Requests
        tabbedPane.addTab("My Requests", createMyRequestsPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 139, 34)); // Green
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Pharmacist Dashboard - " + currentUser.getFullName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ui.LoginFrame().setVisible(true));
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * TAB 1: Pending Prescriptions from Doctors
     * WORK REQUEST #2: Inter-enterprise (receiving side)
     */
    private JPanel createPendingPrescriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title with alert
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("⚠ Pending Prescriptions from Clinic");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(200, 0, 0));
        
        JLabel infoLabel = new JLabel(" (Work Request #2: Received from Doctors)");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);
        
        titlePanel.add(titleLabel);
        titlePanel.add(infoLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Rx ID", "Patient", "Doctor", "Medication", "Dosage", "Qty", "Status", "Issued Date"};
        pendingPrescriptionsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pendingPrescriptionsTable = new JTable(pendingPrescriptionsModel);
        pendingPrescriptionsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        pendingPrescriptionsTable.setRowHeight(25);
        pendingPrescriptionsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(pendingPrescriptionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadPendingPrescriptions());
        
        JButton fillButton = new JButton("✓ Fill Selected Prescription");
        fillButton.setBackground(new Color(34, 139, 34));
        fillButton.setForeground(Color.WHITE);
        fillButton.setFont(new Font("Arial", Font.BOLD, 12));
        fillButton.addActionListener(e -> fillSelectedPrescription());
        
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> viewPrescriptionDetails());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(fillButton);
        buttonsPanel.add(viewDetailsButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 2: All Prescriptions Panel
     */
    private JPanel createAllPrescriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("All Prescriptions History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Rx ID", "Patient", "Doctor", "Medication", "Dosage", "Qty", "Status", "Filled By", "Filled Date"};
        allPrescriptionsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        allPrescriptionsTable = new JTable(allPrescriptionsModel);
        allPrescriptionsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        allPrescriptionsTable.setRowHeight(25);
        allPrescriptionsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(allPrescriptionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAllPrescriptions());
        buttonsPanel.add(refreshButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 3: Medication Inventory Panel
     */
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Medication Inventory");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Med ID", "Medication Name", "Category", "Strength", "Form", "Current Stock", "Price"};
        inventoryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(inventoryModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 12));
        inventoryTable.setRowHeight(25);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadInventory());
        
        JButton checkStockButton = new JButton("Check Stock Level");
        checkStockButton.addActionListener(e -> checkSelectedStock());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(checkStockButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 4: Request Restock Panel
     * WORK REQUEST #4: Intra-enterprise (Pharmacist → Manager)
     */
    private JPanel createRestockRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Request Medication Restock from Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Restock Request Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Medication
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Medication:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> medicationCombo = new JComboBox<>();
        loadMedicationsIntoCombo(medicationCombo);
        formPanel.add(medicationCombo, gbc);
        
        // Current Stock
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Current Stock:"), gbc);
        gbc.gridx = 1;
        JTextField currentStockField = new JTextField(20);
        formPanel.add(currentStockField, gbc);
        
        // Requested Quantity
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Requested Quantity:"), gbc);
        gbc.gridx = 1;
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 10));
        formPanel.add(quantitySpinner, gbc);
        
        // Priority
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH", "URGENT"});
        priorityCombo.setSelectedItem("MEDIUM");
        formPanel.add(priorityCombo, gbc);
        
        // Reason
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        JTextArea reasonArea = new JTextArea(3, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        formPanel.add(reasonScroll, gbc);
        
        // Info Label
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><i>Work Request #4: This request will be sent to Pharmacy Manager for approval.<br>" +
                "Status will be tracked: PENDING → APPROVED → RECEIVED</i></html>");
        infoLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(infoLabel, gbc);
        
        // Submit Button
        gbc.gridy = 6;
        JButton submitButton = new JButton("Submit Restock Request to Manager");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(255, 140, 0)); // Orange
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            createRestockRequest(
                medicationCombo,
                currentStockField.getText(),
                (Integer) quantitySpinner.getValue(),
                (String) priorityCombo.getSelectedItem(),
                reasonArea.getText()
            );
        });
        formPanel.add(submitButton, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * TAB 5: My Restock Requests Panel
     */
    private JPanel createMyRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("My Restock Requests");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Request ID", "Medication", "Requested Qty", "Current Stock", "Priority", "Status", "Requested Date"};
        myRestockRequestsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myRestockRequestsTable = new JTable(myRestockRequestsModel);
        myRestockRequestsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        myRestockRequestsTable.setRowHeight(25);
        myRestockRequestsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(myRestockRequestsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyRestockRequests());
        buttonsPanel.add(refreshButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Load pending prescriptions sent by doctors
     * WORK REQUEST #2: Receiving side
     */
    private void loadPendingPrescriptions() {
        pendingPrescriptionsModel.setRowCount(0);
        
        List<Prescription> prescriptions = prescriptionDAO.getPendingPrescriptions();
        
        for (Prescription rx : prescriptions) {
            Object[] row = {
                rx.getPrescriptionId(),
                rx.getPatientName(),
                rx.getDoctorName(),
                rx.getMedicationName(),
                rx.getDosageInstructions(),
                rx.getQuantity(),
                rx.getStatus(),
                rx.getIssuedDate()
            };
            pendingPrescriptionsModel.addRow(row);
        }
        
        // Show count in tab title
        tabbedPane.setTitleAt(0, "⚠ Pending Prescriptions (" + prescriptions.size() + ")");
    }
    
    /**
     * Load all prescriptions
     */
    private void loadAllPrescriptions() {
        allPrescriptionsModel.setRowCount(0);
        
        List<Prescription> prescriptions = prescriptionDAO.getAllPrescriptions();
        
        for (Prescription rx : prescriptions) {
            Object[] row = {
                rx.getPrescriptionId(),
                rx.getPatientName(),
                rx.getDoctorName(),
                rx.getMedicationName(),
                rx.getDosageInstructions(),
                rx.getQuantity(),
                rx.getStatus(),
                rx.getPharmacistName() != null ? rx.getPharmacistName() : "Not filled",
                rx.getFilledDate() != null ? rx.getFilledDate() : "N/A"
            };
            allPrescriptionsModel.addRow(row);
        }
    }
    
    /**
     * Load medication inventory
     */
    private void loadInventory() {
        inventoryModel.setRowCount(0);
        
        List<Medication> medications = medicationDAO.getAllMedications();
        
        for (Medication med : medications) {
            int stock = medicationDAO.getMedicationStock(med.getMedicationId());
            
            Object[] row = {
                med.getMedicationId(),
                med.getMedicationName(),
                med.getCategory(),
                med.getStrength(),
                med.getDosageForm(),
                stock,
                "$" + med.getUnitPrice()
            };
            inventoryModel.addRow(row);
        }
    }
    
    /**
     * Load medications into combo box
     */
    private void loadMedicationsIntoCombo(JComboBox<String> combo) {
        List<Medication> medications = medicationDAO.getAllMedications();
        for (Medication med : medications) {
            combo.addItem(med.getMedicationId() + " - " + med.getMedicationName() + " " + med.getStrength());
        }
    }
    
    /**
     * Load my restock requests
     */
    private void loadMyRestockRequests() {
        myRestockRequestsModel.setRowCount(0);
        
        List<RestockRequest> requests = restockRequestDAO.getRestockRequestsByPharmacist(currentUser.getUserId());
        
        for (RestockRequest req : requests) {
            Object[] row = {
                req.getRequestId(),
                req.getMedicationName(),
                req.getRequestedQuantity(),
                req.getCurrentStock(),
                req.getPriority(),
                req.getStatus(),
                req.getRequestedDate()
            };
            myRestockRequestsModel.addRow(row);
        }
    }
    
    /**
     * Fill selected prescription
     * WORK REQUEST #2: Completing the inter-enterprise flow
     */
    private void fillSelectedPrescription() {
        int selectedRow = pendingPrescriptionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a prescription to fill!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int rxId = (Integer) pendingPrescriptionsModel.getValueAt(selectedRow, 0);
        String patientName = (String) pendingPrescriptionsModel.getValueAt(selectedRow, 1);
        String medication = (String) pendingPrescriptionsModel.getValueAt(selectedRow, 3);
        
        // Confirm
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Fill prescription for " + patientName + "?\n" +
            "Medication: " + medication + "\n\n" +
            "This will mark the prescription as FILLED and reduce inventory.",
            "Confirm Fill Prescription", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Optional: Add pharmacist notes
            String notes = JOptionPane.showInputDialog(this, 
                "Enter any notes (optional):", 
                "Pharmacist Notes", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (notes == null) notes = "Filled without issues";
            
            boolean success = prescriptionDAO.fillPrescription(rxId, currentUser.getUserId(), notes);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Prescription filled successfully!\n\n" +
                    "Work Request #2 COMPLETED:\n" +
                    "Doctor (Clinic) → Pharmacist (Pharmacy)\n\n" +
                    "The prescription status is now FILLED.\n" +
                    "Inventory has been updated.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPendingPrescriptions(); // Refresh
                loadAllPrescriptions();
                loadInventory();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to fill prescription!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View prescription details
     */
    private void viewPrescriptionDetails() {
        int selectedRow = pendingPrescriptionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a prescription first!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int rxId = (Integer) pendingPrescriptionsModel.getValueAt(selectedRow, 0);
        Prescription rx = prescriptionDAO.getPrescriptionById(rxId);
        
        if (rx != null) {
            String details = String.format(
                "Prescription ID: %d\n" +
                "Patient: %s\n" +
                "Doctor: %s\n" +
                "Medication: %s\n" +
                "Dosage: %s\n" +
                "Quantity: %d\n" +
                "Refills: %d\n" +
                "Status: %s\n" +
                "Issued Date: %s",
                rx.getPrescriptionId(),
                rx.getPatientName(),
                rx.getDoctorName(),
                rx.getMedicationName(),
                rx.getDosageInstructions(),
                rx.getQuantity(),
                rx.getRefills(),
                rx.getStatus(),
                rx.getIssuedDate()
            );
            
            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Prescription Details", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Check stock level for selected medication
     */
    private void checkSelectedStock() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medication first!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int medId = (Integer) inventoryModel.getValueAt(selectedRow, 0);
        String medName = (String) inventoryModel.getValueAt(selectedRow, 1);
        int stock = (Integer) inventoryModel.getValueAt(selectedRow, 5);
        
        String message = String.format(
            "Medication: %s\n" +
            "Current Stock: %d units\n\n" +
            "%s",
            medName,
            stock,
            stock < 50 ? "⚠ LOW STOCK - Consider requesting restock!" : "✓ Stock level is adequate"
        );
        
        JOptionPane.showMessageDialog(this, message, "Stock Level", 
            stock < 50 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Create restock request
     * WORK REQUEST #4: Pharmacist → Manager
     */
    private void createRestockRequest(JComboBox<String> medCombo, String currentStockText, 
                                     int requestedQty, String priority, String reason) {
        try {
            // Validate
            if (currentStockText.isEmpty() || reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int currentStock = Integer.parseInt(currentStockText);
            
            // Get medication ID
            String medSelection = (String) medCombo.getSelectedItem();
            if (medSelection == null) {
                JOptionPane.showMessageDialog(this, "Please select a medication!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int medicationId = Integer.parseInt(medSelection.split(" - ")[0]);
            
            // Create request
            RestockRequest request = new RestockRequest();
            request.setMedicationId(medicationId);
            request.setRequestedQuantity(requestedQty);
            request.setCurrentStock(currentStock);
            request.setPriority(priority);
            request.setReason(reason);
            request.setRequestedBy(currentUser.getUserId());
            request.setStatus("PENDING");
            
            boolean success = restockRequestDAO.createRestockRequest(request);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Restock request created and sent to manager!\n\n" +
                    "Request ID: " + request.getRequestId() + "\n" +
                    "Status: PENDING (waiting for manager approval)\n\n" +
                    "This demonstrates INTRA-ENTERPRISE communication:\n" +
                    "Pharmacist → Pharmacy Manager (Work Request #4)", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadMyRestockRequests(); // Refresh
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create restock request!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format!", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Load all data when dashboard opens
     */
    private void loadData() {
        loadPendingPrescriptions();
        loadAllPrescriptions();
        loadInventory();
        loadMyRestockRequests();
    }
}