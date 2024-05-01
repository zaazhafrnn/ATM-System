/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atmsystem;

/**
 *
 * @author aza
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private Connection connection;

    public Database(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }
    
    public Connection getConnection() {
        return connection;
    }

    public double getBalance(int userId) {
        double balance = 0.0;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT balance FROM users WHERE id = ?");
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                balance = rs.getDouble("balance");
            } else {
                System.out.println("User not found in the database.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching balance: " + e.getMessage());
        }
        return balance;
    }
    
    public String getUserName(int userId) throws SQLException {
        String name = "";
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT name FROM users WHERE id = ?");
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            } else {
                System.out.println("User not found in the database.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user name: " + e.getMessage());
            throw e; // Re-throw the exception
        }
        return name;
    }

    public void updateBalance(int userId, double newBalance) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE users SET balance = ? WHERE id = ?");
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            System.out.println("Balance updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }
}
