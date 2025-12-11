package ui;

import model.User;
import ui.doctor.DoctorDashboard;
import ui.pharmacist.PharmacistDashboard;
import ui.admin.ClinicAdminDashboard;
import ui.admin.PharmacyManagerDashboard;

import javax.swing.*;
import java.awt.*;

public class DashboardLauncher extends JFrame {
    
    public DashboardLauncher() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Healthcare System - Dashboard Launcher (Demo Mode)");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Title
        JLabel titleLabel = new JLabel("Healthcare Coordination System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        
        JLabel subtitleLabel = new JLabel("Demo Dashboard Launcher - Click to View Any Dashboard", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        titlePanel.setBackground(new Color(245, 245, 245));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Dashboard buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(6, 1, 15, 15));
        buttonsPanel.setBackground(new Color(245, 245, 245));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Login Screen Button
        JButton loginButton = createCleanButton(
            "🔐 Login Screen",
            "Main entry point - Role-based authentication",
            new Color(70, 130, 180)
        );
        loginButton.addActionListener(e -> new LoginFrame().setVisible(true));
        
        // Doctor Dashboard Button
        JButton doctorButton = createCleanButton(
            "👨‍⚕️ Doctor Dashboard",
            "View appointments, patients, create prescriptions (Work Request #2)",
            new Color(41, 128, 185)
        );
        doctorButton.addActionListener(e -> openDoctorDashboard());
        
        // Pharmacist Dashboard Button
        JButton pharmacistButton = createCleanButton(
            "💊 Pharmacist Dashboard",
            "Fill prescriptions, manage inventory (Work Requests #2 & #4)",
            new Color(39, 174, 96)
        );
        pharmacistButton.addActionListener(e -> openPharmacistDashboard());
        
        // Clinic Admin Dashboard Button
        JButton clinicAdminButton = createCleanButton(
            "🏥 Clinic Admin Dashboard",
            "Manage staff, view reports, oversee operations",
            new Color(52, 152, 219)
        );
        clinicAdminButton.addActionListener(e -> openClinicAdminDashboard());
        
        // Pharmacy Manager Dashboard Button
        JButton pharmacyManagerButton = createCleanButton(
            "📊 Pharmacy Manager Dashboard",
            "Approve restock requests, manage inventory (Work Request #4)",
            new Color(192, 57, 43)
        );
        pharmacyManagerButton.addActionListener(e -> openPharmacyManagerDashboard());
        
        // Info Button
        JButton infoButton = createCleanButton(
            "ℹ️ System Information",
            "View project structure and requirements fulfillment",
            new Color(127, 140, 141)
        );
        infoButton.addActionListener(e -> showSystemInfo());
        
        buttonsPanel.add(loginButton);
        buttonsPanel.add(doctorButton);
        buttonsPanel.add(pharmacistButton);
        buttonsPanel.add(clinicAdminButton);
        buttonsPanel.add(pharmacyManagerButton);
        buttonsPanel.add(infoButton);
        
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new GridLayout(3, 1));
        footerPanel.setBackground(new Color(245, 245, 245));
        
        JLabel footer1 = new JLabel("INFO 5100 Final Project", SwingConstants.CENTER);
        footer1.setFont(new Font("Arial", Font.BOLD, 12));
        footer1.setForeground(Color.GRAY);
        
        JLabel footer2 = new JLabel("Healthcare Coordination System", SwingConstants.CENTER);
        footer2.setFont(new Font("Arial", Font.PLAIN, 11));
        footer2.setForeground(Color.GRAY);
        
        JLabel footer3 = new JLabel("2 Enterprises • 4 Roles • 4 Work Requests • Full CRUD", SwingConstants.CENTER);
        footer3.setFont(new Font("Arial", Font.PLAIN, 10));
        footer3.setForeground(Color.GRAY);
        
        footerPanel.add(footer1);
        footerPanel.add(footer2);
        footerPanel.add(footer3);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JButton createCleanButton(String title, String description, Color color) {
        JPanel buttonContent = new JPanel();
        buttonContent.setLayout(new BoxLayout(buttonContent, BoxLayout.Y_AXIS));
        buttonContent.setBackground(color);
        buttonContent.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(255, 255, 255, 200));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        buttonContent.add(titleLabel);
        buttonContent.add(Box.createVerticalStrut(5));
        buttonContent.add(descLabel);
        
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.add(buttonContent);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
                buttonContent.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                buttonContent.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void openDoctorDashboard() {
        User doctor = new User();
        doctor.setUserId(1);
        doctor.setFullName("Dr. Sarah Smith");
        doctor.setRole("DOCTOR");
        doctor.setEnterpriseType("CLINIC");
        new DoctorDashboard(doctor).setVisible(true);
    }
    
    private void openPharmacistDashboard() {
        User pharmacist = new User();
        pharmacist.setUserId(4);
        pharmacist.setFullName("David Wilson");
        pharmacist.setRole("PHARMACIST");
        pharmacist.setEnterpriseType("PHARMACY");
        new PharmacistDashboard(pharmacist).setVisible(true);
    }
    
    private void openClinicAdminDashboard() {
        User admin = new User();
        admin.setUserId(3);
        admin.setFullName("Jennifer Admin");
        admin.setRole("CLINIC_ADMIN");
        admin.setEnterpriseType("CLINIC");
        new ClinicAdminDashboard(admin).setVisible(true);
    }
    
    private void openPharmacyManagerDashboard() {
        User manager = new User();
        manager.setUserId(6);
        manager.setFullName("Robert Manager");
        manager.setRole("PHARMACY_MANAGER");
        manager.setEnterpriseType("PHARMACY");
        new PharmacyManagerDashboard(manager).setVisible(true);
    }
    
    private void showSystemInfo() {
        String info = 
            "═══════════════════════════════════════════════\n" +
            "  HEALTHCARE COORDINATION SYSTEM\n" +
            "═══════════════════════════════════════════════\n\n" +
            
            "✅ TECHNOLOGY STACK:\n" +
            "   • Java Swing for UI\n" +
            "   • MySQL Database with JDBC\n" +
            "   • Docker for MySQL hosting\n\n" +
            
            "✅ APPLICATION ARCHITECTURE:\n" +
            "   • 19 Java classes total\n" +
            "   • 6 Model classes\n" +
            "   • 6 DAO classes (Full CRUD)\n" +
            "   • 6 UI classes\n" +
            "   • 1 Database connection utility\n\n" +
            
            "✅ OOP CONCEPTS:\n" +
            "   • Encapsulation\n" +
            "   • Inheritance (all dashboards extend JFrame)\n" +
            "   • Polymorphism\n" +
            "   • Abstraction (DAO pattern)\n\n" +
            
            "✅ ECOSYSTEM:\n" +
            "   • 2 Enterprises: Clinic and Pharmacy\n" +
            "   • 4 Roles: Doctor, Pharmacist, Admin, Manager\n\n" +
            
            "✅ WORK REQUESTS:\n" +
            "   1. Appointment Booking (Intra-Clinic)\n" +
            "   2. Prescription Flow (Inter-Enterprise) ⭐\n" +
            "   3. Medication Check\n" +
            "   4. Inventory Restock (Intra-Pharmacy) ⭐\n\n" +
            
            "✅ DATABASE:\n" +
            "   • Full CRUD on all 7 tables\n" +
            "   • Docker MySQL 8.0\n" +
            "   • JDBC connectivity\n\n" +
            
            "═══════════════════════════════════════════════\n" +
            "Successfully Implemented!\n" +
            "═══════════════════════════════════════════════";
        
        JTextArea textArea = new JTextArea(info);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "System Information", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new DashboardLauncher().setVisible(true));
    }
}