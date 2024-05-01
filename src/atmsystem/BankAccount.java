/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atmsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankAccount {
    private int userId;
    private double balance;
    private List<Transaction> transactions;
    private Connection conn;

    public BankAccount(Connection conn, int userId) {
        this.conn = conn;
        this.userId = userId;
        balance = retrieveBalanceFromDatabase();
        transactions = new ArrayList<>();
    }

    private double retrieveBalanceFromDatabase() {
        double balance = 0.0;
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT balance FROM users WHERE id = ?")) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                balance = rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving balance from database: " + e.getMessage());
        }
        return balance;
    }

    public void checkBalance() {
        System.out.println("\nYour current balance is: $" + balance);
    }

    public void deposit(Scanner scanner) {
        checkBalance();
        System.out.print("\nHow much money do you want to deposit? $");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Amount cannot be zero or negative.");
            System.out.println("To cancel the transaction, type 9.");
            int cancelOption = scanner.nextInt();
            if (cancelOption == 9) {
                System.out.println("Transaction cancelled.");
                return;
            }
        }

        balance += amount;
        transactions.add(new Transaction("Deposit", amount));

        updateBalanceInDatabase(balance);
        addTransactionToDatabase("Deposit", amount);

        System.out.println("Deposit successful. Your balance right now: $" + balance);
    }

    public void withdraw(Scanner scanner) {
        checkBalance();
        System.out.print("\nEnter the amount to withdraw: $");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Amount cannot be zero or negative.");
            System.out.println("To cancel the transaction, type 9.");
            int cancelOption = scanner.nextInt();
            if (cancelOption == 9) {
                System.out.println("Transaction cancelled.");
                return;
            }
        }

        if (amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction("Withdrawal", amount));

            updateBalanceInDatabase(balance);
            addTransactionToDatabase("Withdrawal", amount);

            System.out.println("Withdrawal successful. Your remaining balance: $" + balance);
        } else {
            System.out.println("Insufficient funds. Cannot withdraw.");
        }
    }

    public void transfer(Scanner scanner, Database dbManager, String senderName) {
        checkBalance();
        System.out.print("\nEnter the recipient's account number: ");
        String recipientAccountNumber = scanner.nextLine();

        int recipientUserId = dbManager.getUserIdByAccountNumber(recipientAccountNumber);
        if (recipientUserId != -1) {
            String recipientName;
            try {
                recipientName = dbManager.getUserName(recipientUserId);
            } catch (SQLException e) {
                System.out.println("Error fetching recipient's name: " + e.getMessage());
                return;
            }

            System.out.println("Recipient Name: " + recipientName);
            System.out.println("Is this the right recipient? (Y/N)");
            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("Y")) {
                System.out.print("Enter the amount to transfer: $");
                double amount = scanner.nextDouble();
                scanner.nextLine(); // Consume newline character

                if (amount <= 0) {
                    System.out.println("Invalid amount. Amount cannot be zero or negative.");
                    System.out.println("Transfer cancelled.");
                    return;
                }

                if (amount <= balance) {
                    balance -= amount;
                    updateBalanceInDatabase(balance);

                    BankAccount recipientAccount = new BankAccount(conn, recipientUserId);
                    recipientAccount.transferAmount(amount);

                    addTransactionToDatabase("Transfer to " + recipientName, amount);
                    recipientAccount.addTransactionToDatabase("Transfer from " + senderName, amount);

                    System.out.println("Transfer successful. $" + amount + " transferred to account number: " + recipientAccountNumber);
                } else {
                    System.out.println("Insufficient funds. Transfer cancelled.");
                }
            } else {
                System.out.println("Transfer cancelled.");
            }
        } else {
            System.out.println("Recipient account not found. Please try input the right recipient number.");
        }
    }
    
    public void transferAmount(double amount) {
        balance += amount;
        transactions.add(new Transaction("Transfer", amount));

        updateBalanceInDatabase(balance);

        System.out.println("Transfer successful. Your balance right now: $" + balance);
    }

    private void addTransactionToDatabase(String type, double amount) {
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO transactions (user_id, type, amount, timestamp) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setObject(4, LocalDateTime.now());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding transaction to database: " + e.getMessage());
        }
    }

    private void updateBalanceInDatabase(double newBalance) {
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET balance = ? WHERE id = ?")) {
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating balance in database: " + e.getMessage());
        }
    }

    public void viewTransactionHistory(Database dbManager) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM transactions WHERE user_id = ?")) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nTransaction History:");
            if (!rs.isBeforeFirst()) {
                System.out.println("There is no transaction history yet.");
            } else {
                System.out.println("\nTransaction History:");
                while (rs.next()) {
                    int transactionId = rs.getInt("id");
                    String type = rs.getString("type");
                    double amount = rs.getDouble("amount");
                    LocalDateTime timestamp = rs.getObject("timestamp", LocalDateTime.class);

                    System.out.println("Transaction ID: " + transactionId);
                    System.out.println("Type: " + type);
                    System.out.println("Amount: $" + amount);
                    System.out.println("Timestamp: " + timestamp);
                    System.out.println("-------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transaction history: " + e.getMessage());
        }
    }

    
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
