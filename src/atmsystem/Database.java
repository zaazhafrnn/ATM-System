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
import java.sql.*;

public class Database {
    private Connection connection;

    public Database() {
        connectDatabase();
    }

    private void connectDatabase() {
        String url = "jdbc:mysql://localhost:3306/atm_database";
        String user = "user-baru";
        String password = "password";
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
        String query = "SELECT * FROM users"; 

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int nik = rs.getInt("nik");
                String firstName = rs.getString("nama_depan");
                String lastName = rs.getString("nama_belakang");
                String address = rs.getString("alamat");
                String phone = rs.getString("no_telp");
                String password = rs.getString("password");

                User user = new User(id, nik, firstName, lastName, address, phone, password);
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    public boolean addNewUser(int nik, String firstName, String lastName, String address, String phone, String password) {
        if (isNikExists(nik)) {
            System.out.println("\nNIK sudah ada. Gagal menambahkan user.");
            return false;
        }

        String query = "INSERT INTO users (nik, nama_depan, nama_belakang, alamat, no_telp, password) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, nik);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, address);
            pstmt.setString(5, phone);
            pstmt.setString(6, password);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error adding new user: " + e.getMessage());
            return false;
        }
    }
    
    public void updateUser(int id, int nik, String firstName, String lastName, String address, String phone, String password) {
        String query = "UPDATE users SET nama_depan = ?, nama_belakang = ?, alamat = ?, no_telp = ?, password = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//            pstmt.setInt(1, nik);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, address);
            pstmt.setString(4, phone);
            pstmt.setString(5, password);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }
    
    public boolean isNikExists(int nik) {
        String query = "SELECT COUNT(*) FROM users WHERE nik = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, nik);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking NIK: " + e.getMessage());
        }
        return false;
    }
    
    public User getUserByNik(int nik) {
        String query = "SELECT * FROM users WHERE nik = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, nik);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String firstName = rs.getString("nama_depan");
                    String lastName = rs.getString("nama_belakang");
                    String address = rs.getString("alamat");
                    String phone = rs.getString("no_telp");
                    String password = rs.getString("password");

                    return new User(id, nik, firstName, lastName, address, phone, password);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteUserByNik(int nik) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM users WHERE nik = ?");
            pstmt.setInt(1, nik);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    public List<Rekening> getAllRekenings() {
        List<Rekening> rekenings = new ArrayList<>();
        String query = "SELECT r.id, r.user_id, r.no_rekening, r.pin, r.saldo, u.nama_depan, u.nama_belakang " +
                       "FROM rekening r JOIN users u ON r.user_id = u.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String accountNumber = rs.getString("no_rekening");
                int pin = rs.getInt("pin");
                double balance = rs.getDouble("saldo");
                String firstName = rs.getString("nama_depan");
                String lastName = rs.getString("nama_belakang");

                Rekening rekening = new Rekening(id, userId, accountNumber, pin, balance, firstName, lastName);
                rekenings.add(rekening);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving rekenings: " + e.getMessage());
        }
        return rekenings;
    }
    
    public String getUserNameRekening(int userId) throws SQLException {
        String name = "";
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT nama_depan, nama_belakang FROM users WHERE id = ?");
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
    
    public boolean addNewRekening(String accountNumber, int userId, int pin) throws SQLException {
        String query = "INSERT INTO rekening (no_rekening, user_id, pin) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, accountNumber);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, pin);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding new rekening: " + e.getMessage());
            throw e;
        }
    }
    
    public Rekening getRekeningByAccountNumber(String accountNumber) {
        String query = "SELECT r.id, r.user_id, r.no_rekening, r.pin, r.saldo, u.nama_depan, u.nama_belakang " +
                       "FROM rekening r JOIN users u ON r.user_id = u.id WHERE r.no_rekening = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String accNumber = rs.getString("no_rekening");
                int pin = rs.getInt("pin");
                double balance = rs.getDouble("saldo");
                String firstName = rs.getString("nama_depan");
                String lastName = rs.getString("nama_belakang");

                return new Rekening(id, userId, accNumber, pin, balance, firstName, lastName);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving rekening: " + e.getMessage());
        }
        return null;
    }

    public boolean updatePin(int accountId, int newPin) throws SQLException {
        String query = "UPDATE rekening SET pin = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, newPin);
            pstmt.setInt(2, accountId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating PIN: " + e.getMessage());
            throw e;
        }
    }

    
    public boolean deleteRekeningByAccountNumber(String accountNumber) {
        String query = "DELETE FROM rekening WHERE no_rekening = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, accountNumber);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting rekening: " + e.getMessage());
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
