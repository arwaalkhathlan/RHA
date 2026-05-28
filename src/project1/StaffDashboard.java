package project1;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
/**
 * Staff Dashboard - Main interface for employees (Waiter/Cashier)
 * Privileges: View orders, Update order status, Handle billing, Manage tables
 */
public class StaffDashboard extends JFrame implements ActionListener {
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
    private JButton ordersButton;
    private JButton registerOrderButton;
    private JButton tablesButton;
    private JButton reservationsButton;
    private JButton billingButton;
    private JButton menuButton;
    private JButton manageEmployeesButton;
    private JButton logoutButton;
    
    // Orders Panel Components
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JComboBox<String> orderStatusFilter;
    
    // Tables Panel Components
    private JTable tablesTable;
    private DefaultTableModel tablesTableModel;
    
    // Reservations Panel Components
    private JTable reservationsTable;
    private DefaultTableModel reservationsTableModel;
    
    // Menu Panel Components
    private JTable menuTable;
    private DefaultTableModel menuTableModel;
    
    public StaffDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setFrameProperties();
        loadOrders();
        loadTables();
        loadReservations();
        loadMenu();
    }
    
    private void initComponents() {
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
        
        // Add panels to CardLayout
        contentPanel.add(createOrdersPanel(), "ORDERS");
        contentPanel.add(createRegisterOrderPanel(), "REGISTER_ORDER");
        contentPanel.add(createTablesPanel(), "TABLES");
        contentPanel.add(createReservationsPanel(), "RESERVATIONS");
        contentPanel.add(createBillingPanel(), "BILLING");
        contentPanel.add(createMenuPanel(), "MENU");
        
        // Show employee management for supervisors
        if (currentUser.isAdmin() || currentUser.isSupervisor()) {
            contentPanel.add(createManageEmployeesPanel(), "MANAGE_EMPLOYEES");
        }
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(RHA_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(RHA_DARK);
        
        // RHA Logo Image
        JLabel logoLabel = new JLabel();
        try {
            // Try PNG first, then JPG
            javax.swing.ImageIcon logoIcon = null;
            if (new java.io.File("RHALogo.png").exists()) {
                logoIcon = new javax.swing.ImageIcon("RHALogo.png");
            } else if (new java.io.File("RHALogo.jpg").exists()) {
                logoIcon = new javax.swing.ImageIcon("RHALogo.jpg");
            }
            
            // Check if image was loaded
            if (logoIcon != null && logoIcon.getIconWidth() > 0) {
                Image img = logoIcon.getImage();
                Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_AREA_AVERAGING);
                logoIcon = new javax.swing.ImageIcon(scaledImg);
                logoLabel.setIcon(logoIcon);
            } else {
                // Image file not found, use text fallback
                logoLabel.setText("RHA");
                logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
                logoLabel.setForeground(RHA_YELLOW);
            }
        } catch (Exception e) {
            // Fallback to text logo if image not found
            logoLabel.setText("RHA");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
            logoLabel.setForeground(RHA_YELLOW);
        }
        
        JLabel titleLabel = new JLabel("RHA Restaurant - Staff Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        String roleInfo = currentUser.getRole() != null ? currentUser.getRole() : "Staff";
        JLabel welcomeLabel = new JLabel(currentUser.getFullName() + " (" + roleInfo + ")");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(welcomeLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createNavigationPanel() {
        // Check if user is admin (for menu management) OR supervisor (for employee management)
        boolean isAdmin = currentUser.isAdmin();
        boolean isSupervisor = currentUser.isSupervisor();
        
        // Show employee management button if user is either admin OR supervisor
        boolean showEmployeeManagement = isAdmin || isSupervisor;
        int gridRows = showEmployeeManagement ? 9 : 8;
        
        JPanel panel = new JPanel(new GridLayout(gridRows, 1, 5, 5));
        panel.setBackground(RHA_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(180, 0));
        
        ordersButton = createNavButton("View Orders");
        registerOrderButton = createNavButton("Register Order");
        tablesButton = createNavButton("Manage Tables");
        reservationsButton = createNavButton("Reservations");
        billingButton = createNavButton("Billing & Receipts");
        menuButton = createNavButton("View Menu");
        logoutButton = createNavButton("Logout");
        logoutButton.setBackground(RHA_ORANGE);
        
        panel.add(ordersButton);
        panel.add(registerOrderButton);
        panel.add(tablesButton);
        panel.add(reservationsButton);
        panel.add(billingButton);
        panel.add(menuButton);
        
        if (showEmployeeManagement) {
            manageEmployeesButton = createNavButton("Manage Employees");
            manageEmployeesButton.setBackground(RHA_YELLOW);
            manageEmployeesButton.setForeground(Color.BLACK);
            panel.add(manageEmployeesButton);
        }
        
        panel.add(new JLabel());
        panel.add(logoutButton);
        
        return panel;
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(TEXT_LIGHT);
        button.setBackground(RHA_ORANGE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.addActionListener(this);
        return button;
    }
    
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Customer Orders"));
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Status:"));
        orderStatusFilter = new JComboBox<>(new String[]{"All", "Pending", "Preparing", "Served", "Completed"});
        orderStatusFilter.addActionListener(e -> filterOrders());
        filterPanel.add(orderStatusFilter);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadOrders());
        filterPanel.add(refreshBtn);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Orders Table
        String[] columns = {"Order ID", "Customer", "Table", "Total (SAR)", "Status", "Date"};
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setRowHeight(25);
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> viewOrderDetails());
        
        JButton updateStatusBtn = new JButton("Update Status");
        updateStatusBtn.setBackground(RHA_DARK);
        updateStatusBtn.setForeground(Color.WHITE);
        updateStatusBtn.addActionListener(e -> updateOrderStatus());
        
        JButton registerPaymentBtn = new JButton("Register Payment");
        registerPaymentBtn.setBackground(new Color(60, 179, 113));
        registerPaymentBtn.setForeground(Color.WHITE);
        registerPaymentBtn.addActionListener(e -> registerPayment());
        
        JButton notifyKitchenBtn = new JButton("Notify Kitchen");
        notifyKitchenBtn.setBackground(new Color(255, 140, 0));
        notifyKitchenBtn.setForeground(Color.WHITE);
        notifyKitchenBtn.addActionListener(e -> notifyKitchen());
        
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(registerPaymentBtn);
        buttonPanel.add(notifyKitchenBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Table Management"));
        
        // Tables Table
        String[] columns = {"Table #", "Section", "Floor", "Seats", "Status"};
        tablesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablesTable = new JTable(tablesTableModel);
        tablesTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tablesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadTables());
        
        JButton updateStatusBtn = new JButton("Update Table Status");
        updateStatusBtn.setBackground(RHA_DARK);
        updateStatusBtn.setForeground(Color.WHITE);
        updateStatusBtn.addActionListener(e -> updateTableStatus());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(updateStatusBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Reservations"));
        
        // Reservations Table
        String[] columns = {"Reservation ID", "Customer", "Table #", "Guests", "Date & Time", "Status"};
        reservationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsTable = new JTable(reservationsTableModel);
        reservationsTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadReservations());
        
        JButton confirmBtn = new JButton("Confirm Arrival");
        confirmBtn.setBackground(new Color(60, 179, 113));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.addActionListener(e -> confirmReservation());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(confirmBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Billing & Receipts"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Order ID:"), gbc);
        JTextField orderIdField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(orderIdField, gbc);

        JPanel paymentsPanel = new JPanel(new BorderLayout());
        paymentsPanel.setBorder(BorderFactory.createTitledBorder("Completed Orders (Receipts)"));

        String[] cols = { "Payment ID", "Order ID", "Amount", "Method", "Date" };
        DefaultTableModel paymentsModel = new DefaultTableModel(cols, 0);
        JTable paymentsTable = new JTable(paymentsModel);
        loadRecentPayments(paymentsModel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton printReceiptBtn = new JButton("Print Receipt");
        printReceiptBtn.setBackground(RHA_DARK);
        printReceiptBtn.setForeground(Color.WHITE);
        printReceiptBtn.setFocusPainted(false);
        printReceiptBtn.addActionListener(e -> {
            String orderIdText = orderIdField.getText().trim();
            if (!orderIdText.isEmpty()) {
                try {
                    int orderId = Integer.parseInt(orderIdText);
                    printReceiptByOrderId(orderId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Order ID.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter an Order ID.");
            }
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadRecentPayments(paymentsModel));

        btnPanel.add(printReceiptBtn);
        btnPanel.add(refreshBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnPanel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setPreferredSize(new Dimension(700, 250));
        paymentsPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(paymentsPanel, BorderLayout.CENTER);

        return panel;
    }
    private void printReceiptByOrderId(int orderID) {
    // 1. Retrieve payment info
    double paidAmount = 0;
    String paymentDate = "";
    String paymentMethod = "";
    int paymentID = 0;
    boolean paymentFound = false;

    String paymentSQL = "SELECT PaymentID, Amount, Date, Method FROM Payment WHERE OrderID = ? LIMIT 1";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(paymentSQL)) {

        ps.setInt(1, orderID);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                paymentFound = true;
                paymentID = rs.getInt("PaymentID");
                paidAmount = rs.getDouble("Amount");
                paymentDate = rs.getString("Date");
                paymentMethod = rs.getString("Method");
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error retrieving payment info: " + e.getMessage());
        return;
    }

    if (!paymentFound) {
        JOptionPane.showMessageDialog(this, "No payment found for Order ID: " + orderID);
        return;
    }

    // 2. Retrieve order items
    String orderSQL = "SELECT od.Quantity, m.Name, m.Price, od.SpecialRequest " +
                      "FROM OrderDetails od " +
                      "JOIN MenuItem m ON od.ItemID = m.ItemID " +
                      "WHERE od.OrderID = ?";
    
    StringBuilder receipt = new StringBuilder();
    double subtotal = 0;
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(orderSQL)) {

        ps.setInt(1, orderID);
        try (ResultSet rs = ps.executeQuery()) {
            
            // Build receipt header
            receipt.append("========================================\n");
            receipt.append("          RHA RESTAURANT RECEIPT        \n");
            receipt.append("========================================\n");
            receipt.append("Receipt #: ").append(paymentID).append("\n");
            receipt.append("Order #: ").append(orderID).append("\n");
            receipt.append("Date: ").append(paymentDate).append("\n");
            receipt.append("Payment Method: ").append(paymentMethod).append("\n\n");
            receipt.append(String.format("%-20s %-5s %-10s %-10s\n", "Item", "Qty", "Price", "Subtotal"));
            receipt.append("----------------------------------------\n");
            
            while (rs.next()) {
                String name = rs.getString("Name");
                int qty = rs.getInt("Quantity");
                double price = rs.getDouble("Price");
                double itemTotal = price * qty;
                subtotal += itemTotal;
                String special = rs.getString("SpecialRequest");
                
                if (special != null && !special.isEmpty()) {
                    name += " (" + special + ")";
                }
                
                if (name.length() > 20) {
                    name = name.substring(0, 17) + "...";
                }
                
                receipt.append(String.format("%-20s %-5d %-10.2f %-10.2f\n", name, qty, price, itemTotal));
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error retrieving order details: " + e.getMessage());
        return;
    }

    // 3. Add totals
    receipt.append("----------------------------------------\n");
    receipt.append(String.format("%-30s %10.2f\n", "SUBTOTAL:", subtotal));
    if (paidAmount < subtotal) {
        double discount = subtotal - paidAmount;
        receipt.append(String.format("%-30s %10.2f\n", "DISCOUNT:", discount));
    }
    receipt.append(String.format("%-30s %10.2f\n", "TOTAL PAID:", paidAmount));
    receipt.append("========================================\n");
    receipt.append("      Thank you for dining with us!     \n");
    receipt.append("         King Fahd Road, Riyadh         \n");
    receipt.append("            +966-11-456-7890            \n");
    receipt.append("========================================\n");

    // 4. Let user choose where to save the receipt
    String fileName = "receipt_" + paymentID + ".txt";
    
    javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
    fileChooser.setDialogTitle("Save Receipt");
    fileChooser.setSelectedFile(new java.io.File(fileName));
    
    // Set default directory to Desktop (user can navigate anywhere from there)
    String desktopPath = System.getProperty("user.home") + java.io.File.separator + "Desktop";
    java.io.File desktopDir = new java.io.File(desktopPath);
    if (desktopDir.exists()) {
        fileChooser.setCurrentDirectory(desktopDir);
    } else {
        // Fallback to Documents if Desktop doesn't exist
        String documentsPath = System.getProperty("user.home") + java.io.File.separator + "Documents";
        fileChooser.setCurrentDirectory(new java.io.File(documentsPath));
    }
    
    // Show save dialog
    int userSelection = fileChooser.showSaveDialog(this);
    
    if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
        java.io.File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        
        // Make sure it has .txt extension
        if (!filePath.toLowerCase().endsWith(".txt")) {
            filePath += ".txt";
        }
        
        try {
            java.io.FileWriter fileWriter = new java.io.FileWriter(filePath);
            java.io.BufferedWriter writer = new java.io.BufferedWriter(fileWriter);
            writer.write(receipt.toString());
            writer.close();
            
            JOptionPane.showMessageDialog(this, 
                "Receipt saved successfully!\n\nLocation: " + filePath, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Try to open the file automatically
            try {
                java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
            } catch (Exception ex) {
                // If can't open automatically
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error saving receipt: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    } else {
        // User cancelled - do nothing
        JOptionPane.showMessageDialog(this, 
            "Receipt save cancelled.", 
            "Cancelled", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
    
    
    private JPanel createRegisterOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Register New Order"));
        
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Selection"));
        
        JRadioButton existingCustomerRadio = new JRadioButton("Existing Customer", true);
        JRadioButton walkInRadio = new JRadioButton("Walk-in Customer");
        ButtonGroup customerGroup = new ButtonGroup();
        customerGroup.add(existingCustomerRadio);
        customerGroup.add(walkInRadio);
        
        JLabel customerIdLabel = new JLabel("Customer ID:");
        JTextField customerIdField = new JTextField(10);
        JLabel walkInLabel = new JLabel("(Auto-generated)");
        walkInLabel.setVisible(false);
        
        existingCustomerRadio.addActionListener(e -> {
            customerIdField.setEnabled(true);
            customerIdLabel.setText("Customer ID:");
            walkInLabel.setVisible(false);
        });
        
        walkInRadio.addActionListener(e -> {
            customerIdField.setEnabled(false);
            customerIdField.setText("");
            customerIdLabel.setText("Customer ID:");
            walkInLabel.setVisible(true);
        });
        
        customerPanel.add(existingCustomerRadio);
        customerPanel.add(walkInRadio);
        customerPanel.add(customerIdLabel);
        customerPanel.add(customerIdField);
        customerPanel.add(walkInLabel);
        
        topPanel.add(customerPanel, BorderLayout.NORTH);
        
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu Items"));
        
        String[] menuColumns = {"Item ID", "Name", "Price (SAR)"};
        DefaultTableModel availableItemsModel = new DefaultTableModel(menuColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable availableItemsTable = new JTable(availableItemsModel);
        availableItemsTable.setRowHeight(25);
        loadAvailableMenuItems(availableItemsModel);
        
        JScrollPane menuScrollPane = new JScrollPane(availableItemsTable);
        menuScrollPane.setPreferredSize(new Dimension(350, 200));
        menuPanel.add(menuScrollPane, BorderLayout.CENTER);
        
        JPanel menuBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton addToCartBtn = new JButton("Add to Cart →");
        addToCartBtn.setBackground(new Color(60, 179, 113));
        addToCartBtn.setForeground(Color.WHITE);
        
        JButton refreshMenuBtn = new JButton("Refresh Menu");
        refreshMenuBtn.addActionListener(e -> loadAvailableMenuItems(availableItemsModel));
        
        menuBtnPanel.add(addToCartBtn);
        menuBtnPanel.add(refreshMenuBtn);
        menuPanel.add(menuBtnPanel, BorderLayout.SOUTH);
        
        topPanel.add(menuPanel, BorderLayout.WEST);
        
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Order Cart"));
        
        String[] cartColumns = {"Item ID", "Name", "Price", "Qty", "Subtotal"};
        DefaultTableModel cartModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) return Integer.class;
                return super.getColumnClass(column);
            }
        };
        JTable cartTable = new JTable(cartModel);
        cartTable.setRowHeight(25);
        
        JLabel totalLabel = new JLabel("Total: 0.00 SAR");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setName("cartTotalLabel");
        
        cartTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 3) {
                int row = e.getFirstRow();
                int qty = (Integer) cartModel.getValueAt(row, 3);
                double price = Double.parseDouble(cartModel.getValueAt(row, 2).toString());
                double subtotal = price * qty;
                cartModel.setValueAt(String.format("%.2f", subtotal), row, 4);
                updateCartTotalDirect(cartModel, totalLabel);
            }
        });
        
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartScrollPane.setPreferredSize(new Dimension(400, 200));
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        
        JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton increaseQtyBtn = new JButton("+");
        increaseQtyBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row >= 0) {
                int qty = (Integer) cartModel.getValueAt(row, 3);
                cartModel.setValueAt(qty + 1, row, 3);
            }
        });
        
        JButton decreaseQtyBtn = new JButton("-");
        decreaseQtyBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row >= 0) {
                int qty = (Integer) cartModel.getValueAt(row, 3);
                if (qty > 1) {
                    cartModel.setValueAt(qty - 1, row, 3);
                }
            }
        });
        
        JButton removeItemBtn = new JButton("Remove");
        removeItemBtn.setBackground(new Color(220, 53, 69));
        removeItemBtn.setForeground(Color.WHITE);
        removeItemBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row >= 0) {
                cartModel.removeRow(row);
                updateCartTotalDirect(cartModel, totalLabel);
            }
        });
        
        cartButtonPanel.add(increaseQtyBtn);
        cartButtonPanel.add(decreaseQtyBtn);
        cartButtonPanel.add(removeItemBtn);
        cartButtonPanel.add(Box.createHorizontalStrut(20));
        cartButtonPanel.add(totalLabel);
        
        cartPanel.add(cartButtonPanel, BorderLayout.SOUTH);
        
        topPanel.add(cartPanel, BorderLayout.CENTER);
        
        addToCartBtn.addActionListener(e -> {
            int row = availableItemsTable.getSelectedRow();
            if (row >= 0) {
                int itemId = (Integer) availableItemsModel.getValueAt(row, 0);
                String itemName = (String) availableItemsModel.getValueAt(row, 1);
                double price = (Double) availableItemsModel.getValueAt(row, 2);
                
                boolean found = false;
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    if ((Integer) cartModel.getValueAt(i, 0) == itemId) {
                        int qty = (Integer) cartModel.getValueAt(i, 3);
                        cartModel.setValueAt(qty + 1, i, 3);
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    cartModel.addRow(new Object[]{
                        itemId,
                        itemName,
                        String.format("%.2f", price),
                        1,
                        String.format("%.2f", price)
                    });
                }
                updateCartTotalDirect(cartModel, totalLabel);
            }
        });
        
        panel.add(topPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JPanel specialRequestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        specialRequestPanel.add(new JLabel("Special Requests:"));
        JTextField specialRequestField = new JTextField(30);
        specialRequestPanel.add(specialRequestField);
        
        bottomPanel.add(specialRequestPanel, BorderLayout.NORTH);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.setBackground(new Color(60, 179, 113));
        placeOrderBtn.setForeground(Color.WHITE);
        placeOrderBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        placeOrderBtn.setPreferredSize(new Dimension(150, 40));
        placeOrderBtn.addActionListener(e -> {
            if (cartModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Cart is empty. Please add items.");
                return;
            }
            
            int customerId;
            if (walkInRadio.isSelected()) {
                customerId = generateWalkInCustomerId();
                if (customerId == -1) {
                    JOptionPane.showMessageDialog(this, "Error generating walk-in customer.");
                    return;
                }
            } else {
                String customerIdText = customerIdField.getText().trim();
                if (customerIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter Customer ID.");
                    return;
                }
                try {
                    customerId = Integer.parseInt(customerIdText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Customer ID.");
                    return;
                }
            }
            
            int[] itemIds = new int[cartModel.getRowCount()];
            int[] quantities = new int[cartModel.getRowCount()];
            
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                itemIds[i] = (Integer) cartModel.getValueAt(i, 0);
                quantities[i] = (Integer) cartModel.getValueAt(i, 3);
            }
            
            String request = specialRequestField.getText().trim();
            String specialRequest = request.isEmpty() ? null : request;
            
            if (createNewOrder(customerId, itemIds, quantities, specialRequest)) {
                JOptionPane.showMessageDialog(this, "Order placed successfully!");
                cartModel.setRowCount(0);
                customerIdField.setText("");
                specialRequestField.setText("");
                updateCartTotalDirect(cartModel, totalLabel);
                loadOrders();
            }
        });
        
        JButton clearCartBtn = new JButton("Clear Cart");
        clearCartBtn.setBackground(new Color(220, 53, 69));
        clearCartBtn.setForeground(Color.WHITE);
        clearCartBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        clearCartBtn.setPreferredSize(new Dimension(150, 40));
        clearCartBtn.addActionListener(e -> {
            cartModel.setRowCount(0);
            updateCartTotalDirect(cartModel, totalLabel);
        });
        
        actionPanel.add(placeOrderBtn);
        actionPanel.add(clearCartBtn);
        
        bottomPanel.add(actionPanel, BorderLayout.CENTER);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadAvailableMenuItems(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT ItemID, Name, Price FROM MenuItem WHERE Availability = TRUE ORDER BY Name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("ItemID"),
                    rs.getString("Name"),
                    rs.getDouble("Price")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            System.err.println("Error loading menu items: " + e.getMessage());
        }
    }
    
    private void updateCartTotal(DefaultTableModel cartModel) {
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String subtotalStr = cartModel.getValueAt(i, 4).toString();
            total += Double.parseDouble(subtotalStr);
        }
        
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                findAndUpdateTotalLabel((JPanel) comp, total);
            }
        }
    }
    
    private void updateCartTotalDirect(DefaultTableModel cartModel, JLabel totalLabel) {
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String subtotalStr = cartModel.getValueAt(i, 4).toString();
            total += Double.parseDouble(subtotalStr);
        }
        totalLabel.setText(String.format("Total: %.2f SAR", total));
    }
    
    private void findAndUpdateTotalLabel(JPanel panel, double total) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().startsWith("Total:")) {
                    label.setText(String.format("Total: %.2f SAR", total));
                    return;
                }
            } else if (comp instanceof JPanel) {
                findAndUpdateTotalLabel((JPanel) comp, total);
            }
        }
    }
    
    private int generateWalkInCustomerId() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            int nextUserID = 1;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(UserID) FROM Users")) {
                if (rs.next()) {
                    nextUserID = rs.getInt(1) + 1;
                }
            }
            
            String insertUserSQL = "INSERT INTO Users (UserID, FName, MName, LName, Username, Email, PhoneNumber, Password, UserType) " +
                                   "VALUES (?, 'Walk-In', NULL, 'Customer', ?, NULL, NULL, 'walkin', 'Customer')";
            try (PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {
                pstmt.setInt(1, nextUserID);
                pstmt.setString(2, "walkin" + nextUserID);
                pstmt.executeUpdate();
            }
            
            String insertCustomerSQL = "INSERT INTO Customer (CustomerID) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertCustomerSQL)) {
                pstmt.setInt(1, nextUserID);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return nextUserID;
            
        } catch (SQLException e) {
            System.err.println("Error creating walk-in customer: " + e.getMessage());
            return -1;
        }
    }
    
    private JPanel createManageEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Manage Employees"));
        
        boolean isGeneralManager = currentUser.getRole() != null && 
                                   currentUser.getRole().equalsIgnoreCase("General Manager");
        
        // Employee Table
        String[] columns = {"Employee ID", "Name", "Role", "Salary", "Supervisor"};
        DefaultTableModel employeesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable employeesTable = new JTable(employeesTableModel);
        employeesTable.setRowHeight(25);
        employeesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(employeesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadEmployees(employeesTableModel));
        buttonPanel.add(refreshBtn);
        
        // Only show Add/Edit/Delete buttons for General Manager
        if (isGeneralManager) {
            JButton addEmployeeBtn = new JButton("Add Employee");
            addEmployeeBtn.setBackground(new Color(60, 179, 113));
            addEmployeeBtn.setForeground(Color.WHITE);
            addEmployeeBtn.addActionListener(e -> addEmployee(employeesTableModel));
            
            JButton editEmployeeBtn = new JButton("Edit Employee");
            editEmployeeBtn.setBackground(RHA_DARK);
            editEmployeeBtn.setForeground(Color.WHITE);
            editEmployeeBtn.addActionListener(e -> {
                int selectedRow = employeesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int employeeId = (Integer) employeesTableModel.getValueAt(selectedRow, 0);
                    editEmployee(employeeId, employeesTableModel);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select an employee to edit.");
                }
            });
            
            JButton deleteEmployeeBtn = new JButton("Delete Employee");
            deleteEmployeeBtn.setBackground(new Color(220, 53, 69));
            deleteEmployeeBtn.setForeground(Color.WHITE);
            deleteEmployeeBtn.addActionListener(e -> {
                int selectedRow = employeesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int employeeId = (Integer) employeesTableModel.getValueAt(selectedRow, 0);
                    String employeeName = (String) employeesTableModel.getValueAt(selectedRow, 1);
                    deleteEmployee(employeeId, employeeName, employeesTableModel);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
                }
            });
            
            buttonPanel.add(addEmployeeBtn);
            buttonPanel.add(editEmployeeBtn);
            buttonPanel.add(deleteEmployeeBtn);
        }
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        loadEmployees(employeesTableModel);
        
        return panel;
    }
    
    // Data Loading Methods
    private void loadOrders() {
        ordersTableModel.setRowCount(0);
        String sql = "SELECT o.*, u.FName, u.LName " +
                     "FROM `Order` o " +
                     "JOIN Customer c ON o.CustomerID = c.CustomerID " +
                     "JOIN Users u ON c.CustomerID = u.UserID " +
                     "WHERE o.OrderID NOT IN (SELECT DISTINCT OrderID FROM Payment WHERE Method NOT LIKE 'Tip%') " +
                     "ORDER BY o.OrderDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("OrderID"),
                    rs.getString("FName") + " " + rs.getString("LName"),
                    "-",
                    rs.getDouble("TotalAmount"),
                    rs.getString("Status"),
                    rs.getTimestamp("OrderDate")
                };
                ordersTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }
    
    private void filterOrders() {
        String status = (String) orderStatusFilter.getSelectedItem();
        ordersTableModel.setRowCount(0);
        
        String sql = "SELECT o.*, u.FName, u.LName " +
                     "FROM `Order` o " +
                     "JOIN Customer c ON o.CustomerID = c.CustomerID " +
                     "JOIN Users u ON c.CustomerID = u.UserID " +
                     "WHERE o.OrderID NOT IN (SELECT DISTINCT OrderID FROM Payment WHERE Method NOT LIKE 'Tip%') ";
        
        if (!"All".equals(status)) {
            sql += "AND o.Status = '" + status + "' ";
        }
        sql += "ORDER BY o.OrderDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("OrderID"),
                    rs.getString("FName") + " " + rs.getString("LName"),
                    "-",
                    rs.getDouble("TotalAmount"),
                    rs.getString("Status"),
                    rs.getTimestamp("OrderDate")
                };
                ordersTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering orders: " + e.getMessage());
        }
    }
    
    private void loadTables() {
        tablesTableModel.setRowCount(0);
        String sql = "SELECT * FROM Restaurant_Table WHERE RestaurantID = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("TableNumber"),
                    rs.getString("Section"),
                    rs.getInt("Floor"),
                    rs.getInt("Seats"),
                    rs.getString("Status")
                };
                tablesTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading tables: " + e.getMessage());
        }
    }
    
    private void loadReservations() {
        reservationsTableModel.setRowCount(0);
        String sql = "SELECT r.*, u.FName, u.LName " +
                     "FROM Reservation r " +
                     "JOIN Customer c ON r.CustomerID = c.CustomerID " +
                     "JOIN Users u ON c.CustomerID = u.UserID " +
                     "ORDER BY r.DateTime DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("ReservationID"),
                    rs.getString("FName") + " " + rs.getString("LName"),
                    rs.getInt("TableNumber"),
                    rs.getInt("Guests"),
                    rs.getTimestamp("DateTime"),
                    rs.getString("Status")
                };
                reservationsTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + e.getMessage());
        }
    }
    
    private void loadMenu() {
        menuTableModel.setRowCount(0);
        String sql = "SELECT m.ItemID, m.Name, m.Price, m.Availability, " +
                     "GROUP_CONCAT(mc.Category SEPARATOR ', ') AS Categories " +
                     "FROM MenuItem m " +
                     "LEFT JOIN MenuItem_Category mc ON m.ItemID = mc.ItemID " +
                     "WHERE m.RestaurantID = 1 " +
                     "GROUP BY m.ItemID, m.Name, m.Price, m.Availability";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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
        private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Menu Items (View Only)"));
        
        // Menu Table
        String[] columns = {"Item ID", "Name", "Price (SAR)", "Category", "Available"};
        menuTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuTable = new JTable(menuTableModel);
        menuTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(menuTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadMenu());
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        
        
        // Add New Dish Section (Supervisor Only)
        // Only show this section if the user is a supervisora and specifically (General Manager or Floor Manager)
        if (currentUser.isMangaer()) {
            JPanel addDishPanel = new JPanel(new GridBagLayout());
            addDishPanel.setBorder(BorderFactory.createTitledBorder("Add New Dish"));
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.insets = new Insets(8, 8, 8, 8);
            gbc2.anchor = GridBagConstraints.WEST;

            // Item Name
            gbc2.gridx = 0; gbc2.gridy = 0;
            addDishPanel.add(new JLabel("Name:"), gbc2);
            JTextField nameField = new JTextField(12);
            gbc2.gridx = 1;
            addDishPanel.add(nameField, gbc2);

            // Price
            gbc2.gridx = 0; gbc2.gridy = 1;
            addDishPanel.add(new JLabel("Price (SAR):"), gbc2);
            JTextField priceField = new JTextField(12);
            gbc2.gridx = 1;
            addDishPanel.add(priceField, gbc2);

            // Category
            gbc2.gridx = 0; gbc2.gridy = 2;
            addDishPanel.add(new JLabel("Category:"), gbc2);
            JTextField categoryField = new JTextField(12);
            gbc2.gridx = 1;
            addDishPanel.add(categoryField, gbc2);

            // Availability
            gbc2.gridx = 0; gbc2.gridy = 3;
            addDishPanel.add(new JLabel("Available:"), gbc2);
            JCheckBox availableBox = new JCheckBox();
            availableBox.setSelected(true);
            gbc2.gridx = 1;
            addDishPanel.add(availableBox, gbc2);

            // Add button
            JButton addDishBtn = new JButton("Add Dish");
            addDishBtn.setBackground(RHA_ORANGE);
            addDishBtn.setForeground(Color.BLACK);
            addDishBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String priceStr = priceField.getText().trim();
                String category = categoryField.getText().trim();

                if (name.isEmpty() || priceStr.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.");
                    return;
                }

                try (Connection conn = DatabaseConnection.getConnection()) {
                    
                    // Generate next ItemID
                    int nextId = 1;
                    ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(ItemID) FROM MenuItem");
                    if (rs.next()) nextId = rs.getInt(1) + 1;

                    // Insert into MenuItem
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO MenuItem (ItemID, RestaurantID, Price, Name, Availability) VALUES (?, ?, ?, ?, ?)"
                    );
                    ps.setInt(1, nextId);
                    ps.setInt(2, 1); // this changes if there was more then one restrount
                    ps.setDouble(3, Double.parseDouble(priceStr));
                    ps.setString(4, name);
                    ps.setBoolean(5, availableBox.isSelected());
                    ps.executeUpdate();

                    // Insert category
                    PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO MenuItem_Category (ItemID, Category) VALUES (?, ?)"
                    );
                    ps2.setInt(1, nextId);
                    ps2.setString(2, category);
                    ps2.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Dish added successfully!");

                    nameField.setText("");
                    priceField.setText("");
                    categoryField.setText("");
                    availableBox.setSelected(true);

                    loadMenu(); // refresh table
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });

            // Add button to panel
            gbc2.gridx = 0; gbc2.gridy = 4; gbc2.gridwidth = 2;
            addDishPanel.add(addDishBtn, gbc2);

            // Add the section under the table
            panel.add(addDishPanel, BorderLayout.SOUTH);
        }
        return panel;
    }
    private void loadEmployees(DefaultTableModel model) {
        model.setRowCount(0);
        
       
        String sql = "SELECT e.EmployeeID, u.FName, u.MName, u.LName, er.Role, e.Salary, " +
                     "s.FName AS SupervisorFName, s.LName AS SupervisorLName " +
                     "FROM Employee e " +
                     "JOIN Users u ON e.EmployeeID = u.UserID " +
                     "LEFT JOIN Employee_Role er ON e.EmployeeID = er.EmployeeID " +
                     "LEFT JOIN Employee sup ON e.SupervisorID = sup.EmployeeID " +
                     "LEFT JOIN Users s ON sup.EmployeeID = s.UserID " +
                     "WHERE e.RestaurantID = 1 ";
        
        // If not General Manager, only show employees they supervise
        if (!currentUser.isAdmin()) {
            sql += "AND e.SupervisorID = " + currentUser.getUserID() + " ";
        }
        
        sql += "ORDER BY e.EmployeeID";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String fullName = rs.getString("FName");
                String mName = rs.getString("MName");
                if (mName != null && !mName.isEmpty()) {
                    fullName += " " + mName;
                }
                fullName += " " + rs.getString("LName");
                
                String supervisor = rs.getString("SupervisorFName");
                if (supervisor != null) {
                    supervisor += " " + rs.getString("SupervisorLName");
                } else {
                    supervisor = "None";
                }
                
                Object[] row = {
                    rs.getInt("EmployeeID"),
                    fullName,
                    rs.getString("Role"),
                    String.format("%.2f SAR", rs.getDouble("Salary")),
                    supervisor
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + e.getMessage());
        }
    }
    
    private void addEmployee(DefaultTableModel model) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField fnameField = new JTextField(15);
        JTextField mnameField = new JTextField(15);
        JTextField lnameField = new JTextField(15);
        JTextField usernameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField salaryField = new JTextField(15);
        
        String[] roles = {"Waiter", "Cashier", "Hostess", "Sushi Chef", "Line Cook", 
                         "Kitchen Assistant", "Floor Manager", "Head Chef", "General Manager"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        
        JComboBox<Integer> supervisorCombo = new JComboBox<>();
        supervisorCombo.addItem(null); // No supervisor option
        loadSupervisors(supervisorCombo);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(fnameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Middle Name:"), gbc);
        gbc.gridx = 1;
        panel.add(mnameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        panel.add(lnameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Salary (SAR):"), gbc);
        gbc.gridx = 1;
        panel.add(salaryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("Supervisor:"), gbc);
        gbc.gridx = 1;
        panel.add(supervisorCombo, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Employee", 
                                                   JOptionPane.OK_CANCEL_OPTION, 
                                                   JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String fname = fnameField.getText().trim();
            String mname = mnameField.getText().trim();
            String lname = lnameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String salaryText = salaryField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            Integer supervisorId = (Integer) supervisorCombo.getSelectedItem();
            
            if (fname.isEmpty() || lname.isEmpty() || username.isEmpty() || 
                password.isEmpty() || salaryText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
                return;
            }
            
            try {
                double salary = Double.parseDouble(salaryText);
                
                try (Connection conn = DatabaseConnection.getConnection()) {
                    conn.setAutoCommit(false);
                    
                    int newUserId = getNextUserId(conn);
                    
                    String insertUserSQL = "INSERT INTO Users (UserID, FName, MName, LName, Username, Email, PhoneNumber, Password, UserType) " +
                                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Employee')";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {
                        pstmt.setInt(1, newUserId);
                        pstmt.setString(2, fname);
                        pstmt.setString(3, mname.isEmpty() ? null : mname);
                        pstmt.setString(4, lname);
                        pstmt.setString(5, username);
                        pstmt.setString(6, email.isEmpty() ? null : email);
                        pstmt.setString(7, phone.isEmpty() ? null : phone);
                        pstmt.setString(8, password);
                        pstmt.executeUpdate();
                    }
                    
                    String insertEmployeeSQL = "INSERT INTO Employee (EmployeeID, RestaurantID, Salary, SupervisorID) " +
                                              "VALUES (?, 1, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertEmployeeSQL)) {
                        pstmt.setInt(1, newUserId);
                        pstmt.setDouble(2, salary);
                        if (supervisorId == null) {
                            pstmt.setNull(3, java.sql.Types.INTEGER);
                        } else {
                            pstmt.setInt(3, supervisorId);
                        }
                        pstmt.executeUpdate();
                    }
                    
                    String insertRoleSQL = "INSERT INTO Employee_Role (EmployeeID, Role) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertRoleSQL)) {
                        pstmt.setInt(1, newUserId);
                        pstmt.setString(2, role);
                        pstmt.executeUpdate();
                    }
                    
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Employee added successfully!");
                    loadEmployees(model);
                    
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid salary amount.");
            }
        }
    }
    
    private int getNextUserId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(UserID) FROM Users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            return 1;
        }
    }
    
    private void loadSupervisors(JComboBox<Integer> comboBox) {
        String sql = "SELECT e.EmployeeID, u.FName, u.LName " +
                     "FROM Employee e " +
                     "JOIN Users u ON e.EmployeeID = u.UserID " +
                     "JOIN Employee_Role er ON e.EmployeeID = er.EmployeeID " +
                     "WHERE er.Role IN ('General Manager', 'Floor Manager', 'Head Chef') " +
                     "ORDER BY u.FName, u.LName";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("EmployeeID");
                String name = rs.getString("FName") + " " + rs.getString("LName");
                comboBox.addItem(id);
                comboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
                    @Override
                    public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, 
                                                                           int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value == null) {
                            setText("No Supervisor");
                        } else if (value instanceof Integer) {
                            try (Connection conn = DatabaseConnection.getConnection()) {
                                String query = "SELECT FName, LName FROM Users WHERE UserID = ?";
                                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                                    pstmt.setInt(1, (Integer) value);
                                    try (ResultSet rs = pstmt.executeQuery()) {
                                        if (rs.next()) {
                                            setText(value + " - " + rs.getString("FName") + " " + rs.getString("LName"));
                                        }
                                    }
                                }
                            } catch (SQLException e) {
                                setText(value.toString());
                            }
                        }
                        return this;
                    }
                });
            }
        } catch (SQLException e) {
            System.err.println("Error loading supervisors: " + e.getMessage());
        }
    }
    
    private void editEmployee(int employeeId, DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT u.*, e.Salary, e.SupervisorID, er.Role " +
                        "FROM Users u " +
                        "JOIN Employee e ON u.UserID = e.EmployeeID " +
                        "LEFT JOIN Employee_Role er ON e.EmployeeID = er.EmployeeID " +
                        "WHERE u.UserID = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, employeeId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        JPanel panel = new JPanel(new GridBagLayout());
                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.insets = new Insets(5, 5, 5, 5);
                        gbc.anchor = GridBagConstraints.WEST;
                        gbc.fill = GridBagConstraints.HORIZONTAL;
                        
                        JTextField fnameField = new JTextField(rs.getString("FName"), 15);
                        JTextField mnameField = new JTextField(rs.getString("MName") != null ? rs.getString("MName") : "", 15);
                        JTextField lnameField = new JTextField(rs.getString("LName"), 15);
                        JTextField emailField = new JTextField(rs.getString("Email") != null ? rs.getString("Email") : "", 15);
                        JTextField phoneField = new JTextField(rs.getString("PhoneNumber") != null ? rs.getString("PhoneNumber") : "", 15);
                        JTextField salaryField = new JTextField(String.valueOf(rs.getDouble("Salary")), 15);
                        
                        String[] roles = {"Waiter", "Cashier", "Hostess", "Sushi Chef", "Line Cook", 
                                         "Kitchen Assistant", "Floor Manager", "Head Chef", "General Manager"};
                        JComboBox<String> roleCombo = new JComboBox<>(roles);
                        String currentRole = rs.getString("Role");
                        if (currentRole != null) {
                            roleCombo.setSelectedItem(currentRole);
                        }
                        
                        JComboBox<Integer> supervisorCombo = new JComboBox<>();
                        supervisorCombo.addItem(null);
                        loadSupervisors(supervisorCombo);
                        Integer currentSupervisor = rs.getInt("SupervisorID");
                        if (rs.wasNull()) {
                            currentSupervisor = null;
                        }
                        supervisorCombo.setSelectedItem(currentSupervisor);
                        
                        gbc.gridx = 0; gbc.gridy = 0;
                        panel.add(new JLabel("Employee ID:"), gbc);
                        gbc.gridx = 1;
                        panel.add(new JLabel(String.valueOf(employeeId)), gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 1;
                        panel.add(new JLabel("First Name:"), gbc);
                        gbc.gridx = 1;
                        panel.add(fnameField, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 2;
                        panel.add(new JLabel("Middle Name:"), gbc);
                        gbc.gridx = 1;
                        panel.add(mnameField, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 3;
                        panel.add(new JLabel("Last Name:"), gbc);
                        gbc.gridx = 1;
                        panel.add(lnameField, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 4;
                        panel.add(new JLabel("Email:"), gbc);
                        gbc.gridx = 1;
                        panel.add(emailField, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 5;
                        panel.add(new JLabel("Phone Number:"), gbc);
                        gbc.gridx = 1;
                        panel.add(phoneField, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 6;
                        panel.add(new JLabel("Salary (SAR):"), gbc);
                        gbc.gridx = 1;
                        panel.add(salaryField, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 7;
                        panel.add(new JLabel("Role:"), gbc);
                        gbc.gridx = 1;
                        panel.add(roleCombo, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 8;
                        panel.add(new JLabel("Supervisor:"), gbc);
                        gbc.gridx = 1;
                        panel.add(supervisorCombo, gbc);
                        
                        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Employee", 
                                                                   JOptionPane.OK_CANCEL_OPTION, 
                                                                   JOptionPane.PLAIN_MESSAGE);
                        
                        if (result == JOptionPane.OK_OPTION) {
                            String fname = fnameField.getText().trim();
                            String mname = mnameField.getText().trim();
                            String lname = lnameField.getText().trim();
                            String email = emailField.getText().trim();
                            String phone = phoneField.getText().trim();
                            String salaryText = salaryField.getText().trim();
                            String role = (String) roleCombo.getSelectedItem();
                            Integer supervisorId = (Integer) supervisorCombo.getSelectedItem();
                            
                            if (fname.isEmpty() || lname.isEmpty() || salaryText.isEmpty()) {
                                JOptionPane.showMessageDialog(this, "First name, last name, and salary are required.");
                                return;
                            }
                            
                            try {
                                double salary = Double.parseDouble(salaryText);
                                conn.setAutoCommit(false);
                                
                                String updateUserSQL = "UPDATE Users SET FName = ?, MName = ?, LName = ?, Email = ?, PhoneNumber = ? WHERE UserID = ?";
                                try (PreparedStatement updateUser = conn.prepareStatement(updateUserSQL)) {
                                    updateUser.setString(1, fname);
                                    updateUser.setString(2, mname.isEmpty() ? null : mname);
                                    updateUser.setString(3, lname);
                                    updateUser.setString(4, email.isEmpty() ? null : email);
                                    updateUser.setString(5, phone.isEmpty() ? null : phone);
                                    updateUser.setInt(6, employeeId);
                                    updateUser.executeUpdate();
                                }
                                
                                String updateEmployeeSQL = "UPDATE Employee SET Salary = ?, SupervisorID = ? WHERE EmployeeID = ?";
                                try (PreparedStatement updateEmp = conn.prepareStatement(updateEmployeeSQL)) {
                                    updateEmp.setDouble(1, salary);
                                    if (supervisorId == null) {
                                        updateEmp.setNull(2, java.sql.Types.INTEGER);
                                    } else {
                                        updateEmp.setInt(2, supervisorId);
                                    }
                                    updateEmp.setInt(3, employeeId);
                                    updateEmp.executeUpdate();
                                }
                                
                                String deleteRoleSQL = "DELETE FROM Employee_Role WHERE EmployeeID = ?";
                                try (PreparedStatement deleteRole = conn.prepareStatement(deleteRoleSQL)) {
                                    deleteRole.setInt(1, employeeId);
                                    deleteRole.executeUpdate();
                                }
                                
                                String insertRoleSQL = "INSERT INTO Employee_Role (EmployeeID, Role) VALUES (?, ?)";
                                try (PreparedStatement insertRole = conn.prepareStatement(insertRoleSQL)) {
                                    insertRole.setInt(1, employeeId);
                                    insertRole.setString(2, role);
                                    insertRole.executeUpdate();
                                }
                                
                                conn.commit();
                                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                                loadEmployees(model);
                                
                            } catch (NumberFormatException e) {
                                conn.rollback();
                                JOptionPane.showMessageDialog(this, "Invalid salary amount.");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
    
    private void deleteEmployee(int employeeId, String employeeName, DefaultTableModel model) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete employee:\n" + employeeName + " (ID: " + employeeId + ")?\n\n" +
            "WARNING: This will permanently delete the employee and all related records!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Delete from Users table - CASCADE will handle Employee, Employee_Role
                String deleteSQL = "DELETE FROM Users WHERE UserID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
                    pstmt.setInt(1, employeeId);
                    int rows = pstmt.executeUpdate();
                    
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                        loadEmployees(model);
                    } else {
                        JOptionPane.showMessageDialog(this, "Employee not found.");
                    }
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("foreign key constraint")) {
                    JOptionPane.showMessageDialog(this, 
                        "Cannot delete employee: They are referenced by other records.\n" +
                        "This employee may be a supervisor or assigned to orders.",
                        "Delete Failed", 
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage());
                }
            }
        }
    }
    
    private void loadRecentPayments(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT * FROM Payment ORDER BY Date DESC LIMIT 10";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("PaymentID"),
                    rs.getInt("OrderID"),
                    rs.getDouble("Amount"),
                    rs.getString("Method"),
                    rs.getTimestamp("Date")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            System.err.println("Error loading payments: " + e.getMessage());
        }
    }
    
    // Action Methods
    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order.");
            return;
        }
        
        int orderID = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
        
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
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            return;
        }
        
        JOptionPane.showMessageDialog(this, details.toString(), "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateOrderStatus() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order.");
            return;
        }
        
        int orderID = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) ordersTableModel.getValueAt(selectedRow, 4);
        
        String[] statuses = {"Pending", "Preparing", "Served", "Completed"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new status for Order #" + orderID,
            "Update Order Status",
            JOptionPane.QUESTION_MESSAGE,
            null, statuses, currentStatus);
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            String sql = "UPDATE `Order` SET Status = ?, EmployeeID = ? WHERE OrderID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, currentUser.getUserID());
                pstmt.setInt(3, orderID);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Order status updated!");
                loadOrders();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void notifyKitchen() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order.");
            return;
        }
        
        int orderID = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this, 
            "Kitchen has been notified about Order #" + orderID + "!",
            "Kitchen Notification",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean createNewOrder(int customerId, int[] itemIds, int[] quantities, String specialRequest) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            int nextOrderID = 1;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(OrderID) FROM `Order`")) {
                if (rs.next()) {
                    nextOrderID = rs.getInt(1) + 1;
                }
            }
            
            double totalAmount = 0;
            for (int i = 0; i < itemIds.length; i++) {
                String priceSQL = "SELECT Price FROM MenuItem WHERE ItemID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(priceSQL)) {
                    pstmt.setInt(1, itemIds[i]);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            totalAmount += rs.getDouble("Price") * quantities[i];
                        }
                    }
                }
            }
            
            String insertOrderSQL = "INSERT INTO `Order` (OrderID, CustomerID, EmployeeID, Status, TotalAmount, OrderDate) " +
                                   "VALUES (?, ?, ?, 'Pending', ?, NOW())";
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSQL)) {
                pstmt.setInt(1, nextOrderID);
                pstmt.setInt(2, customerId);
                pstmt.setInt(3, currentUser.getUserID());
                pstmt.setDouble(4, totalAmount);
                pstmt.executeUpdate();
            }
            
            String insertDetailsSQL = "INSERT INTO OrderDetails (OrderID, ItemID, Quantity, SpecialRequest) VALUES (?, ?, ?, ?)";
            for (int i = 0; i < itemIds.length; i++) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertDetailsSQL)) {
                    pstmt.setInt(1, nextOrderID);
                    pstmt.setInt(2, itemIds[i]);
                    pstmt.setInt(3, quantities[i]);
                    pstmt.setString(4, i == 0 ? specialRequest : null);
                    pstmt.executeUpdate();
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error creating order: " + e.getMessage());
            return false;
        }
    }
    
    private void registerPayment() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order.");
            return;
        }
        
        int orderID = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
        double totalAmount = (Double) ordersTableModel.getValueAt(selectedRow, 3);
        
        String checkSQL = "SELECT COUNT(*) FROM Payment WHERE OrderID = ? AND Method NOT LIKE 'Tip%'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSQL)) {
            
            pstmt.setInt(1, orderID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Order " + orderID + " already has a payment recorded.",
                        "Duplicate Payment", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking existing payment: " + e.getMessage());
        }
        
        String[] paymentMethods = {"Cash", "Credit Card", "Mada", "Apple Pay", "STC Pay"};
        String selectedMethod = (String) JOptionPane.showInputDialog(
            this,
            "Order #" + orderID + "\nTotal Amount: " + totalAmount + " SAR\n\nSelect Payment Method:",
            "Register Payment",
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentMethods,
            paymentMethods[0]
        );
        
        if (selectedMethod == null) {
            return;
        }
        
        if (processPayment(orderID, totalAmount, selectedMethod)) {
            JOptionPane.showMessageDialog(this, "Payment registered successfully!");
            loadOrders();
        }
    }
    
    private void updateTableStatus() {
        int selectedRow = tablesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a table.");
            return;
        }
        
        int tableNumber = (Integer) tablesTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tablesTableModel.getValueAt(selectedRow, 4);
        
        String[] statuses = {"Available", "Occupied", "Reserved", "Maintenance"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new status for Table #" + tableNumber,
            "Update Table Status",
            JOptionPane.QUESTION_MESSAGE,
            null, statuses, currentStatus);
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            String sql = "UPDATE Restaurant_Table SET Status = ? WHERE TableNumber = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, tableNumber);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Table status updated!");
                loadTables();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void confirmReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation.");
            return;
        }
        
        int reservationID = (Integer) reservationsTableModel.getValueAt(selectedRow, 0);
        int tableNumber = (Integer) reservationsTableModel.getValueAt(selectedRow, 2);
        
        String sql = "UPDATE Reservation SET Status = 'Confirmed' WHERE ReservationID = ?";
        String tableSQL = "UPDATE Restaurant_Table SET Status = 'Reserved' WHERE TableNumber = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, reservationID);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(tableSQL)) {
                pstmt.setInt(1, tableNumber);
                pstmt.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Reservation confirmed!");
            loadReservations();
            loadTables();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private double lookupOrderTotal(int orderID, String paymentMethod) {
        String checkPaymentSQL = "SELECT Amount FROM Payment WHERE OrderID = ? AND Method = ? AND Method NOT LIKE 'Tip%'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkPaymentSQL)) {
            
            pstmt.setInt(1, orderID);
            pstmt.setString(2, paymentMethod);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("Amount");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking payment: " + e.getMessage());
        }
        
        String sql = "SELECT TotalAmount FROM `Order` WHERE OrderID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TotalAmount");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0;
    }
    
    private boolean processPayment(int orderID, double amount, String method) {
        if (method.toLowerCase().contains("tip")) {
            JOptionPane.showMessageDialog(this, 
                "Invalid payment method. Tip payments are not allowed through this interface.",
                "Invalid Payment Method", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String checkOrderSQL = "SELECT COUNT(*) FROM `Order` WHERE OrderID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkOrderSQL)) {
            
            pstmt.setInt(1, orderID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Order ID " + orderID + " does not exist.\nPlease verify the order number.",
                        "Order Not Found", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking order: " + e.getMessage());
            return false;
        }
        
        String checkSQL = "SELECT COUNT(*) FROM Payment WHERE OrderID = ? AND Method NOT LIKE 'Tip%'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSQL)) {
            
            pstmt.setInt(1, orderID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Order " + orderID + " already has a payment recorded.",
                        "Duplicate Payment", 
                        JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking existing payment: " + e.getMessage());
        }
        
        String insertSQL = "INSERT INTO Payment (PaymentID, OrderID, Amount, Method, Date) VALUES (?, ?, ?, ?, NOW())";
        String updateSQL = "UPDATE `Order` SET Status = 'Completed' WHERE OrderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            int nextID = 1;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(PaymentID) FROM Payment")) {
                if (rs.next()) {
                    nextID = rs.getInt(1) + 1;
                }
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setInt(1, nextID);
                pstmt.setInt(2, orderID);
                pstmt.setDouble(3, amount);
                pstmt.setString(4, method);
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setInt(1, orderID);
                pstmt.executeUpdate();
            }
            
            loadOrders();
            
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    
    private void setFrameProperties() {
        setTitle("Staff Dashboard - RHA Restaurant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ordersButton) {
            loadOrders();
            cardLayout.show(contentPanel, "ORDERS");
        } else if (e.getSource() == registerOrderButton) {
            cardLayout.show(contentPanel, "REGISTER_ORDER");
        } else if (e.getSource() == tablesButton) {
            loadTables();
            cardLayout.show(contentPanel, "TABLES");
        } else if (e.getSource() == reservationsButton) {
            loadReservations();
            cardLayout.show(contentPanel, "RESERVATIONS");
        } else if (e.getSource() == billingButton) {
            cardLayout.show(contentPanel, "BILLING");
        } else if (e.getSource() == menuButton) {
            loadMenu();
            cardLayout.show(contentPanel, "MENU");
        } else if (e.getSource() == manageEmployeesButton) {
            cardLayout.show(contentPanel, "MANAGE_EMPLOYEES");
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