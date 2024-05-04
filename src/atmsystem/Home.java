/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atmsystem;

/**
 *
 * @author aza
 */
import java.sql.SQLException;
import java.util.Scanner;

public class Home {
    private Scanner scanner;
    private BankAccount account;

    public Home(Scanner scanner, BankAccount account) {
        this.scanner = scanner;
        this.account = account;
    }


    public void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Check Balance");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. View Transaction History");
        System.out.println("5. Transfer");
        System.out.println("6. Exit");
        System.out.println("\nYour input: ");
    }

    public void handleUserInput() throws SQLException {
        while (true) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

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
                    account.transfer(scanner);
                    break;
                case 6:
                    System.out.println("Exiting program. Thank you!");
                    account.getDatabase().closeConnection();
                    account.closeScanner(scanner);
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please choose the right option");
            }
        }
    }
}
