package project1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User model class representing a user in the system
 */
public class User {
    
    private int userID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String userType; // "Customer" or "Employee"
    
    // Additional fields for Employee
    private int restaurantID;
    private double salary;
    private Integer supervisorID;
    private String role;
    
    public User() {
    }
    
    public User(int userID, String firstName, String middleName, String lastName, 
                String username, String email, String phoneNumber, String userType) {
        this.userID = userID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public int getRestaurantID() {
        return restaurantID;
    }
    
    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    public Integer getSupervisorID() {
        return supervisorID;
    }
    
    public void setSupervisorID(Integer supervisorID) {
        this.supervisorID = supervisorID;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getFullName() {
        if (middleName != null && !middleName.isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
    
    /**
     * Check if user is an Admin (General Manager only)
     */
    public boolean isAdmin() {
        return role != null && (role.equalsIgnoreCase("General Manager"));
    }
    
    /**
     * Check if user is a Manager (General Manager and Floor Manager only)
     */
    
    public boolean isMangaer() {
        return role != null && (role.equalsIgnoreCase("General Manager")|| role.equalsIgnoreCase("Floor Manager") );
    }
    
    /**
     * Check if user is a Staff member (Waiter, Cashier, etc.)
     */
    public boolean isStaff() {
        return userType != null && userType.equalsIgnoreCase("Employee") && !isMangaer();
    }
    
    /**
     * Check if user is a Customer
     */
    public boolean isCustomer() {
        return userType != null && userType.equalsIgnoreCase("Customer");
    }
    
    /**
     * Check if user is a supervisor (has subordinates)
     * This checks the database to see if any employees have this user as their supervisor
     */
    public boolean isSupervisor() {
        if (!userType.equalsIgnoreCase("Employee")) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM Employee WHERE SupervisorID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, this.userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Returns true if they supervise at least one person
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking supervisor status: " + e.getMessage());
        }
        return false;
    }
}