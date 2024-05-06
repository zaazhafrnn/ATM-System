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
        AccountManager account = null;
        
        System.out.print("\nEnter your account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter your PIN: ");
        int pin = scanner.nextInt();
        scanner.nextLine();

        int userType = authenticateUser(dbManager, accountNumber, pin);
        if (userType != -1) {
//            int userId = dbManager.getUserIdByAccountNumber(accountNumber);
            
            int userId = userType;

            if (userType == 0) { 
                System.out.println("\nAdmin authentication successful. Welcome!");
                Admin admin = new Admin(scanner, dbManager);
                admin.handleAdminInput();
            } else if (userType == userId) {
                System.out.println("\nAuthentication successful. Welcome!");
                System.out.println("\nAuthenticate as " + dbManager.getUserName(userId));
                
//                int userId = dbManager.getUserIdByAccountNumber(userId); // Assuming you have a method to get userId from account number
                
                account = new AccountManager(dbManager, userId);
                Home home = new Home(scanner, account);
                home.handleUserInput();
            }
        } else {
            System.out.println("Authentication failed. Exiting program.");
        }

        scanner.close();
    }

    private static int authenticateUser(Database dbManager, String accountNumber, int pin) {

        if (accountNumber.equals("0000") && pin == 0000) {
            return 0;
        } else {
            int userId = dbManager.getUserIdByAccountNumber(accountNumber);
            if (userId != -1 && dbManager.authenticateUser(accountNumber, pin)) {
                // Regular user authentication successful
                return userId;
            } else {
                // Authentication failed
                System.out.println("Authentication failed. Invalid credentials.");
                return -1;
            }
        }
    }

}
