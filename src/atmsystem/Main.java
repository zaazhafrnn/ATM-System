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
        
        System.out.print("\nSilahkan Masukkan Nomer Rekening Anda: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Silahkan Masukkan PIN: ");
        int pin = scanner.nextInt();
        scanner.nextLine();

        int accountId = dbManager.authenticateRekening(accountNumber, pin);
        if (accountId != -1) {
            String firstName = dbManager.getUserName(accountId);
            System.out.println("Selamat datang, " + firstName + "!");
            
            account = new AccountManager(dbManager, accountId);
            Home home = new Home(scanner, account);
            home.handleUserInput();
        } else if (accountId == 0) {
            System.out.println("Admin authentication successful. Welcome!");
//            Admin admin = new Admin(scanner, dbManager);
//            admin.handleAdminInput();
        } else {
            System.out.println("Authentication failed. Exiting program.");
        }
        
        scanner.close();
    }
}
