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
import java.util.List;
import java.util.Scanner;

public class Admin {
    private Scanner scanner;
    private Database dbManager;

    public Admin(Scanner scanner, Database dbManager) {
        this.scanner = scanner;
        this.dbManager = dbManager;
    }

    public void displayAdminMenu() {
        System.out.println("\nAdmin Menu:");
        System.out.println("1. View All Users");
        System.out.println("2. Add New User");
        System.out.println("3. Delete User");
        System.out.println("4. Return to Main Menu");
        System.out.println("\nYour input: ");
    }

    public void handleAdminInput() throws SQLException {
        while (true) {
            displayAdminMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    addNewUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please choose the right option");
                    handleAdminInput();
                    break;
            }
        }
    }

    private void viewAllUsers() {
    System.out.println("\nAll Users:");
    List<User> users = dbManager.getAllUsers();
    for (User user : users) {
        System.out.println("User ID: " + user.getId());
        System.out.println("Name: " + user.getName());
        System.out.println("Account Number: " + user.getAccountNumber());
//        System.out.println("Balance: " + user.getBalance());
        System.out.println("-------------------------------------");
    }
}


    private void addNewUser() {
        System.out.print("Enter new user name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new user account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter new user PIN: ");
        int pin = scanner.nextInt();
        scanner.nextLine();

        double balance = 0.0; 
        boolean success = dbManager.addNewUser(name, accountNumber, pin, balance);
        if (success) {
            System.out.println("New user added successfully.");
        } else {
            System.out.println("Failed to add new user.");
        }
    }

    private void deleteUser() {
        System.out.print("Enter user ID to delete: ");
        int userIdToDelete = scanner.nextInt();
        scanner.nextLine();

        User userToDelete = dbManager.getUserById(userIdToDelete);
        if (userToDelete != null) {
            System.out.println("Are you sure you want to delete user " + userToDelete.getName() + " (ID: " + userToDelete.getId() + ")? (Y/N)");
            String confirmation = scanner.nextLine();
            if (confirmation.equalsIgnoreCase("Y")) {
                boolean success = dbManager.deleteUser(userIdToDelete);
                if (success) {
                    System.out.println("User deleted successfully.");
                } else {
                    System.out.println("Failed to delete user.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } else {
            System.out.println("User not found.");
        }
    }


}
