package ui.admin;

import model.User;
import model.Appointment;
import model.Patient;
import model.Prescription;
import database.UserDAO;
import database.AppointmentDAO;
import database.PatientDAO;
import database.PrescriptionDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ClinicAdminDashboard - Main screen for clinic administrators
 * Manages clinic staff, views reports, oversees operations
 */
public class ClinicAdminDashboard extends JFrame {
    
    private User currentUser;
    
    // DAOs
    private UserDAO userDAO;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private PrescriptionDAO prescriptionDAO;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable usersTable;
    private JTable appointmentsTable;
    private JTable patientsTable;
    private JTable prescriptionsTable;
    private DefaultTableModel usersModel;
    private DefaultTableModel appointmentsModel;
    private DefaultTableModel patientsModel;
    private DefaultTableModel prescriptionsModel;
    
    public ClinicAdminDashboard(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.prescriptionDAO = new PrescriptionDAO();
        
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        setTitle("Clinic Admin Dashboard - " + currentUser.getFullName());
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
        
        // Tab 1: Dashboard Overview
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        
        // Tab 2: Manage Users
        tabbedPane.addTab("Manage Users", createUsersPanel());
        
        // Tab 3: All Appointments
        tabbedPane.addTab("Appointments", createAppointmentsPanel());
        
        // Tab 4: All Patients
        tabbedPane.addTab("Patients", createPatientsPanel());
        
        // Tab 5: Prescriptions Report
        tabbedPane.addTab("Prescriptions", createPrescriptionsPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180)); // Steel blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Clinic Admin Dashboard - " + currentUser.getFullName());
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
     * TAB 1: Dashboard Overview with Statistics
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Clinic Overview & Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Get counts
        int totalDoctors = userDAO.getUsersByRole("DOCTOR").size();
        int totalPatients = patientDAO.getAllPatients().size();
        int totalAppointments = appointmentDAO.getAllAppointments().size();
        int totalPrescriptions = prescriptionDAO.getAllPrescriptions().size();
        
        // Create stat cards
        statsPanel.add(createStatCard("Total Doctors", String.valueOf(totalDoctors), new Color(70, 130, 180)));
        statsPanel.add(createStatCard("Total Patients", String.valueOf(totalPatients), new Color(34, 139, 34)));
        statsPanel.add(createStatCard("Total Appointments", String.valueOf(totalAppointments), new Color(255, 140, 0)));
        statsPanel.add(createStatCard("Prescriptions Issued", String.valueOf(totalPrescriptions), new Color(147, 112, 219)));
        statsPanel.add(createStatCard("Clinic Staff", String.valueOf(userDAO.getUsersByRole("CLINIC_ADMIN").size() + totalDoctors), new Color(220, 20, 60)));
        statsPanel.add(createStatCard("System Status", "✓ Active", new Color(60, 179, 113)));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        JButton refreshButton = new JButton("🔄 Refresh Dashboard");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(e -> {
            loadData();
            JOptionPane.showMessageDialog(this, "Dashboard refreshed successfully!", 
                "Refreshed", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton reportButton = new JButton("📊 Generate Report");
        reportButton.setFont(new Font("Arial", Font.BOLD, 14));
        reportButton.addActionListener(e -> generateReport());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(reportButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Helper method to create stat cards
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * TAB 2: Manage Users Panel
     */
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Clinic Staff & Users Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"User ID", "Full Name", "Username", "Role", "Enterprise", "Email", "Phone"};
        usersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(usersModel);
        usersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        usersTable.setRowHeight(25);
        usersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadUsers());
        
        JButton addUserButton = new JButton("+ Add New User");
        addUserButton.setBackground(new Color(34, 139, 34));
        addUserButton.setForeground(Color.BLACK);
        addUserButton.addActionListener(e -> addNewUser());
        
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> viewUserDetails());
        
        JButton filterDoctorsButton = new JButton("Show Doctors Only");
        filterDoctorsButton.addActionListener(e -> filterByRole("DOCTOR"));
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(addUserButton);
        buttonsPanel.add(viewDetailsButton);
        buttonsPanel.add(filterDoctorsButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 3: All Appointments Panel
     */
    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("All Clinic Appointments");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Apt ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status", "Diagnosis"};
        appointmentsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentsTable = new JTable(appointmentsModel);
        appointmentsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        appointmentsTable.setRowHeight(25);
        appointmentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAppointments());
        
        JButton filterTodayButton = new JButton("Today's Appointments");
        filterTodayButton.addActionListener(e -> filterTodayAppointments());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(filterTodayButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 4: All Patients Panel
     */
    private JPanel createPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("All Registered Patients");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Patient ID", "Name", "DOB", "Gender", "Phone", "Email", "Blood Group", "Allergies"};
        patientsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientsTable = new JTable(patientsModel);
        patientsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        patientsTable.setRowHeight(25);
        patientsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadPatients());
        
        JButton searchButton = new JButton("🔍 Search Patient");
        searchButton.addActionListener(e -> searchPatient());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(searchButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 5: Prescriptions Report Panel
     */
    private JPanel createPrescriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("All Prescriptions Issued by Clinic");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Rx ID", "Patient", "Doctor", "Medication", "Quantity", "Status", "Issued Date", "Filled By"};
        prescriptionsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        prescriptionsTable = new JTable(prescriptionsModel);
        prescriptionsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        prescriptionsTable.setRowHeight(25);
        prescriptionsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(prescriptionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadPrescriptions());
        
        JButton statsButton = new JButton("📊 Prescription Statistics");
        statsButton.addActionListener(e -> showPrescriptionStats());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(statsButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Load all users
     */
    private void loadUsers() {
        usersModel.setRowCount(0);
        
        List<User> users = userDAO.getAllUsers();
        
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getFullName(),
                user.getUsername(),
                user.getRole(),
                user.getEnterpriseType(),
                user.getEmail(),
                user.getPhone()
            };
            usersModel.addRow(row);
        }
    }
    
    /**
     * Load all appointments
     */
    private void loadAppointments() {
        appointmentsModel.setRowCount(0);
        
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        
        for (Appointment apt : appointments) {
            Object[] row = {
                apt.getAppointmentId(),
                apt.getPatientName(),
                apt.getDoctorName(),
                apt.getAppointmentDate(),
                apt.getAppointmentTime(),
                apt.getReason(),
                apt.getStatus(),
                apt.getDiagnosis() != null ? apt.getDiagnosis().substring(0, Math.min(30, apt.getDiagnosis().length())) + "..." : "N/A"
            };
            appointmentsModel.addRow(row);
        }
    }
    
    /**
     * Load all patients
     */
    private void loadPatients() {
        patientsModel.setRowCount(0);
        
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
            patientsModel.addRow(row);
        }
    }
    
    /**
     * Load all prescriptions
     */
    private void loadPrescriptions() {
        prescriptionsModel.setRowCount(0);
        
        List<Prescription> prescriptions = prescriptionDAO.getAllPrescriptions();
        
        for (Prescription rx : prescriptions) {
            Object[] row = {
                rx.getPrescriptionId(),
                rx.getPatientName(),
                rx.getDoctorName(),
                rx.getMedicationName(),
                rx.getQuantity(),
                rx.getStatus(),
                rx.getIssuedDate(),
                rx.getPharmacistName() != null ? rx.getPharmacistName() : "Not filled"
            };
            prescriptionsModel.addRow(row);
        }
    }
    
    /**
     * Add new user
     */
    private void addNewUser() {
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField fullNameField = new JTextField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"DOCTOR", "CLINIC_ADMIN"});
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            User newUser = new User();
            newUser.setUsername(usernameField.getText().trim());
            newUser.setPassword(new String(passwordField.getPassword()));
            newUser.setFullName(fullNameField.getText().trim());
            newUser.setRole((String) roleCombo.getSelectedItem());
            newUser.setEnterpriseType("CLINIC");
            newUser.setEmail(emailField.getText().trim());
            newUser.setPhone(phoneField.getText().trim());
            
            boolean success = userDAO.createUser(newUser);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "User created successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View user details
     */
    private void viewUserDetails() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (Integer) usersModel.getValueAt(selectedRow, 0);
        User user = userDAO.getUserById(userId);
        
        if (user != null) {
            String details = String.format(
                "User ID: %d\n" +
                "Full Name: %s\n" +
                "Username: %s\n" +
                "Role: %s\n" +
                "Enterprise: %s\n" +
                "Email: %s\n" +
                "Phone: %s\n" +
                "Created: %s",
                user.getUserId(),
                user.getFullName(),
                user.getUsername(),
                user.getRole(),
                user.getEnterpriseType(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedDate()
            );
            
            JOptionPane.showMessageDialog(this, details, "User Details", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Filter users by role
     */
    private void filterByRole(String role) {
        usersModel.setRowCount(0);
        
        List<User> users = userDAO.getUsersByRole(role);
        
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getFullName(),
                user.getUsername(),
                user.getRole(),
                user.getEnterpriseType(),
                user.getEmail(),
                user.getPhone()
            };
            usersModel.addRow(row);
        }
        
        JOptionPane.showMessageDialog(this, "Showing " + users.size() + " " + role + "(s)", 
            "Filtered", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Filter today's appointments
     */
    private void filterTodayAppointments() {
        JOptionPane.showMessageDialog(this, 
            "This feature would filter appointments for today.\n" +
            "Currently showing all appointments.", 
            "Filter", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Search patient
     */
    private void searchPatient() {
        String searchTerm = JOptionPane.showInputDialog(this, 
            "Enter patient name to search:", 
            "Search Patient", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            patientsModel.setRowCount(0);
            
            List<Patient> patients = patientDAO.searchPatientsByName(searchTerm);
            
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
                patientsModel.addRow(row);
            }
            
            JOptionPane.showMessageDialog(this, "Found " + patients.size() + " patient(s)", 
                "Search Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Show prescription statistics
     */
    private void showPrescriptionStats() {
        List<Prescription> prescriptions = prescriptionDAO.getAllPrescriptions();
        
        int pending = 0, filled = 0, completed = 0;
        
        for (Prescription rx : prescriptions) {
            switch (rx.getStatus()) {
                case "PENDING": pending++; break;
                case "FILLED": filled++; break;
                case "COMPLETED": completed++; break;
            }
        }
        
        String stats = String.format(
            "Prescription Statistics:\n\n" +
            "Total Prescriptions: %d\n" +
            "Pending (at pharmacy): %d\n" +
            "Filled: %d\n" +
            "Completed: %d\n\n" +
            "Fulfillment Rate: %.1f%%",
            prescriptions.size(),
            pending,
            filled,
            completed,
            prescriptions.size() > 0 ? ((filled + completed) * 100.0 / prescriptions.size()) : 0
        );
        
        JOptionPane.showMessageDialog(this, stats, "Prescription Statistics", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Generate comprehensive report
     */
    private void generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════\n");
        report.append("     CLINIC COMPREHENSIVE REPORT\n");
        report.append("═══════════════════════════════════════\n\n");
        
        report.append("STAFF:\n");
        report.append("  Doctors: ").append(userDAO.getUsersByRole("DOCTOR").size()).append("\n");
        report.append("  Admins: ").append(userDAO.getUsersByRole("CLINIC_ADMIN").size()).append("\n\n");
        
        report.append("PATIENTS:\n");
        report.append("  Total Registered: ").append(patientDAO.getAllPatients().size()).append("\n\n");
        
        report.append("APPOINTMENTS:\n");
        report.append("  Total: ").append(appointmentDAO.getAllAppointments().size()).append("\n\n");
        
        report.append("PRESCRIPTIONS:\n");
        report.append("  Total Issued: ").append(prescriptionDAO.getAllPrescriptions().size()).append("\n");
        report.append("  Sent to Pharmacy: ").append(prescriptionDAO.getPendingPrescriptions().size()).append("\n\n");
        
        report.append("═══════════════════════════════════════\n");
        report.append("Report generated successfully!\n");
        report.append("Date: ").append(new java.util.Date()).append("\n");
        report.append("═══════════════════════════════════════\n");
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Clinic Report", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Load all data
     */
    private void loadData() {
        loadUsers();
        loadAppointments();
        loadPatients();
        loadPrescriptions();
    }
}