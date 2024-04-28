/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atmsystem;

/**
 *
 * @author aza
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankAccount {
    private double balance;
    private List<Transaction> transactions;

    public BankAccount(double initialBalance) {
        balance = initialBalance;
        transactions = new ArrayList<>();
    }

    public void checkBalance() {
        System.out.println("\nYour current balance is: $" + balance);
    }

    public void deposit(Scanner scanner) {
        checkBalance();
        System.out.print("\nHow much money do you want to deposit? $");
        double amount = scanner.nextDouble();

        balance += amount;
        transactions.add(new Transaction("Deposit", amount));

        System.out.println("Deposit successful. Your balance right now: $" + balance);
    }

    public void withdraw(Scanner scanner) {
        checkBalance();
        System.out.print("\nEnter the amount to withdraw: $");
        double amount = scanner.nextDouble();

        if (amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction("Withdrawal", amount));
            System.out.println("Withdrawal successful. Remaining balance: $" + balance);
        } else {
            System.out.println("Insufficient funds. Cannot withdraw.");
        }
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
