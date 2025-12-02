package ui.pharmacist;

import model.User;
import javax.swing.*;
import java.awt.*;

public class PharmacistDashboard extends JFrame {
    
    private User currentUser;
    
    public PharmacistDashboard(User user) {
        this.currentUser = user;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Pharmacist Dashboard - " + currentUser.getFullName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Welcome, " + currentUser.getFullName());
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        JLabel placeholderLabel = new JLabel("Pharmacist Dashboard - Coming Soon!", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        mainPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
}