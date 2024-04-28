/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package atmsystem;

/**
 *
 * @author aza
 */
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import atmsystem.Transaction;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your initial balance: $");
        double initialBalance = scanner.nextDouble();

        BankAccount account = new BankAccount(initialBalance);

        while (true) {
            System.out.println("\nHalo " + name + ", selamat datang!");
            System.out.println("\nChoose an option:");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw Money");
            System.out.println("3. Deposit Money");
            System.out.println("4. View Transaction History");
            System.out.println("5. Exit");
            System.out.print("\nYour input: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    account.checkBalance();
                    break;
                case 2:
                    account.withdraw(scanner);
                    break;
                case 3:
                    account.deposit(scanner);
                    break;
                case 4:
                    viewTransactionHistory(account);
                    break;
                case 5:
                    System.out.println("Exiting program. Thank you!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
        }
    }
    
    private static void viewTransactionHistory(BankAccount account) {
        List<Transaction> transactions = account.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("\nNo transactions to display.");
        } else {
            System.out.println("\nTransaction History:");
            for (Transaction transaction : transactions) {
                System.out.println("Type: " + transaction.getType());
                System.out.println("Amount: $" + transaction.getAmount());
                System.out.println("Timestamp: " + transaction.getTimestamp());
                System.out.println();
            }
            account.checkBalance();
        }
    }
}