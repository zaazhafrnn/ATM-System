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

    public void addHistoryTransaction(int transactionId, int userId, String type, double amount, LocalDateTime timestamp) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO transactions (transaction_id, user_id, type, amount, timestamp) VALUES (?, ?, ?, ?, ?)");
            pstmt.setInt(1, transactionId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, type);
            pstmt.setDouble(4, amount);
            pstmt.setObject(5, timestamp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding transaction to database: " + e.getMessage());
        }
    }


    public boolean authenticateUser(String accountNumber, int pin) {
        try {
            if (accountNumber.equals("0000") && pin == 0000) {
                return true;
            } else {
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
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
            return false;
        }
    }
    
    public User getUserById(int userId) {
        User user = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String accountNumber = rs.getString("account_number");
                double balance = rs.getDouble("balance");
                user = new User(id, name, accountNumber, balance);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        return user;
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
    
    public int getTransactionId() {
        int lastTransactionId = 0;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT MAX(transaction_id) AS last_id FROM transactions");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                lastTransactionId = rs.getInt("last_id");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching last transaction ID: " + e.getMessage());
        }
        return lastTransactionId;
    }
    
    public int generateTransactionId() {
        int lastTransactionId = getTransactionId();
        int newTransactionId = lastTransactionId + 1;
        return newTransactionId;
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
                int transactionId = rs.getInt("transaction_id");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                LocalDateTime timestamp = rs.getObject("timestamp", LocalDateTime.class);
                transactions.add(new Transaction(transactionId, type, amount, timestamp));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transaction history: " + e.getMessage());
            throw e;
        }
        return transactions;
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("name");
                String accountNumber = rs.getString("account_number");
                int pin = rs.getInt("pin");
                User user = new User(userId, name, accountNumber, pin);
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all users: " + e.getMessage());
        }
        return users;
    }

    boolean addNewUser(String name, String accountNumber, int pin, double balance) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO users (name, account_number, pin, balance) VALUES (?, ?, ?, ?)");
            pstmt.setString(1, name);
            pstmt.setString(2, accountNumber);
            pstmt.setInt(3, pin);
            pstmt.setDouble(4, balance);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding new user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    
}
