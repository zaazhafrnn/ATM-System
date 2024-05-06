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
        System.out.println("3. Edit User");
        System.out.println("4. Delete User");
        System.out.println("5. Return to Main Menu");
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
                    editUser();
                    break;
                case 4:
                    deleteUser();
                    break;
                case 5:
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
        System.out.println("Pin: " + user.getPin());
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
    
    public void editUser() {
        System.out.print("Enter the user ID you want to edit: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); 

        User userToEdit = dbManager.getUserById(userId);
        if (userToEdit == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("User Information:");
        System.out.println("Name: " + userToEdit.getName());
        System.out.println("Account Number: " + userToEdit.getAccountNumber());
        System.out.println("PIN: " + userToEdit.getPin());

        System.out.println("\nSelect what you want to edit:");
        System.out.println("1. Change Name");
        System.out.println("2. Change Account Number");
        System.out.println("3. Change PIN");
        System.out.println("4. Cancel Edit");

        System.out.print("\nYour choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                System.out.println("Current user name: " + userToEdit.getName());
                System.out.println("Enter new name: ");
                String newName = scanner.nextLine();
                userToEdit.setName(newName);
                dbManager.updateUser(userToEdit);
                System.out.println("Name updated successfully.");
                break;
            case 2:
                System.out.println("Current " + userToEdit.getName() + "'s account number: " + userToEdit.getAccountNumber());
                System.out.println("Enter new account number: ");
                String newAccountNumber = scanner.nextLine();
                userToEdit.setAccountNumber(newAccountNumber);
                dbManager.updateUser(userToEdit);
                System.out.println("Account number updated successfully.");
                break;
            case 3:
                System.out.println("Current " + userToEdit.getName() + "'s pin: " + userToEdit.getPin());
                System.out.println("Enter new PIN: ");
                int newPin = scanner.nextInt();
                scanner.nextLine(); 
                userToEdit.setPin(newPin);
                dbManager.updateUser(userToEdit);
                System.out.println("PIN updated successfully.");
                break;
            case 4:
                System.out.println("Edit canceled.");
                break;
            default:
                System.out.println("Invalid choice.");
                break;
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
