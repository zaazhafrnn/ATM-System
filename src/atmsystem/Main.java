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
        Scanner scanner = new Scanner(System.in);
        BankAccount account = null;

        int userId = authenticateUser(dbManager, scanner);
        if (userId != -1) {
            System.out.println("\nAuthentication successful.");

            String name = "";
            try {
                name = dbManager.getUserName(userId);
            } catch (SQLException e) {
                System.out.println("Error fetching user data: " + e.getMessage());
                System.exit(1);
            }

            System.out.println("\nHello " + name + "! Welcome to the ATM");
            System.out.println("User ID: " + userId);
            System.out.println("User Name: " + name);

            Connection conn = dbManager.getConnection();
            account = new BankAccount(conn, userId);

            while (true) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

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
                        account.viewTransactionHistory(dbManager);
                        break;
                    case 5:
                        account.transfer(scanner, dbManager, name);
                        break;
                    case 6:
                        System.out.println("Exiting program. Thank you!");
                        scanner.close();
                        try {
                            dbManager.closeConnection();
                        } catch (SQLException e) {
                            System.out.println("Error closing database connection: " + e.getMessage());
                        }
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please choose again.");
                }
            }
        } else {
            System.out.println("Authentication failed. Exiting program.");
        }
    }

    private static int authenticateUser(Database dbManager, Scanner scanner) {
        System.out.print("\nEnter your account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter your PIN: ");
        int pin = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        if (dbManager.authenticateUser(accountNumber, pin)) {
            return dbManager.getUserIdByAccountNumber(accountNumber);
        } else {
            return -1; // Authentication failed
        } // Return -1 on authentication failure
    }

    private static void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Check Balance");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. View Transaction History");
        System.out.println("5. Transfer");
        System.out.println("6. Exit");
        System.out.print("\nYour input: ");
    }
}
