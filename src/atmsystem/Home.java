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
    private AccountManager account;

    public Home(Scanner scanner, AccountManager account) {
        this.scanner = scanner;
        this.account = account;
    }


    public void displayMenu() {
        System.out.println("\nPilih opsi dibawah:");
        System.out.println("\n1. Cek saldo");
        System.out.println("2. Setor Tunai");
        System.out.println("3. Tarik Tunai");
        System.out.println("4. Transfer");
        System.out.println("5. Cek Mutasi Rekening");
        System.out.println("6. Keluar");
        System.out.print("\nInput : ");
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
                    account.transfer(scanner);
                    break;
                case 5:
                    account.viewTransactionHistory();
                    break;
                case 6:
                    System.out.println("Terimakasih telah berkunjung! Sampai Ketemu lagi.");
                    account.getDatabase().closeConnection();
                    account.closeScanner(scanner);
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please choose the right option");
            }
        }
    }
}
