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
        System.out.println("3. Return to Main Menu");
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
                    return; // Return to main menu
                default:
                    System.out.println("Invalid choice. Please choose the right option");
            }
        }
    }

    public void viewAllUsers() throws SQLException {
        // Implement method to fetch and display all users from the database
    }

    public void addNewUser() throws SQLException {
        // Implement method to add a new user to the database
    }
}
