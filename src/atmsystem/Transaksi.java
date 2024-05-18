/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atmsystem;

/**
 *
 * @author aza
 */
import java.time.LocalDateTime;

public class Transaksi {
    private int transactionId;
    private int accountId;
    private String transactionType;
    private double amount;
    private String description;
    private LocalDateTime timestamp;

    public Transaksi(int accountId, int transactionId, String transactionType, double amount, String description, LocalDateTime timestamp) {
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
