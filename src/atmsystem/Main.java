/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package atmsystem;

/**
 *
 * @author aza
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Database dbManager = new Database("jdbc:mysql://localhost:3306/atm_database", "atm_user", "root");

        String name = "";
        double balance = 0.0;
        try {
            name = dbManager.getUserName(1); // Assuming user ID 1 for simplicity
            balance = dbManager.getBalance(1); // Assuming user ID 1 for simplicity
        } catch (SQLException e) {
            System.out.println("Error fetching user data: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("\nHello " + name + "! Welcome to the ATM system.");
        System.out.println("Your current balance is: $" + balance);

        Connection conn = dbManager.getConnection();
        Scanner scanner = new Scanner(System.in);
        BankAccount account = new BankAccount(conn);

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. View Transaction History");
            System.out.println("5. Exit");
            System.out.print("\nYour input: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    account.checkBalance();
                    break;
                case 2:
                    account.deposit(scanner);
                    break;
                case 3:
                    account.withdraw(scanner);
                    break;
                case 4:
                     account.viewTransactionHistory();
                    break;
                case 5:
                    System.out.println("Exiting program. Thank you!");
                    scanner.close();
                    dbManager.closeConnection();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
        }
    }
}
