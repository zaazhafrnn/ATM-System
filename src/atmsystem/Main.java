package atmsystem;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Database dbManager = new Database();
        Scanner scanner = new Scanner(System.in);
        AccountManager account = null;
        
        boolean running = true;
        while (running) {
            System.out.println("\nSelamat Datang!");
            System.out.println("1. Login Sebagai Admin");
            System.out.println("2. Login Menggunakan Rekening");
            System.out.print("\nPilih: ");
            int userTypeChoice = scanner.nextInt();
            scanner.nextLine(); 

            if (userTypeChoice == 1) {
                System.out.println("\nSukses Login Sebagai Admin");
                Admin admin = new Admin(scanner, dbManager);
                admin.handleAdminInput();
            } else if (userTypeChoice == 2) {
                System.out.print("\nSilahkan Masukkan Nomer Rekening Anda: ");
                String accountNumber = scanner.nextLine();
                System.out.print("Silahkan Masukkan PIN: ");
                int pin = scanner.nextInt();
                scanner.nextLine();

                int accountId = dbManager.authenticateRekening(accountNumber, pin);
                if (accountId != -1) {
                    String firstName = dbManager.getUserName(accountId);
                    System.out.println("\nSelamat datang, " + firstName + "!");

                    account = new AccountManager(dbManager, accountId);
                    Home home = new Home(scanner, account);
                    home.handleUserInput();
                } else {
                    System.out.println("Authentication failed. Exiting program.");
                }
            } else {
                System.out.println("Pilihan tidak Valid. Keluar program");
                running = false;
            }
        }
        
        scanner.close();
    }
}
