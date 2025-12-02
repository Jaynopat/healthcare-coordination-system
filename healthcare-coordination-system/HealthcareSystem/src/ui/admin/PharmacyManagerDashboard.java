package ui.admin;

import model.User;
import model.RestockRequest;
import model.Medication;
import database.RestockRequestDAO;
import database.MedicationDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PharmacyManagerDashboard - Main screen for pharmacy managers
 * Work Request #4: Approves restock requests from pharmacists (Intra-enterprise)
 */
public class PharmacyManagerDashboard extends JFrame {
    
    private User currentUser;
    
    // DAOs
    private RestockRequestDAO restockRequestDAO;
    private MedicationDAO medicationDAO;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable pendingRequestsTable;
    private JTable allRequestsTable;
    private JTable inventoryTable;
    private DefaultTableModel pendingRequestsModel;
    private DefaultTableModel allRequestsModel;
    private DefaultTableModel inventoryModel;
    
    public PharmacyManagerDashboard(User user) {
        this.currentUser = user;
        this.restockRequestDAO = new RestockRequestDAO();
        this.medicationDAO = new MedicationDAO();
        
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        setTitle("Pharmacy Manager Dashboard - " + currentUser.getFullName());
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
        
        // Tab 1: Pending Restock Requests (Work Request #4 - approval side)
        tabbedPane.addTab("⚠ Pending Requests", createPendingRequestsPanel());
        
        // Tab 2: All Restock Requests History
        tabbedPane.addTab("All Requests", createAllRequestsPanel());
        
        // Tab 3: Inventory Overview
        tabbedPane.addTab("Inventory Overview", createInventoryPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(178, 34, 34)); // Dark red
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Pharmacy Manager Dashboard - " + currentUser.getFullName());
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
     * TAB 1: Pending Restock Requests
     * WORK REQUEST #4: Approval side (Manager approves Pharmacist's requests)
     */
    private JPanel createPendingRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title with alert
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("⚠ Pending Restock Requests");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(200, 0, 0));
        
        JLabel infoLabel = new JLabel(" (Work Request #4: From Pharmacists)");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);
        
        titlePanel.add(titleLabel);
        titlePanel.add(infoLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Request ID", "Medication", "Requested By", "Requested Qty", "Current Stock", "Priority", "Reason", "Date"};
        pendingRequestsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pendingRequestsTable = new JTable(pendingRequestsModel);
        pendingRequestsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        pendingRequestsTable.setRowHeight(25);
        pendingRequestsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Color code by priority
        pendingRequestsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                          boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String priority = (String) table.getValueAt(row, 5); // Priority column
                    switch (priority) {
                        case "URGENT":
                            c.setBackground(new Color(255, 200, 200)); // Light red
                            break;
                        case "HIGH":
                            c.setBackground(new Color(255, 230, 200)); // Light orange
                            break;
                        case "MEDIUM":
                            c.setBackground(new Color(255, 255, 200)); // Light yellow
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(pendingRequestsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadPendingRequests());
        
        JButton approveButton = new JButton("✓ Approve Request");
        approveButton.setBackground(new Color(34, 139, 34)); // Green
        approveButton.setForeground(Color.WHITE);
        approveButton.setFont(new Font("Arial", Font.BOLD, 12));
        approveButton.addActionListener(e -> approveSelectedRequest());
        
        JButton rejectButton = new JButton("✗ Reject Request");
        rejectButton.setBackground(new Color(178, 34, 34)); // Dark red
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFont(new Font("Arial", Font.BOLD, 12));
        rejectButton.addActionListener(e -> rejectSelectedRequest());
        
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> viewRequestDetails());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(approveButton);
        buttonsPanel.add(rejectButton);
        buttonsPanel.add(viewDetailsButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 2: All Restock Requests History
     */
    private JPanel createAllRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("All Restock Requests History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Request ID", "Medication", "Requested By", "Qty", "Priority", "Status", "Approved By", "Date"};
        allRequestsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        allRequestsTable = new JTable(allRequestsModel);
        allRequestsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        allRequestsTable.setRowHeight(25);
        allRequestsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(allRequestsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAllRequests());
        
        JButton statsButton = new JButton("View Statistics");
        statsButton.addActionListener(e -> showStatistics());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(statsButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * TAB 3: Inventory Overview
     */
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Medication Inventory Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Med ID", "Medication", "Category", "Strength", "Current Stock", "Status", "Price"};
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
        
        // Color code by stock level
        inventoryTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                          boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    try {
                        int stock = (Integer) table.getValueAt(row, 4); // Stock column
                        if (stock < 30) {
                            c.setBackground(new Color(255, 200, 200)); // Red - critical
                        } else if (stock < 50) {
                            c.setBackground(new Color(255, 230, 200)); // Orange - low
                        } else if (stock < 100) {
                            c.setBackground(new Color(255, 255, 200)); // Yellow - medium
                        } else {
                            c.setBackground(new Color(200, 255, 200)); // Green - good
                        }
                    } catch (Exception e) {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadInventory());
        
        JButton lowStockButton = new JButton("Show Low Stock Items");
        lowStockButton.addActionListener(e -> showLowStockItems());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(lowStockButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Load pending restock requests from pharmacists
     * WORK REQUEST #4: Manager receives requests
     */
    private void loadPendingRequests() {
        pendingRequestsModel.setRowCount(0);
        
        List<RestockRequest> requests = restockRequestDAO.getPendingRestockRequests();
        
        for (RestockRequest req : requests) {
            Object[] row = {
                req.getRequestId(),
                req.getMedicationName(),
                req.getRequesterName(),
                req.getRequestedQuantity(),
                req.getCurrentStock(),
                req.getPriority(),
                req.getReason(),
                req.getRequestedDate()
            };
            pendingRequestsModel.addRow(row);
        }
        
        // Update tab title with count
        tabbedPane.setTitleAt(0, "⚠ Pending Requests (" + requests.size() + ")");
    }
    
    /**
     * Load all restock requests
     */
    private void loadAllRequests() {
        allRequestsModel.setRowCount(0);
        
        List<RestockRequest> requests = restockRequestDAO.getAllRestockRequests();
        
        for (RestockRequest req : requests) {
            Object[] row = {
                req.getRequestId(),
                req.getMedicationName(),
                req.getRequesterName(),
                req.getRequestedQuantity(),
                req.getPriority(),
                req.getStatus(),
                req.getApproverName() != null ? req.getApproverName() : "N/A",
                req.getRequestedDate()
            };
            allRequestsModel.addRow(row);
        }
    }
    
    /**
     * Load inventory
     */
    private void loadInventory() {
        inventoryModel.setRowCount(0);
        
        List<Medication> medications = medicationDAO.getAllMedications();
        
        for (Medication med : medications) {
            int stock = medicationDAO.getMedicationStock(med.getMedicationId());
            
            String status;
            if (stock < 30) status = "CRITICAL";
            else if (stock < 50) status = "LOW";
            else if (stock < 100) status = "MEDIUM";
            else status = "GOOD";
            
            Object[] row = {
                med.getMedicationId(),
                med.getMedicationName(),
                med.getCategory(),
                med.getStrength(),
                stock,
                status,
                "$" + med.getUnitPrice()
            };
            inventoryModel.addRow(row);
        }
    }
    
    /**
     * Approve selected restock request
     * WORK REQUEST #4: Manager approves Pharmacist's request
     */
    private void approveSelectedRequest() {
        int selectedRow = pendingRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to approve!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int requestId = (Integer) pendingRequestsModel.getValueAt(selectedRow, 0);
        String medication = (String) pendingRequestsModel.getValueAt(selectedRow, 1);
        String requester = (String) pendingRequestsModel.getValueAt(selectedRow, 2);
        int quantity = (Integer) pendingRequestsModel.getValueAt(selectedRow, 3);
        
        // Confirm approval
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Approve restock request?\n\n" +
            "Requester: " + requester + "\n" +
            "Medication: " + medication + "\n" +
            "Quantity: " + quantity + " units\n\n" +
            "This will add " + quantity + " units to inventory.",
            "Confirm Approval", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Optional: Add manager notes
            String notes = JOptionPane.showInputDialog(this, 
                "Enter approval notes (optional):", 
                "Manager Notes", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (notes == null) notes = "Approved";
            
            boolean success = restockRequestDAO.approveRestockRequest(requestId, currentUser.getUserId(), notes);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Restock request APPROVED!\n\n" +
                    "Work Request #4 COMPLETED:\n" +
                    "Pharmacist → Manager → Inventory Updated\n\n" +
                    "Status changed: PENDING → APPROVED\n" +
                    "Inventory increased by " + quantity + " units!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPendingRequests(); // Refresh
                loadAllRequests();
                loadInventory();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to approve request!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Reject selected restock request
     */
    private void rejectSelectedRequest() {
        int selectedRow = pendingRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to reject!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int requestId = (Integer) pendingRequestsModel.getValueAt(selectedRow, 0);
        String medication = (String) pendingRequestsModel.getValueAt(selectedRow, 1);
        
        // Ask for rejection reason
        String reason = JOptionPane.showInputDialog(this, 
            "Enter reason for rejection:\n(This will be sent to the pharmacist)", 
            "Reject Request", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (reason != null && !reason.trim().isEmpty()) {
            boolean success = restockRequestDAO.updateRestockRequestStatus(requestId, "REJECTED");
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Request rejected.\nPharmacist will be notified.", 
                    "Rejected", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPendingRequests();
                loadAllRequests();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reject request!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View request details
     */
    private void viewRequestDetails() {
        int selectedRow = pendingRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request first!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int requestId = (Integer) pendingRequestsModel.getValueAt(selectedRow, 0);
        RestockRequest req = restockRequestDAO.getRestockRequestById(requestId);
        
        if (req != null) {
            String details = String.format(
                "Request ID: %d\n" +
                "Medication: %s\n" +
                "Requested By: %s\n" +
                "Requested Quantity: %d units\n" +
                "Current Stock: %d units\n" +
                "Priority: %s\n" +
                "Reason: %s\n" +
                "Status: %s\n" +
                "Requested Date: %s",
                req.getRequestId(),
                req.getMedicationName(),
                req.getRequesterName(),
                req.getRequestedQuantity(),
                req.getCurrentStock(),
                req.getPriority(),
                req.getReason(),
                req.getStatus(),
                req.getRequestedDate()
            );
            
            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 250));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Request Details", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Show low stock items
     */
    private void showLowStockItems() {
        StringBuilder lowStock = new StringBuilder("Low Stock Items (< 50 units):\n\n");
        int count = 0;
        
        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            int stock = (Integer) inventoryModel.getValueAt(i, 4);
            if (stock < 50) {
                String medName = (String) inventoryModel.getValueAt(i, 1);
                String status = (String) inventoryModel.getValueAt(i, 5);
                lowStock.append(String.format("%s: %d units (%s)\n", medName, stock, status));
                count++;
            }
        }
        
        if (count == 0) {
            lowStock.append("No low stock items found!");
        } else {
            lowStock.append("\nTotal: ").append(count).append(" medications need attention.");
        }
        
        JTextArea textArea = new JTextArea(lowStock.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Low Stock Report", 
            count > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show statistics
     */
    private void showStatistics() {
        List<RestockRequest> allRequests = restockRequestDAO.getAllRestockRequests();
        
        int pending = 0, approved = 0, rejected = 0;
        
        for (RestockRequest req : allRequests) {
            switch (req.getStatus()) {
                case "PENDING": pending++; break;
                case "APPROVED": approved++; break;
                case "REJECTED": rejected++; break;
            }
        }
        
        String stats = String.format(
            "Restock Request Statistics:\n\n" +
            "Total Requests: %d\n" +
            "Pending: %d\n" +
            "Approved: %d\n" +
            "Rejected: %d\n\n" +
            "Approval Rate: %.1f%%",
            allRequests.size(),
            pending,
            approved,
            rejected,
            allRequests.size() > 0 ? (approved * 100.0 / allRequests.size()) : 0
        );
        
        JOptionPane.showMessageDialog(this, stats, "Statistics", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Load all data when dashboard opens
     */
    private void loadData() {
        loadPendingRequests();
        loadAllRequests();
        loadInventory();
    }
}