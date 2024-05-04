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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection connection;

    public Database() {
        connectDatabase();
    }

    private void connectDatabase() {
        String url = "jdbc:mysql://localhost:3306/atm_database";
        String user = "atm_user";
        String password = "root";
        try {
            connection = DriverManager.getConnection(url, user, password);
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
                System.out.println("User not found in database");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching balance: " + e.getMessage());
        }
        return balance;
    }

    public void updateBalanceDatabase(int userId, double newBalance) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE users SET balance = ? WHERE id = ?");
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public void addHistoryTransaction(int userId, String type, double amount, LocalDateTime timestamp) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO transactions (user_id, type, amount, timestamp) VALUES (?, ?, ?, ?)");
            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setObject(4, timestamp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding transaction to database: " + e.getMessage());
        }
    }


    public boolean authenticateUser(String accountNumber, int pin) {
        try {
            String query = "SELECT pin FROM users WHERE account_number = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, accountNumber);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int storedPin = rs.getInt("pin");
                    return storedPin == pin;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
            return false;
        }
    }

    public int getUserIdByAccountNumber(String accountNumber) {
        int userId = -1;
        try {
            String query = "SELECT id FROM users WHERE account_number = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, accountNumber);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user ID: " + e.getMessage());
        }
        return userId;
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
            throw e;
        }
        return name;
    }

    public void closeConnection() {
    try {
        if (connection != null) {
            connection.close();
        }
    } catch (SQLException e) {
        System.out.println("Error closing database connection: " + e.getMessage());
    }
}


    public List<Transaction> getTransactionHistory(int userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM transactions WHERE user_id = ?");
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                LocalDateTime timestamp = rs.getObject("timestamp", LocalDateTime.class);
                transactions.add(new Transaction(type, amount, timestamp));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transaction history: " + e.getMessage());
            throw e; // Re-throw the exception
        }
        return transactions;
    }
}
