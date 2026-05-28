package project1;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * Customer Dashboard - Main interface for customers with RHA branding
 * Privileges: Browse menu, Reserve tables, View reservations, Make payments
 */
public class CustomerDashboard extends JFrame implements ActionListener {
    
    // RHA Brand Colors
    private static final Color RHA_DARK = new Color(0x232326);      // #232326 - Dark background
    private static final Color RHA_LIGHT = new Color(0xEDEDED);     // #EDEDED - Light background
    private static final Color RHA_YELLOW = new Color(0xFCC660);    // #FCC660 - Primary accent
    private static final Color RHA_ORANGE = new Color(0xEB2F00);    // #EB2F00 - Secondary accent
    private static final Color TEXT_DARK = new Color(0x232326);     // Dark text
    private static final Color TEXT_LIGHT = new Color(0xEDEDED);    // Light text
    
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // Navigation Buttons
    private JButton menuButton;
    private JButton reservationButton;
    private JButton myReservationsButton;
    private JButton ordersButton;
    private JButton profileButton;
    private JButton logoutButton;
    
    // Menu Panel Components
    private JTable menuTable;
    private DefaultTableModel menuTableModel;
    private JComboBox<String> categoryFilter;
    
    // Reservation Panel Components
    private JComboBox<String> tableComboBox;
    private JSpinner guestsSpinner;
    private JTextField dateTimeField;
    private JTextArea reservationNotes;
    
    // My Reservations Panel Components
    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;
    
    // Orders Panel Components
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    
    public CustomerDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setFrameProperties();
        loadMenuData();
        loadMyReservations();
        loadMyOrders();
    }
    
    private void initComponents() {
        // Main Layout
        setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Navigation Panel
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.WEST);
        
        // Content Panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(RHA_LIGHT);
        
        // Add panels to CardLayout
        contentPanel.add(createMenuPanel(), "MENU");
        contentPanel.add(createReservationPanel(), "RESERVATION");
        contentPanel.add(createMyReservationsPanel(), "MY_RESERVATIONS");
        contentPanel.add(createOrdersPanel(), "ORDERS");
        contentPanel.add(createProfilePanel(), "PROFILE");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(RHA_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Left panel with logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(RHA_DARK);
        
        // RHA Logo Image
        JLabel logoLabel = new JLabel();
        try {
            // Try PNG first, then JPG
            ImageIcon logoIcon = null;
            if (new java.io.File("RHALogo.png").exists()) {
                logoIcon = new ImageIcon("RHALogo.png");
            } else if (new java.io.File("RHALogo.jpg").exists()) {
                logoIcon = new ImageIcon("RHALogo.jpg");
            }
            
            if (logoIcon != null && logoIcon.getIconWidth() > 0) {
                
                Image img = logoIcon.getImage();
                Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_AREA_AVERAGING);
                logoIcon = new ImageIcon(scaledImg);
                logoLabel.setIcon(logoIcon);
            } else {
                // Fallback to text logo
                logoLabel.setText("RHA");
                logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
                logoLabel.setForeground(RHA_YELLOW);
            }
        } catch (Exception e) {
            // Fallback to text logo if image not found
            logoLabel.setText("RHA");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
            logoLabel.setForeground(RHA_YELLOW);
        }
        
        JLabel titleLabel = new JLabel("Restaurant Management");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(TEXT_LIGHT);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(TEXT_LIGHT);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(welcomeLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 8, 8));
        panel.setBackground(RHA_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
        panel.setPreferredSize(new Dimension(200, 0));
        
        menuButton = createNavButton("Browse Menu");
        reservationButton = createNavButton("Make Reservation");
        myReservationsButton = createNavButton("My Reservations");
        ordersButton = createNavButton("My Orders");
        profileButton = createNavButton("My Profile");
        logoutButton = createNavButton("Logout");
        logoutButton.setBackground(RHA_ORANGE);
        logoutButton.setForeground(RHA_LIGHT); 

        
        panel.add(menuButton);
        panel.add(reservationButton);
        panel.add(myReservationsButton);
        panel.add(ordersButton);
        panel.add(profileButton);
        panel.add(new JLabel()); // Spacer
        panel.add(logoutButton);
        
        return panel;
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(TEXT_DARK);
        button.setBackground(RHA_YELLOW);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.addActionListener(this);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(RHA_ORANGE);
                button.setForeground(TEXT_LIGHT);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button == logoutButton) {
                    button.setBackground(RHA_ORANGE);
                    button.setForeground(TEXT_LIGHT);
                } else {
                    button.setBackground(RHA_YELLOW);
                    button.setForeground(TEXT_DARK);
                }
            }
        });
        
        return button;
    }
    
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(RHA_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Browse Menu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(RHA_DARK);
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(RHA_LIGHT);
        
        JLabel filterLabel = new JLabel("Filter by Category:");
        filterLabel.setForeground(RHA_DARK);
        filterPanel.add(filterLabel);
        
        categoryFilter = new JComboBox<>(new String[]{"All", "Sushi", "Seafood", "Main Course", 
                                                       "Appetizer", "Soup", "Vegetarian", "Signature"});
        categoryFilter.setBackground(Color.WHITE);
        categoryFilter.setForeground(RHA_DARK);
        categoryFilter.addActionListener(e -> filterMenu());
        filterPanel.add(categoryFilter);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(RHA_YELLOW);
        refreshBtn.setForeground(RHA_DARK);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadMenuData());
        filterPanel.add(refreshBtn);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(RHA_LIGHT);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Menu Table
        String[] columns = {"Item ID", "Name", "Price (SAR)", "Category", "Available"};
        menuTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuTable = new JTable(menuTableModel);
        menuTable.setRowHeight(28);
        menuTable.setBackground(Color.WHITE);
        menuTable.setForeground(RHA_DARK);
        menuTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        menuTable.getTableHeader().setBackground(RHA_YELLOW);
        menuTable.getTableHeader().setForeground(RHA_DARK);
        
        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(RHA_DARK, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(RHA_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Make a Reservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(RHA_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(RHA_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Table Selection
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel tableLabel = new JLabel("Select Table:");
        tableLabel.setForeground(RHA_DARK);
        formPanel.add(tableLabel, gbc);
        
        gbc.gridx = 1;
        tableComboBox = new JComboBox<>();
        tableComboBox.setBackground(Color.WHITE);
        tableComboBox.setPreferredSize(new Dimension(200, 35));
        formPanel.add(tableComboBox, gbc);
        
        // Number of Guests
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel guestsLabel = new JLabel("Number of Guests:");
        guestsLabel.setForeground(RHA_DARK);
        formPanel.add(guestsLabel, gbc);
        
        gbc.gridx = 1;
        guestsSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 12, 1));
        guestsSpinner.setPreferredSize(new Dimension(200, 35));
        formPanel.add(guestsSpinner, gbc);
        
        // Date and Time
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel dateTimeLabel = new JLabel("Date & Time (YYYY-MM-DD HH:MM):");
        dateTimeLabel.setForeground(RHA_DARK);
        formPanel.add(dateTimeLabel, gbc);
        
        gbc.gridx = 1;
        dateTimeField = new JTextField();
        dateTimeField.setPreferredSize(new Dimension(200, 35));
        dateTimeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(dateTimeField, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel notesLabel = new JLabel("Special Requests:");
        notesLabel.setForeground(RHA_DARK);
        formPanel.add(notesLabel, gbc);
        
        gbc.gridx = 1;
        reservationNotes = new JTextArea(3, 20);
        reservationNotes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JScrollPane notesScroll = new JScrollPane(reservationNotes);
        formPanel.add(notesScroll, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(RHA_LIGHT);
        
        JButton reserveBtn = new JButton("Reserve Table");
        reserveBtn.setBackground(RHA_YELLOW);
        reserveBtn.setForeground(RHA_DARK);
        reserveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        reserveBtn.setPreferredSize(new Dimension(160, 45));
        reserveBtn.setFocusPainted(false);
        reserveBtn.addActionListener(e -> makeReservation());
        
        JButton refreshBtn = new JButton("Refresh Tables");
        refreshBtn.setBackground(RHA_DARK);
        refreshBtn.setForeground(TEXT_LIGHT);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setPreferredSize(new Dimension(160, 45));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadAvailableTables());
        
        buttonPanel.add(reserveBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(RHA_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("My Reservations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(RHA_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Reservations Table
        String[] columns = {"Reservation ID", "Table", "Guests", "Date & Time", "Status"};
        reservationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setRowHeight(28);
        reservationsTable.setBackground(Color.WHITE);
        reservationsTable.setForeground(RHA_DARK);
        reservationsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        reservationsTable.getTableHeader().setBackground(RHA_YELLOW);
        reservationsTable.getTableHeader().setForeground(RHA_DARK);
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(RHA_DARK, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(RHA_LIGHT);
        
        JButton cancelBtn = new JButton("Cancel Reservation");
        cancelBtn.setBackground(RHA_ORANGE);
        cancelBtn.setForeground(TEXT_LIGHT);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setPreferredSize(new Dimension(180, 45));
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> cancelReservation());
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(RHA_YELLOW);
        refreshBtn.setForeground(RHA_DARK);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setPreferredSize(new Dimension(140, 45));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadMyReservations());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(RHA_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("My Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(RHA_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Orders Table
        String[] columns = {"Order ID", "Date", "Total (SAR)", "Status", "Payment"};
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setRowHeight(28);
        ordersTable.setBackground(Color.WHITE);
        ordersTable.setForeground(RHA_DARK);
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ordersTable.getTableHeader().setBackground(RHA_YELLOW);
        ordersTable.getTableHeader().setForeground(RHA_DARK);
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(RHA_DARK, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(RHA_LIGHT);
        
        JButton viewBtn = new JButton("View Details");
        viewBtn.setBackground(RHA_YELLOW);
        viewBtn.setForeground(RHA_DARK);
        viewBtn.setFont(new Font("Arial", Font.BOLD, 14));
        viewBtn.setPreferredSize(new Dimension(150, 45));
        viewBtn.setFocusPainted(false);
        viewBtn.addActionListener(e -> viewOrderDetails());
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(RHA_DARK);
        refreshBtn.setForeground(TEXT_LIGHT);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setPreferredSize(new Dimension(120, 45));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadMyOrders());
        
        buttonPanel.add(viewBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(RHA_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(RHA_LIGHT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Profile Information Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(RHA_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Display user information
        int row = 0;
        addProfileField(infoPanel, "Name:", currentUser.getFullName(), gbc, row++);
        addProfileField(infoPanel, "Username:", currentUser.getUsername(), gbc, row++);
        addProfileField(infoPanel, "Email:", currentUser.getEmail(), gbc, row++);
        addProfileField(infoPanel, "Phone:", currentUser.getPhoneNumber(), gbc, row++);
        addProfileField(infoPanel, "User Type:", currentUser.getUserType(), gbc, row++);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Update Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(RHA_LIGHT);
        
        JButton updateBtn = new JButton("Update Profile");
        updateBtn.setBackground(RHA_YELLOW);
        updateBtn.setForeground(RHA_DARK);
        updateBtn.setFont(new Font("Arial", Font.BOLD, 14));
        updateBtn.setPreferredSize(new Dimension(160, 45));
        updateBtn.setFocusPainted(false);
        updateBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Profile update feature coming soon!"));
        
        buttonPanel.add(updateBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addProfileField(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(RHA_DARK);
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        JLabel val = new JLabel(value != null ? value : "N/A");
        val.setFont(new Font("Arial", Font.PLAIN, 14));
        val.setForeground(RHA_DARK);
        panel.add(val, gbc);
    }
    
    private void loadMenuData() {
        menuTableModel.setRowCount(0);
        String sql = "SELECT DISTINCT m.ItemID, m.Name, m.Price, " +
                     "GROUP_CONCAT(mc.Category SEPARATOR ', ') as Categories, m.Availability " +
                     "FROM MenuItem m " +
                     "LEFT JOIN MenuItem_Category mc ON m.ItemID = mc.ItemID " +
                     "WHERE m.Availability = TRUE " +
                     "GROUP BY m.ItemID, m.Name, m.Price, m.Availability " +
                     "ORDER BY m.Name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("ItemID"),
                    rs.getString("Name"),
                    rs.getDouble("Price"),
                    rs.getString("Categories"),
                    rs.getBoolean("Availability") ? "Yes" : "No"
                };
                menuTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + e.getMessage());
        }
    }
    
    private void filterMenu() {
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        menuTableModel.setRowCount(0);
        
        String sql;
        if ("All".equals(selectedCategory)) {
            sql = "SELECT DISTINCT m.ItemID, m.Name, m.Price, " +
                  "GROUP_CONCAT(mc.Category SEPARATOR ', ') as Categories, m.Availability " +
                  "FROM MenuItem m " +
                  "LEFT JOIN MenuItem_Category mc ON m.ItemID = mc.ItemID " +
                  "WHERE m.Availability = TRUE " +
                  "GROUP BY m.ItemID, m.Name, m.Price, m.Availability " +
                  "ORDER BY m.Name";
        } else {
            sql = "SELECT DISTINCT m.ItemID, m.Name, m.Price, " +
                  "GROUP_CONCAT(mc.Category SEPARATOR ', ') as Categories, m.Availability " +
                  "FROM MenuItem m " +
                  "INNER JOIN MenuItem_Category mc ON m.ItemID = mc.ItemID " +
                  "WHERE m.Availability = TRUE AND mc.Category = ? " +
                  "GROUP BY m.ItemID, m.Name, m.Price, m.Availability " +
                  "ORDER BY m.Name";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (!"All".equals(selectedCategory)) {
                pstmt.setString(1, selectedCategory);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("ItemID"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getString("Categories"),
                        rs.getBoolean("Availability") ? "Yes" : "No"
                    };
                    menuTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering menu: " + e.getMessage());
        }
    }
    
    private void loadAvailableTables() {
        tableComboBox.removeAllItems();
        String sql = "SELECT TableNumber, Seats, Status FROM Restaurant_Table WHERE Status = 'Available' ORDER BY TableNumber";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int tableNum = rs.getInt("TableNumber");
                int capacity = rs.getInt("Seats");
                tableComboBox.addItem("Table " + tableNum + " (Capacity: " + capacity + ")");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading tables: " + e.getMessage());
        }
    }
    
    private void loadMyReservations() {
        reservationsTableModel.setRowCount(0);
        String sql = "SELECT * FROM Reservation WHERE CustomerID = ? ORDER BY DateTime DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, currentUser.getUserID());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("ReservationID"),
                        rs.getInt("TableNumber"),
                        rs.getInt("Guests"),
                        rs.getTimestamp("DateTime"),
                        rs.getString("Status")
                    };
                    reservationsTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + e.getMessage());
        }
    }
    
    private void loadMyOrders() {
        ordersTableModel.setRowCount(0);
        String sql = "SELECT o.*, p.Method as PaymentMethod " +
                     "FROM `Order` o " +
                     "LEFT JOIN Payment p ON o.OrderID = p.OrderID " +
                     "WHERE o.CustomerID = ? ORDER BY o.OrderDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, currentUser.getUserID());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("OrderID"),
                        rs.getTimestamp("OrderDate"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("Status"),
                        rs.getString("PaymentMethod") != null ? rs.getString("PaymentMethod") : "Pending"
                    };
                    ordersTableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }
    
    private void makeReservation() {
        if (tableComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a table.");
            return;
        }
        
        String tableStr = (String) tableComboBox.getSelectedItem();
        int tableNumber = Integer.parseInt(tableStr.split(" ")[1]);
        int guests = (Integer) guestsSpinner.getValue();
        String dateTime = dateTimeField.getText().trim();
        
        if (dateTime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter date and time.");
            return;
        }
        
        String sql = "INSERT INTO Reservation (ReservationID, CustomerID, TableNumber, Guests, Status, DateTime) " +
                     "VALUES (?, ?, ?, ?, 'Pending', ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get next reservation ID
            int nextID = 1;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(ReservationID) FROM Reservation")) {
                if (rs.next()) {
                    nextID = rs.getInt(1) + 1;
                }
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, nextID);
                pstmt.setInt(2, currentUser.getUserID());
                pstmt.setInt(3, tableNumber);
                pstmt.setInt(4, guests);
                pstmt.setString(5, dateTime + ":00");
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Reservation made successfully!");
                loadMyReservations();
                loadAvailableTables();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error making reservation: " + e.getMessage());
        }
    }
    
    private void cancelReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.");
            return;
        }
        
        int reservationID = (Integer) reservationsTableModel.getValueAt(selectedRow, 0);
        String status = (String) reservationsTableModel.getValueAt(selectedRow, 4);
        
        if ("Cancelled".equals(status) || "Completed".equals(status)) {
            JOptionPane.showMessageDialog(this, "This reservation cannot be cancelled.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this reservation?",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "UPDATE Reservation SET Status = 'Cancelled' WHERE ReservationID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, reservationID);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!");
                loadMyReservations();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error cancelling reservation: " + e.getMessage());
            }
        }
    }
    
    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to view details.");
            return;
        }
        
        int orderID = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
        
        // Create a dialog to show order details
        StringBuilder details = new StringBuilder();
        details.append("Order #").append(orderID).append(" Details:\n\n");
        
        String sql = "SELECT od.*, m.Name, m.Price " +
                     "FROM OrderDetails od " +
                     "JOIN MenuItem m ON od.ItemID = m.ItemID " +
                     "WHERE od.OrderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderID);
            try (ResultSet rs = pstmt.executeQuery()) {
                double total = 0;
                while (rs.next()) {
                    String name = rs.getString("Name");
                    int qty = rs.getInt("Quantity");
                    double price = rs.getDouble("Price");
                    String request = rs.getString("SpecialRequest");
                    
                    details.append("- ").append(name).append(" x").append(qty)
                           .append(" @ ").append(price).append(" SAR");
                    if (request != null && !request.isEmpty()) {
                        details.append(" (").append(request).append(")");
                    }
                    details.append("\n");
                    total += price * qty;
                }
                details.append("\nTotal: ").append(total).append(" SAR");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading order details: " + e.getMessage());
            return;
        }
        
        JOptionPane.showMessageDialog(this, details.toString(), "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void setFrameProperties() {
        setTitle("RHA Restaurant - Customer Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 650));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuButton) {
            cardLayout.show(contentPanel, "MENU");
        } else if (e.getSource() == reservationButton) {
            loadAvailableTables();
            cardLayout.show(contentPanel, "RESERVATION");
        } else if (e.getSource() == myReservationsButton) {
            loadMyReservations();
            cardLayout.show(contentPanel, "MY_RESERVATIONS");
        } else if (e.getSource() == ordersButton) {
            loadMyOrders();
            cardLayout.show(contentPanel, "ORDERS");
        } else if (e.getSource() == profileButton) {
            cardLayout.show(contentPanel, "PROFILE");
        } else if (e.getSource() == logoutButton) {
            logout();
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }
}