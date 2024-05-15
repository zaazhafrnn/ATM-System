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

    public double getBalance(int accountId) {
        double saldo = 0.0;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT saldo FROM rekening WHERE id = ?");
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                saldo = rs.getDouble("saldo");
            } else {
                System.out.println("Balance not found in database");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching balance: " + e.getMessage());
        }
        return saldo;
    }

    public void updateBalanceDatabase(int accountId, double newBalance) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE rekening SET saldo = ? WHERE id = ?");
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public void addTransaction(Transaksi transaction) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO transaksi (transaksi_id, no_rekening, tipe_transaksi, jumlah, deskripsi, tanggal) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, transaction.getAccountId());
            pstmt.setInt(2, transaction.getTransactionId());
            pstmt.setString(3, transaction.getTransactionType());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getDescription());
            pstmt.setObject(6, transaction.getTimestamp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding transaction to database: " + e.getMessage());
        }
    }

    public int authenticateRekening(String accountNumber, int pin) {
        try {
            if (accountNumber.equals("0000") && pin == 0000) {
                return 0;
            } else {
                String query = "SELECT id FROM rekening WHERE no_rekening = ? AND pin = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, accountNumber);
                    pstmt.setInt(2, pin);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("id");
                    } else {
                        return -1;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating account: " + e.getMessage());
            return -1;
        }
    }

    public int getUserIdByAccountNumber(String accountNumber) {
        int userId = -1;
        try {
            String query = "SELECT id FROM rekening WHERE no_rekening = ?";
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
            PreparedStatement pstmt = connection.prepareStatement("SELECT nama_depan, nama_belakang FROM users WHERE id = (SELECT user_id FROM rekening WHERE id = ?)");
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String firstName = rs.getString("nama_depan");
                String lastName = rs.getString("nama_belakang");
                name = firstName + " " + lastName;
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
            PreparedStatement pstmt = connection.prepareStatement("SELECT MAX(transaksi_id) AS last_id FROM transaksi");
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

    public List<Transaksi> getTransactionHistory(int accountId) throws SQLException {
        List<Transaksi> transactions = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM transaksi WHERE no_rekening = ?");
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int transactionId = rs.getInt("transaksi_id");
                String transactionType = rs.getString("tipe_transaksi");
                double amount = rs.getDouble("jumlah");
                String description = rs.getString("deskripsi");
                LocalDateTime timestamp = rs.getObject("tanggal", LocalDateTime.class);
//                transactions.add(new Transaction(transactionId, type, amount, timestamp));
                Transaksi transaction = new Transaksi(accountId, transactionId, transactionType, amount, description, timestamp);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transaction history: " + e.getMessage());
            throw e;
        }
        return transactions;
    }
    
    
//    ADMIN FUNCTION
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("id");
                int nik = rs.getInt("nik");
                String firstName = rs.getString("nama_depan");
                String lastName = rs.getString("nama_belakang");
                String address = rs.getString("alamat");
                int phone = rs.getInt("no_telp");
                User user = new User(userId, nik, firstName, lastName, address, phone);
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
    
//    public boolean updateUser(User user) {
//        try {
//            PreparedStatement pstmt = connection.prepareStatement("UPDATE users SET name=?, account_number=?, pin=? WHERE id=?");
//            pstmt.setString(1, user.getName());
//            pstmt.setString(2, user.getAccountNumber());
//            pstmt.setInt(3, user.getPin());
//            pstmt.setInt(4, user.getId());
//            pstmt.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            System.out.println("Error updating user: " + e.getMessage());
//            return false;
//        }
//    }

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

    
//        CLOSE CONNECTION
    
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }
}
