package project1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Login Frame for RHA Restaurant Management System
 */
public class LoginFrame extends JFrame implements ActionListener {
    
    // RHA Brand Colors
    private static final Color RHA_DARK = new Color(0x232326);      // #232326 - Dark background
    private static final Color RHA_LIGHT = new Color(0xEDEDED);     // #EDEDED - Light background
    private static final Color RHA_YELLOW = new Color(0xFCC660);    // #FCC660 - Primary accent
    private static final Color RHA_ORANGE = new Color(0xEB2F00);    // #EB2F00 - Secondary accent
    private static final Color TEXT_DARK = new Color(0x232326);     // Dark text
    private static final Color TEXT_LIGHT = new Color(0xEDEDED);    // Light text
    
    // Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel signUpLabel;
    private JLabel logoLabel;
    
    // Constructor
    public LoginFrame() {
        initComponents();
        setFrameProperties();
    }
    
    /**
     * Initialize GUI components
     */
    private void initComponents() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        mainPanel.setBackground(RHA_LIGHT);
        
        // Logo and Title Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(RHA_LIGHT);
        
        // Title label first
        JLabel titleLabel = new JLabel("Restaurant Management");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        titleLabel.setForeground(RHA_DARK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        // RHA Logo Image
        logoLabel = new JLabel();
        try {
            // Try PNG first, then JPG
            ImageIcon logoIcon = null;
            if (new java.io.File("RHALogo.png").exists()) {
                logoIcon = new ImageIcon("RHALogo.png");
            } else if (new java.io.File("RHALogo.jpg").exists()) {
                logoIcon = new ImageIcon("RHALogo.jpg");
            }
            
            // Check if image was loaded
            if (logoIcon != null && logoIcon.getIconWidth() > 0) {
             
                Image img = logoIcon.getImage();
                Image scaledImg = img.getScaledInstance(120, 120, Image.SCALE_AREA_AVERAGING);
                logoIcon = new ImageIcon(scaledImg);
                logoLabel.setIcon(logoIcon);
            } else {
                // Image file not found, use text fallback
                logoLabel.setText("RHA");
                logoLabel.setFont(new Font("Arial", Font.BOLD, 56));
                logoLabel.setForeground(RHA_YELLOW);
            }
        } catch (Exception e) {
            // Fallback to text logo if image not found
            logoLabel.setText("RHA");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 56));
            logoLabel.setForeground(RHA_YELLOW);
        }
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);
        
        // Form Panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(RHA_LIGHT);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username Label
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setForeground(RHA_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameLabel, gbc);
        
        // Username TextField
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 15));
        usernameField.setPreferredSize(new Dimension(350, 45));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        usernameField.setBackground(Color.WHITE);
        usernameField.setForeground(RHA_DARK);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(usernameField, gbc);
        
        // Password Label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(RHA_DARK);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordLabel, gbc);
        
        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));
        passwordField.setPreferredSize(new Dimension(350, 45));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(RHA_DARK);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(passwordField, gbc);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setBackground(RHA_LIGHT);
        
        loginButton = new JButton("Sign In");
        loginButton.setPreferredSize(new Dimension(350, 50));
        loginButton.setBackground(RHA_YELLOW);
        loginButton.setForeground(RHA_DARK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(this);
        
        // Hover effect for login button
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(RHA_ORANGE);
                loginButton.setForeground(TEXT_LIGHT);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(RHA_YELLOW);
                loginButton.setForeground(RHA_DARK);
            }
        });
        
        buttonPanel.add(loginButton);
        
        // Sign Up Panel
        JPanel signUpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        signUpPanel.setBackground(RHA_LIGHT);
        
        JLabel noAccountLabel = new JLabel("Don't have an account? ");
        noAccountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        noAccountLabel.setForeground(RHA_DARK);
        
        signUpLabel = new JLabel("Sign Up");
        signUpLabel.setFont(new Font("Arial", Font.BOLD, 13));
        signUpLabel.setForeground(RHA_ORANGE);
        signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Mouse listener for Sign Up label
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openSignUpFrame();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                signUpLabel.setForeground(RHA_YELLOW);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signUpLabel.setForeground(RHA_ORANGE);
            }
        });
        
        signUpPanel.add(noAccountLabel);
        signUpPanel.add(signUpLabel);
        
        // Bottom Panel combining button and sign up
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBackground(RHA_LIGHT);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(signUpPanel, BorderLayout.SOUTH);
        
        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add action listener for Enter key
        passwordField.addActionListener(this);
    }
    
    /**
     * Set frame properties
     */
    private void setFrameProperties() {
        setTitle("RHA - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(550, 600));
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton || e.getSource() == passwordField) {
            performLogin();
        }
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User user = UserDAO.authenticate(username, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(this, 
                "Welcome, " + user.getFullName() + "!", 
                "Login Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            openDashboard(user);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password.", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
    
    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            String userType = user.getUserType().toUpperCase();
            if (userType.equals("CUSTOMER")) {
                new CustomerDashboard(user).setVisible(true);
            } else if (userType.equals("EMPLOYEE")) {
                new StaffDashboard(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Unknown user type: " + userType, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void openSignUpFrame() {
        SignUpFrame signUpFrame = new SignUpFrame(this);
        signUpFrame.setVisible(true);
        this.setVisible(false);
    }
}