package atmsystem;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankAccount {
    private int userId;
    private double balance;
    private List<Transaction> transactions;
    private Database dbManager;
    private int transactionIdCounter;

    public BankAccount(Database dbManager, int userId) {
        this.dbManager = dbManager;
        this.userId = userId;
        balance = dbManager.getBalance(userId);
        transactions = new ArrayList<>();
        transactionIdCounter = 1;
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
            System.out.println("Transaction cancelled.");
            return;
        }
        
        int transactionId = generateTransactionId();

        balance += amount;
        transactions.add(new Transaction(transactionId, "Deposit", amount, LocalDateTime.now()));

        dbManager.updateBalanceDatabase(userId, balance);
        dbManager.addHistoryTransaction(transactionId, userId, "Deposit", amount, LocalDateTime.now());


        System.out.println("Deposit successful. Your balance right now: $" + balance);
    }

    public void withdraw(Scanner scanner) {
        checkBalance();
        System.out.print("\nEnter the amount to withdraw: $");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Amount cannot be zero or negative.");
            System.out.println("Transaction cancelled.");
            return;
        }

        int transactionId = generateTransactionId();
            
        if (amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction(transactionId, "Withdrawal", amount, LocalDateTime.now()));
            

            dbManager.updateBalanceDatabase(userId, balance);
            dbManager.addHistoryTransaction(transactionId, userId, "Withdrawal", amount, LocalDateTime.now());

            System.out.println("Withdrawal successful. Your remaining balance: $" + balance);
        } else {
            System.out.println("Insufficient funds. Cannot withdraw.");
        }
    }

    public void transfer(Scanner scanner) throws SQLException {
        checkBalance();
        System.out.print("\nEnter the recipient's account number: ");
        String recipientAccountNumber = scanner.nextLine();
        int transactionId = generateTransactionId();


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
                scanner.nextLine();

                if (amount <= 0) {
                    System.out.println("Invalid amount. Amount cannot be zero or negative.");
                    System.out.println("Transfer cancelled.");
                    return;
                }
                
                if (amount <= balance) {
                    balance -= amount;
                    dbManager.updateBalanceDatabase(userId, balance);
                    dbManager.addHistoryTransaction(transactionId, userId, "Transfer to " + recipientName, amount, LocalDateTime.now());
                    
                    double recipientBalance = dbManager.getBalance(recipientUserId);
                    recipientBalance += amount;
                    dbManager.updateBalanceDatabase(recipientUserId, recipientBalance);
                    dbManager.addHistoryTransaction(transactionId, recipientUserId, "Transfer from " + dbManager.getUserName(userId), amount, LocalDateTime.now());

                    
//                    dbManager.addHistoryTransaction(userId, "Transfer to " + recipientName, amount, LocalDateTime.now());
//                    dbManager.addHistoryTransaction(recipientUserId, "Transfer from " + dbManager.getUserName(userId), amount, LocalDateTime.now());

                    System.out.println("Transfer successful. $" + amount + " transferred to account number: " + recipientAccountNumber);
                } else {
                    System.out.println("Insufficient funds. Transfer cancelled.");
                }
            } else {
                System.out.println("Transfer cancelled.");
            }
        } else {
            System.out.println("Recipient account not found. Please try inputting the correct recipient number.");
        }
    }

    public void viewTransactionHistory() throws SQLException {
        List<Transaction> transactions = dbManager.getTransactionHistory(userId);

        System.out.println("\nTransaction History:");
        if (transactions.isEmpty()) {
            System.out.println("There is no transaction history yet.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println("Type: " + transaction.getType());
                System.out.println("Amount: $" + transaction.getAmount());
                System.out.println("Timestamp: " + transaction.getTimestamp());
                System.out.println("------------------------");
            }
        }
    }

    public Database getDatabase() {
        return dbManager;
    }
    
    public void closeScanner(Scanner scanner) {
        if (scanner != null) {
            scanner.close();
        }
    }
    
    private int generateTransactionId() {
        return transactionIdCounter++;
    }


}
