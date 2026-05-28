package project1;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Application Entry Point
 * RHA Restaurant Management System
 */
public class project1 {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("Nimbus not available, using default Look and Feel");
        }
        
        // Test database connection
        System.out.println("==============================================");
        System.out.println("   RHA Restaurant Management System v1.0     ");
        System.out.println("==============================================");
        System.out.println("Testing database connection...");
        
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null, 
                "Cannot connect to database.\n\n" +
                "Please ensure:\n" +
                "1. MySQL server is running\n" +
                "2. Database 'project1' exists\n" +
                "3. Username and password are correct in DatabaseConnection.java\n\n" +
                "The application will still launch, but functionality will be limited.",
                "RHA - Database Connection Warning",
                JOptionPane.WARNING_MESSAGE);
        } else {
            System.out.println("Database connection successful!");
        }
        
        System.out.println("Launching RHA Restaurant Management System...");
        
        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}