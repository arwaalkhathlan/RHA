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

/**
 * Sign Up Frame for RHA Customer Registration
 */
public class SignUpFrame extends JFrame implements ActionListener {
    
    // RHA Brand Colors
    private static final Color RHA_DARK = new Color(0x232326);      // #232326 - Dark background
    private static final Color RHA_LIGHT = new Color(0xEDEDED);     // #EDEDED - Light background
    private static final Color RHA_YELLOW = new Color(0xFCC660);    // #FCC660 - Primary accent
    private static final Color RHA_ORANGE = new Color(0xEB2F00);    // #EB2F00 - Secondary accent
    private static final Color TEXT_DARK = new Color(0x232326);     // Dark text
    private static final Color TEXT_LIGHT = new Color(0xEDEDED);    // Light text
    
    // Components
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JTextField lastNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signUpButton;
    private JButton cancelButton;
    private JLabel backToLoginLabel;
    private JLabel logoLabel;
    
    // Reference to login frame
    private LoginFrame loginFrame;
    
    // Define sizes for consistency
    private static final Dimension FIELD_SIZE = new Dimension(200, 40);
    
    // Constructor
    public SignUpFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        initComponents();
        setFrameProperties();
    }
    
    /**
     * Initialize GUI components
     */
    private void initComponents() {
        // Main panel: BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 25));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        mainPanel.setBackground(RHA_LIGHT);
        
        // Header Panel (Logo + Title)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(RHA_LIGHT);
        
        // Title first
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 24));
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
                Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_AREA_AVERAGING);
                logoIcon = new ImageIcon(scaledImg);
                logoLabel.setIcon(logoIcon);
            } else {
                // Image file not found, use text fallback
                logoLabel.setText("RHA");
                logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
                logoLabel.setForeground(RHA_YELLOW);
            }
        } catch (Exception e) {
            // Fallback to text logo if image not found
            logoLabel.setText("RHA");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
            logoLabel.setForeground(RHA_YELLOW);
        }
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Join RHA Restaurant Management");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(12));
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);
        
        // Form Panel (CENTER) - Using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(RHA_LIGHT);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Row 0: First Name & Middle Name
        firstNameField = createTextField();
        middleNameField = createTextField();
        
        addLabelFieldPair(formPanel, createLabel("First Name:*"), firstNameField, gbc, 0, row);
        addLabelFieldPair(formPanel, createLabel("Middle Name:"), middleNameField, gbc, 2, row++);

        // Row 1: Last Name & Username
        lastNameField = createTextField();
        usernameField = createTextField();
        
        addLabelFieldPair(formPanel, createLabel("Last Name:*"), lastNameField, gbc, 0, row);
        addLabelFieldPair(formPanel, createLabel("Username:*"), usernameField, gbc, 2, row++);
        
        // Row 2: Email & Phone
        emailField = createTextField();
        phoneField = createTextField();
        
        addLabelFieldPair(formPanel, createLabel("Email:*"), emailField, gbc, 0, row);
        addLabelFieldPair(formPanel, createLabel("Phone:"), phoneField, gbc, 2, row++);
        
        // Row 3: Password & Confirm Password
        passwordField = createPasswordField();
        confirmPasswordField = createPasswordField();
        
        addLabelFieldPair(formPanel, createLabel("Password:*"), passwordField, gbc, 0, row);
        addLabelFieldPair(formPanel, createLabel("Confirm Password:*"), confirmPasswordField, gbc, 2, row++);
        
        // Row 4: Required fields note
        JLabel noteLabel = new JLabel("* Required fields");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        noteLabel.setForeground(new Color(100, 100, 100));
        
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 12, 5, 12);
        formPanel.add(noteLabel, gbc);
        
        // Bottom Panel (SOUTH)
        JPanel bottomAreaPanel = new JPanel(new BorderLayout(0, 12)); 
        bottomAreaPanel.setBackground(RHA_LIGHT);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(RHA_LIGHT);
        
        signUpButton = new JButton("Create Account");
        signUpButton.setPreferredSize(new Dimension(180, 50));
        signUpButton.setBackground(RHA_YELLOW);
        signUpButton.setForeground(RHA_DARK);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 15));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpButton.addActionListener(this);
        
        // Hover effect for sign up button
        signUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signUpButton.setBackground(RHA_ORANGE);
                signUpButton.setForeground(TEXT_LIGHT);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signUpButton.setBackground(RHA_YELLOW);
                signUpButton.setForeground(RHA_DARK);
            }
        });
        
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(180, 50));
        cancelButton.setBackground(RHA_DARK);
        cancelButton.setForeground(TEXT_LIGHT);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 15));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(this);
        
        // Hover effect for cancel button
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(new Color(50, 50, 53));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(RHA_DARK);
            }
        });
        
        buttonPanel.add(signUpButton);
        buttonPanel.add(cancelButton);
        bottomAreaPanel.add(buttonPanel, BorderLayout.NORTH); 
        
        // Back to Login Panel
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setBackground(RHA_LIGHT);
        
        JLabel haveAccountLabel = new JLabel("Already have an account? ");
        haveAccountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        haveAccountLabel.setForeground(RHA_DARK);
        
        backToLoginLabel = new JLabel("Sign In");
        backToLoginLabel.setFont(new Font("Arial", Font.BOLD, 13));
        backToLoginLabel.setForeground(RHA_ORANGE);
        backToLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        backToLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { 
                goBackToLogin(); 
            }
            
            @Override
            public void mouseEntered(MouseEvent e) { 
                backToLoginLabel.setForeground(RHA_YELLOW); 
            }
            
            @Override
            public void mouseExited(MouseEvent e) { 
                backToLoginLabel.setForeground(RHA_ORANGE); 
            }
        });
        
        backPanel.add(haveAccountLabel);
        backPanel.add(backToLoginLabel);
        bottomAreaPanel.add(backPanel, BorderLayout.CENTER); 
        
        // Add all sections to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(bottomAreaPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Helper method to add a label and field pair using GridBagConstraints
     */
    private void addLabelFieldPair(JPanel panel, JLabel label, JTextField field, GridBagConstraints gbc, int col, int row) {
        // Label placement
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 8, 8, 8);
        panel.add(label, gbc);
        
        // Field placement
        gbc.gridx = col + 1;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        panel.add(field, gbc);
        gbc.weightx = 0; 
    }

    /**
     * Helper method to create labels
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setForeground(RHA_DARK);
        return label;
    }
    
    /**
     * Helper method to create standard text fields
     */
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(FIELD_SIZE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(RHA_DARK);
        return field;
    }
    
    /**
     * Helper method to create password fields
     */
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(FIELD_SIZE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(RHA_DARK, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(RHA_DARK);
        return field;
    }
    
    /**
     * Set frame properties
     */
    private void setFrameProperties() {
        setTitle("RHA - Create Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack(); 
        setMinimumSize(new Dimension(850, 700)); 
        setSize(new Dimension(850, 700));
        setLocationRelativeTo(null);
        setResizable(true); 
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signUpButton) {
            performSignUp();
        } else if (e.getSource() == cancelButton) {
            goBackToLogin();
        }
    }
    
    private void performSignUp() {
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || 
            email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (UserDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.", "Registration Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        if (UserDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already registered. Please use another email.", "Registration Error", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return;
        }
        
        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setMiddleName(middleName.isEmpty() ? null : middleName);
        newUser.setLastName(lastName);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPhoneNumber(phone.isEmpty() ? null : phone);
        newUser.setPassword(password);
        newUser.setUserType("Customer");
        
        if (UserDAO.registerCustomer(newUser)) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful! You can now login.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            goBackToLogin();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration failed due to a database error. Check console for details.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    
    private void goBackToLogin() {
        if (loginFrame != null) {
            loginFrame.setVisible(true);
        }
        this.dispose();
    }
}