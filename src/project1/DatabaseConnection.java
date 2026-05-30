package project1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection utility class
 * Handles MySQL database connection for the Restaurant Management System
 */
public class DatabaseConnection {
    
    // Database configuration
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/project1";
    private static final String USERNAME = ""; //your password from mysql 
    private static final String PASSWORD = ""; //your password from mysql 
    
    /**
     * Gets a connection to the database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }
    
    /**
     * Tests the database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}

 