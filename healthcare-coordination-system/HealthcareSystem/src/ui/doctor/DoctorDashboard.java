package ui.doctor;

import model.User;
import model.Appointment;
import model.Patient;
import model.Medication;
import model.Prescription;
import database.AppointmentDAO;
import database.PatientDAO;
import database.MedicationDAO;
import database.PrescriptionDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * DoctorDashboard - Main screen for doctors
 * Features: View appointments, patients, create prescriptions
 * Work Request #2: Creates prescriptions (Clinic → Pharmacy)
 */
public class DoctorDashboard extends JFrame {
    
    private User currentUser;
    
    // DAOs
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private MedicationDAO medicationDAO;
    private PrescriptionDAO prescriptionDAO;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable appointmentsTable;
    private JTable patientsTable;
    private DefaultTableModel appointmentsTableModel;
    private DefaultTableModel patientsTableModel;
    
    public DoctorDashboard(User user) {
        this.currentUser = user;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.medicationDAO = new MedicationDAO();
        this.prescriptionDAO = new PrescriptionDAO();
        
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        setTitle("Doctor Dashboard - " + currentUser.getFullName());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed Pane for different sections
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Tab 1: My Appointments
        tabbedPane.addTab("My Appointments", createAppointmentsPanel());
        
        // Tab 2: Patients
        tabbedPane.addTab("Patients", createPatientsPanel());
        
        // Tab 3: Create Prescription
        tabbedPane.addTab("Create Prescription", createPrescriptionPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185)); // Vibrant blue - changed from steel blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Welcome, Dr. " + currentUser.getFullName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(236, 240, 241));
        logoutButton.setForeground(new Color(44, 62, 80));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            dispose();
            // Reopen login screen
            SwingUtilities.invokeLater(() -> new ui.LoginFrame().setVisible(true));
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * TAB 1: My Appointments Panel
     */
    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("My Appointments");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Patient", "Date", "Time", "Reason", "Status", "Diagnosis"};
        appointmentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        appointmentsTable = new JTable(appointmentsTableModel);
        appointmentsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        appointmentsTable.setRowHeight(25);
        appointmentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.setBackground(new Color(149, 165, 166));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadAppointments());
        
        JButton completeButton = new JButton("Complete Appointment");
        completeButton.setFont(new Font("Arial", Font.PLAIN, 13));
        completeButton.setBackground(new Color(46, 204, 113)); // Bright green
        completeButton.setForeground(Color.BLACK);
        completeButton.setFocusPainted(false);
        completeButton.addActionListener(e -> completeSelectedAppointment());
        
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setFont(new Font("Arial", Font.PLAIN, 12));
        viewDetailsButton.setBackground(new Color(52, 152, 219));
        viewDetailsButton.setForeground(Color.BLACK);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.addActionListener(e -> viewAppointmentDetails());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(completeButton);
        buttonsPanel.add(viewDetailsButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 2: Patients Panel
     */
    private JPanel createPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("All Patients");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Name", "DOB", "Gender", "Phone", "Email", "Blood Group", "Allergies"};
        patientsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientsTable = new JTable(patientsTableModel);
        patientsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        patientsTable.setRowHeight(25);
        patientsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 13));
        refreshButton.setBackground(new Color(41, 128, 185)); // Vibrant blue
        refreshButton.setForeground(Color.BLACK);
        buttonsPanel.add(refreshButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 3: Create Prescription Panel
     * WORK REQUEST #2: Inter-enterprise communication (Clinic → Pharmacy)
     */
    private JPanel createPrescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Create Prescription (Send to Pharmacy)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Prescription Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Appointment ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Appointment ID:"), gbc);
        gbc.gridx = 1;
        JTextField appointmentIdField = new JTextField(20);
        formPanel.add(appointmentIdField, gbc);
        
        // Patient ID
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1;
        JTextField patientIdField = new JTextField(20);
        formPanel.add(patientIdField, gbc);
        
        // Medication
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Medication:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> medicationCombo = new JComboBox<>();
        loadMedicationsIntoCombo(medicationCombo);
        formPanel.add(medicationCombo, gbc);
        
        // Dosage Instructions
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Dosage Instructions:"), gbc);
        gbc.gridx = 1;
        JTextField dosageField = new JTextField(20);
        dosageField.setText("Take 1 tablet twice daily");
        formPanel.add(dosageField, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        formPanel.add(quantitySpinner, gbc);
        
        // Refills
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Refills:"), gbc);
        gbc.gridx = 1;
        JSpinner refillsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 12, 1));
        formPanel.add(refillsSpinner, gbc);
        
        // Info Label
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><i>This prescription will be sent to the pharmacy for fulfillment.<br>" +
                "Status will be tracked: PENDING → FILLED → COMPLETED</i></html>");
        infoLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(infoLabel, gbc);
        
        // Submit Button - IMPROVED COLORS
        gbc.gridy = 7;
        JButton createButton = new JButton("Create & Send Prescription to Pharmacy");
        createButton.setFont(new Font("Arial", Font.PLAIN, 14));
        createButton.setBackground(new Color(46, 204, 113)); // Bright green - CHANGED
        createButton.setForeground(Color.BLACK);
        createButton.setFocusPainted(false);
        createButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        createButton.addActionListener(e -> {
            createPrescription(
                appointmentIdField.getText(),
                patientIdField.getText(),
                medicationCombo,
                dosageField.getText(),
                (Integer) quantitySpinner.getValue(),
                (Integer) refillsSpinner.getValue()
            );
        });
        formPanel.add(createButton, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Load appointments from database
     */
    private void loadAppointments() {
        appointmentsTableModel.setRowCount(0); // Clear table
        
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDoctor(currentUser.getUserId());
        
        for (Appointment apt : appointments) {
            Object[] row = {
                apt.getAppointmentId(),
                apt.getPatientName(),
                apt.getAppointmentDate(),
                apt.getAppointmentTime(),
                apt.getReason(),
                apt.getStatus(),
                apt.getDiagnosis() != null ? apt.getDiagnosis().substring(0, Math.min(30, apt.getDiagnosis().length())) + "..." : ""
            };
            appointmentsTableModel.addRow(row);
        }
    }
    
    /**
     * Load patients from database
     */
    private void loadPatients() {
        patientsTableModel.setRowCount(0); // Clear table
        
        List<Patient> patients = patientDAO.getAllPatients();
        
        for (Patient patient : patients) {
            Object[] row = {
                patient.getPatientId(),
                patient.getFullName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getBloodGroup(),
                patient.getAllergies()
            };
            patientsTableModel.addRow(row);
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
     * Complete selected appointment with diagnosis
     */
    private void completeSelectedAppointment() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (Integer) appointmentsTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) appointmentsTableModel.getValueAt(selectedRow, 5);
        
        if ("COMPLETED".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This appointment is already completed!", 
                "Already Completed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Dialog to enter diagnosis
        JTextArea diagnosisArea = new JTextArea(5, 30);
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(diagnosisArea);
        
        JTextArea notesArea = new JTextArea(3, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        
        JPanel dialogPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        dialogPanel.add(new JLabel("Diagnosis:"));
        dialogPanel.add(scrollPane);
        dialogPanel.add(new JLabel("Notes:"));
        dialogPanel.add(notesScroll);
        
        int result = JOptionPane.showConfirmDialog(this, dialogPanel, 
            "Complete Appointment - Add Diagnosis", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String diagnosis = diagnosisArea.getText().trim();
            String notes = notesArea.getText().trim();
            
            if (diagnosis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Diagnosis is required!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = appointmentDAO.completeAppointment(appointmentId, diagnosis, notes);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Appointment completed successfully!\nYou can now create a prescription for this patient.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAppointments(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to complete appointment!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View appointment details
     */
    private void viewAppointmentDetails() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (Integer) appointmentsTableModel.getValueAt(selectedRow, 0);
        Appointment apt = appointmentDAO.getAppointmentById(appointmentId);
        
        if (apt != null) {
            String details = String.format(
                "Appointment ID: %d\n" +
                "Patient: %s\n" +
                "Date: %s\n" +
                "Time: %s\n" +
                "Reason: %s\n" +
                "Status: %s\n" +
                "Diagnosis: %s\n" +
                "Notes: %s",
                apt.getAppointmentId(),
                apt.getPatientName(),
                apt.getAppointmentDate(),
                apt.getAppointmentTime(),
                apt.getReason(),
                apt.getStatus(),
                apt.getDiagnosis() != null ? apt.getDiagnosis() : "Not yet diagnosed",
                apt.getNotes() != null ? apt.getNotes() : "No notes"
            );
            
            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Appointment Details", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Create prescription and send to pharmacy
     * WORK REQUEST #2: Inter-enterprise communication
     */
    private void createPrescription(String aptIdText, String patIdText, JComboBox<String> medCombo,
                                   String dosage, int quantity, int refills) {
        try {
            // Validate inputs
            if (aptIdText.isEmpty() || patIdText.isEmpty() || dosage.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int appointmentId = Integer.parseInt(aptIdText);
            int patientId = Integer.parseInt(patIdText);
            
            // Get medication ID from combo box selection
            String medSelection = (String) medCombo.getSelectedItem();
            if (medSelection == null) {
                JOptionPane.showMessageDialog(this, "Please select a medication!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int medicationId = Integer.parseInt(medSelection.split(" - ")[0]);
            
            // Create prescription object
            Prescription prescription = new Prescription();
            prescription.setAppointmentId(appointmentId);
            prescription.setPatientId(patientId);
            prescription.setDoctorId(currentUser.getUserId());
            prescription.setMedicationId(medicationId);
            prescription.setDosageInstructions(dosage);
            prescription.setQuantity(quantity);
            prescription.setRefills(refills);
            prescription.setStatus("PENDING"); // Waiting for pharmacy to fill
            
            // Save to database - this sends it to pharmacy!
            boolean success = prescriptionDAO.createPrescription(prescription);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Prescription created and sent to pharmacy!\n\n" +
                    "Prescription ID: " + prescription.getPrescriptionId() + "\n" +
                    "Status: PENDING (waiting for pharmacy to fill)\n\n" +
                    "The pharmacy will receive this prescription and can fill it.\n" +
                    "This demonstrates INTER-ENTERPRISE communication:\n" +
                    "Clinic (Doctor) → Pharmacy (Pharmacist)", 
                    "Success - Work Request #2", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create prescription!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format! Please enter valid numbers.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Load all data when dashboard opens
     */
    private void loadData() {
        loadAppointments();
        loadPatients();
    }
}