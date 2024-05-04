/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package atmsystem;

/**
 *
 * @author aza
 */
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Database dbManager = new Database();
        Scanner scanner = new Scanner(System.in);
        BankAccount account = null;

        int userId = authenticateUser(dbManager, scanner);
        if (userId != -1) {
            System.out.println("\nAuthentication successful.");
            account = new BankAccount(dbManager, userId);

            Home home = new Home(scanner, account);
            home.handleUserInput();
        } else {
            System.out.println("Authentication failed. Exiting program.");
        }

        scanner.close();
    }

    private static int authenticateUser(Database dbManager, Scanner scanner) {
        System.out.print("\nEnter your account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter your PIN: ");
        int pin = scanner.nextInt();
        scanner.nextLine();
        if (dbManager.authenticateUser(accountNumber, pin)) {
            return dbManager.getUserIdByAccountNumber(accountNumber);
        } else {
            return -1;
        }
    }
}
