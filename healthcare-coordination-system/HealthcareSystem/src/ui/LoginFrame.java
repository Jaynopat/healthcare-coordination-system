package ui;

import database.UserDAO;
import model.User;
import ui.doctor.DoctorDashboard;
import ui.pharmacist.PharmacistDashboard;
import ui.admin.ClinicAdminDashboard;
import ui.admin.PharmacyManagerDashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * LoginFrame - First screen users see
 * Authenticates users and directs them to their role-specific dashboard
 */
public class LoginFrame extends JFrame {
    
    // UI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    // Constructor - sets up the login screen
    public LoginFrame() {
        initializeUI();
    }
    
    /**
     * Initialize and setup the UI components
     */
    private void initializeUI() {
        // Set window properties
        setTitle("Healthcare Coordination System - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(new Color(240, 248, 255)); // Light blue background
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel blue
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JLabel titleLabel = new JLabel("Healthcare Coordination System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Connecting Clinics and Pharmacies");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        
        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(new Color(70, 130, 180));
        titleWrapper.add(titleLabel, BorderLayout.CENTER);
        titleWrapper.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Login Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel loginLabel = new JLabel("Please Login", SwingConstants.CENTER);
        loginLabel.setFont(new Font("Arial", Font.BOLD, 18));
        formPanel.add(loginLabel, gbc);
        
        // Username
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(passwordField, gbc);
        
        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        formPanel.add(loginButton, gbc);
        
        // Status Label (for error messages)
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel, gbc);
        
        // Info Panel (show sample credentials)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(255, 255, 224)); // Light yellow
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 150)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel infoTitle = new JLabel("Sample Login Credentials:");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(5));
        
        String[] credentials = {
            "Doctor: dr.smith / pass123",
            "Pharmacist: pharm.wilson / pass123",
            "Clinic Admin: admin.clinic / pass123",
            "Pharmacy Manager: manager.pharm / pass123"
        };
        
        for (String cred : credentials) {
            JLabel credLabel = new JLabel(cred);
            credLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            infoPanel.add(credLabel);
        }
        
        // Add panels to main panel
        mainPanel.add(titleWrapper, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Allow Enter key to login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    /**
     * Handle login button click
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }
        
        // Show loading message
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setText("Logging in...");
        loginButton.setEnabled(false);
        
        // Authenticate user
        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username, password);
        
        if (user != null) {
            // Login successful!
            statusLabel.setForeground(new Color(0, 128, 0)); // Green
            statusLabel.setText("Login successful! Welcome " + user.getFullName());
            
            // Open appropriate dashboard based on role
            openDashboard(user);
            
            // Close login window
            dispose();
            
        } else {
            // Login failed
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Invalid username or password. Please try again.");
            loginButton.setEnabled(true);
            passwordField.setText(""); // Clear password field
        }
    }
    
    /**
     * Open the appropriate dashboard based on user role
     */
    private void openDashboard(User user) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                switch (user.getRole()) {
                    case "DOCTOR":
                        new DoctorDashboard(user).setVisible(true);
                        break;
                    case "PHARMACIST":
                        new PharmacistDashboard(user).setVisible(true);
                        break;
                    case "CLINIC_ADMIN":
                        new ClinicAdminDashboard(user).setVisible(true);
                        break;
                    case "PHARMACY_MANAGER":
                        new PharmacyManagerDashboard(user).setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, 
                            "Unknown role: " + user.getRole(),
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    /**
     * Main method - Entry point of the application
     */
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show login frame
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
