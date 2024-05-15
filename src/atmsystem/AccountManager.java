package atmsystem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AccountManager {
    private int userId;
    private double saldo;
    private int accountId;
    private List<Transaksi> transactions;
    private Database dbManager;

    public AccountManager(Database dbManager, int accountId) {
        this.dbManager = dbManager;
//        this.userId = userId;
        this.accountId = accountId;
        saldo = dbManager.getBalance(accountId);
        transactions = new ArrayList<>();
    }

    public void checkBalance() {
        System.out.println("\nSaldo anda : Rp. " + saldo);
    }

    public void deposit(Scanner scanner) {
        checkBalance();
        System.out.print("\nJumlah uang yang akan di setor? Rp. ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Nilai tidak bisa kosong atau negatif.");
            System.out.println("Transaksi batal.");
            return;
        }
        
        int transactionId = dbManager.generateTransactionId();

        saldo += amount;
        dbManager.updateBalanceDatabase(accountId, saldo);

        Transaksi depositTransaction = new Transaksi(transactionId, accountId, "Setor", amount, "Setor Tunai", LocalDateTime.now());
        dbManager.addTransaction(depositTransaction);
        
        System.out.println("Setor berhasil. Saldo di rekening anda sebesar: Rp. " + saldo);
    }

    public void withdraw(Scanner scanner) {
        checkBalance();
        System.out.print("\nMasukkan jumlah yang akan di ambil: Rp. ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Nilai tidak bisa kosong atau negatif.");
            System.out.println("Transaksi batal.");
            return;
        }

        int transactionId = dbManager.generateTransactionId();
            
        if (amount <= saldo) {
            saldo -= amount;
            dbManager.updateBalanceDatabase(accountId, saldo);
            
            Transaksi withdrawalTransaction = new Transaksi(transactionId, accountId, "Tarik", amount, "Tarik Tunai", LocalDateTime.now());
            dbManager.addTransaction(withdrawalTransaction);
            
            System.out.println("Transaksi berhasil. Saldo anda tersisa: Rp. " + saldo);
        } else {
            System.out.println("Dana tidak mencukupi. Silahkan coba lagi.");
        }
    }

    public void transfer(Scanner scanner) throws SQLException {
        checkBalance();
        System.out.print("\nCari No. Rekening yang dituju: ");
        String recipientAccountNumber = scanner.nextLine();
        
        int transactionId = dbManager.generateTransactionId();

        int recipientUserId = dbManager.getUserIdByAccountNumber(recipientAccountNumber);
        
        if (recipientUserId == userId) {
            System.out.println("\nTidak bisa melakukan transaksi ke diri sendiri. Coba lagi.");
            return;
        }
        
        if (recipientUserId != -1) {
            String recipientName;
            try {
                recipientName = dbManager.getUserName(recipientUserId);
            } catch (SQLException e) {
                System.out.println("Error fetching recipient's name: " + e.getMessage());
                return;
            }

            System.out.println("Nama Penerima: " + recipientName);

            System.out.print("Nominal yang akan di Transfer: Rp. ");
            double amount = scanner.nextDouble();
            scanner.nextLine();
            
            if (amount <= 0) {
                System.out.println("Nominal tidak sah. Nominal tidak boleh kosong atau negatif.");
                return;
            }
            
            
            if (amount <= saldo) {
                saldo -= amount;
                dbManager.updateBalanceDatabase(accountId, saldo);
                double recipientBalance = dbManager.getBalance(recipientUserId);
                recipientBalance += amount;
                dbManager.updateBalanceDatabase(recipientUserId, recipientBalance);
                System.out.println("Transfer berhasil sebesar Rp. " + amount + " transfer ke No. Rekening: " + recipientAccountNumber);
                
                Transaksi transferToTransaction = new Transaksi(transactionId, accountId, "Transfer", amount, "Transfer kepada " + recipientName, LocalDateTime.now());
                dbManager.addTransaction(transferToTransaction);
                Transaksi transferFromTransaction = new Transaksi(transactionId, recipientUserId, "Transfer", amount, "Transfer dari " + dbManager.getUserName(accountId), LocalDateTime.now());
                dbManager.addTransaction(transferFromTransaction);
                
                } else {
                    System.out.println("Dana tidak mencukupi. Transaksi gagal");
                }
        } else {
            System.out.println("Recipient account not found. Please try inputting the correct recipient number.");
        }
    }

    public void viewTransactionHistory() {
        System.out.println("\nTransaction History:");

        try {
            List<Transaksi> transactions = dbManager.getTransactionHistory(accountId);
            if (transactions.isEmpty()) {
                System.out.println("No transaction history available.");
            } else {
                for (Transaksi transaction : transactions) {
                    System.out.println("Transaksi ID: " + transaction.getTransactionId());
//                    System.out.println("Tipe Transaksi: " + transaction.getTransactionType());
                    System.out.println("Jumlah: " + transaction.getAmount());
                    System.out.println("Transaksi: " + transaction.getDescription());
                    System.out.println("Tanggal: " + transaction.getTimestamp());
                    System.out.println("------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transaction history: " + e.getMessage());
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
}
