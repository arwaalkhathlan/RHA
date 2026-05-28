package project1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for User operations
 */
public class UserDAO {
    
    /**
     * Authenticate user with username and password
     * @param username the username
     * @param password the password
     * @return User object if authentication successful, null otherwise
     */
    public static User authenticate(String username, String password) {
        String sql = "SELECT u.*, e.RestaurantID, e.Salary, e.SupervisorID, er.Role " +
                     "FROM Users u " +
                     "LEFT JOIN Employee e ON u.UserID = e.EmployeeID " +
                     "LEFT JOIN Employee_Role er ON e.EmployeeID = er.EmployeeID " +
                     "WHERE u.Username = ? AND u.Password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("UserID"));
                    user.setFirstName(rs.getString("FName"));
                    user.setMiddleName(rs.getString("MName"));
                    user.setLastName(rs.getString("LName"));
                    user.setUsername(rs.getString("Username"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhoneNumber(rs.getString("PhoneNumber"));
                    user.setPassword(rs.getString("Password"));
                    user.setUserType(rs.getString("UserType"));
                    
                    // Set employee-specific fields if applicable
                    if (user.getUserType().equalsIgnoreCase("Employee")) {
                        user.setRestaurantID(rs.getInt("RestaurantID"));
                        user.setSalary(rs.getDouble("Salary"));
                        int supervisorID = rs.getInt("SupervisorID");
                        if (!rs.wasNull()) {
                            user.setSupervisorID(supervisorID);
                        }
                        user.setRole(rs.getString("Role"));
                    }
                    
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Register a new customer
     * @param user the User object with customer details
     * @return true if registration successful, false otherwise
     */
    public static boolean registerCustomer(User user) {
        String insertUserSQL = "INSERT INTO Users (UserID, FName, MName, LName, Username, Email, PhoneNumber, Password, UserType) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Customer')";
        String insertCustomerSQL = "INSERT INTO Customer (CustomerID) VALUES (?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get next available UserID
            int nextUserID = getNextUserID(conn);
            user.setUserID(nextUserID);
            
            // Insert into Users table
            try (PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {
                pstmt.setInt(1, nextUserID);
                pstmt.setString(2, user.getFirstName());
                pstmt.setString(3, user.getMiddleName());
                pstmt.setString(4, user.getLastName());
                pstmt.setString(5, user.getUsername());
                pstmt.setString(6, user.getEmail());
                pstmt.setString(7, user.getPhoneNumber());
                pstmt.setString(8, user.getPassword());
                pstmt.executeUpdate();
            }
            
            // Insert into Customer table
            try (PreparedStatement pstmt = conn.prepareStatement(insertCustomerSQL)) {
                pstmt.setInt(1, nextUserID);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback error: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Connection close error: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Check if username already exists
     */
    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Username check error: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Check if email already exists
     */
    public static boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Email check error: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get next available UserID
     */
    private static int getNextUserID(Connection conn) throws SQLException {
        String sql = "SELECT MAX(UserID) FROM Users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        }
        return 1;
    }
    
    /**
     * Update user profile
     */
    public static boolean updateUser(User user) {
        String sql = "UPDATE Users SET FName = ?, MName = ?, LName = ?, " +
                     "Email = ?, PhoneNumber = ? WHERE UserID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getMiddleName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setInt(6, user.getUserID());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update user error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Change user password
     */
    public static boolean changePassword(int userID, String oldPassword, String newPassword) {
        String checkSQL = "SELECT COUNT(*) FROM Users WHERE UserID = ? AND Password = ?";
        String updateSQL = "UPDATE Users SET Password = ? WHERE UserID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verify old password
            try (PreparedStatement pstmt = conn.prepareStatement(checkSQL)) {
                pstmt.setInt(1, userID);
                pstmt.setString(2, oldPassword);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        return false; // Old password doesn't match
                    }
                }
            }
            
            // Update to new password
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setString(1, newPassword);
                pstmt.setInt(2, userID);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Change password error: " + e.getMessage());
            return false;
        }
    }
}