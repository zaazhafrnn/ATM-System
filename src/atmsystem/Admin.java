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
import java.sql.PreparedStatement;

public class Admin {
    private Scanner scanner;
    private Database dbManager;

    public Admin(Scanner scanner, Database dbManager) {
        this.scanner = scanner;
        this.dbManager = dbManager;
    }

    public void displayAdminMenu() {
        System.out.println("\nAdmin Menu:");
        System.out.println("1. Lihat Semua Users");
        System.out.println("2. Tambah User Baru");
        System.out.println("3. Edit User");
        System.out.println("4. Hapus User");
        System.out.println("5. Lihat Semua Rekening");
        System.out.println("6. Edit Pin Rekening");
        System.out.println("7. Hapus Rekening");
        System.out.println("8. Kembali ke halaman utama");
        System.out.print("\nInput: ");
    }

    public void handleAdminInput() throws SQLException {
        boolean running = true;
        while (running) {
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
                     viewAllRekenings();
                    break;
                case 6:
                     editRekening();
                    break;
                case 7:
                     deleteRekening();
                    break;
                case 8:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please choose the right option");
                    break;
            }
        }
    }
    
    private void viewAllUsers() {
        System.out.println("\nAll Users:");
        List<User> users = dbManager.getAllUsers();
        for (User user : users) {
            System.out.println("User ID: " + user.getId());
            System.out.println("NIK: " + user.getNik());
            System.out.println("Nama Depan: " + user.getFirstName());
            System.out.println("Nama Belakang: " + user.getLastName());
            System.out.println("Alamat: " + user.getAddress());
            System.out.println("No. Telp: " + user.getPhone());
            System.out.println("-------------------------------------");
        }
    }
    
    private void addNewUser() {
        System.out.print("\nMasukkan NIK: ");
        int nik = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Nama Depan: ");
        String firstName = scanner.nextLine();

        System.out.print("Nama Belakang: ");
        String lastName = scanner.nextLine();

        System.out.print("Alamat: ");
        String address = scanner.nextLine();

        System.out.print("Nomer Telpon: ");
        String phone = scanner.nextLine();

        boolean success = dbManager.addNewUser(nik, firstName, lastName, address, phone);
        if (success) {
            System.out.println("New user added successfully.");
        } else {
            System.out.println("Failed to add new user.");
        }
    }
    
    public void editUser() {
        System.out.print("Masukkan NIK yang ingin di edit: ");
        int nik = scanner.nextInt();
        scanner.nextLine(); 

        User userToEdit = dbManager.getUserByNik(nik);
        if (userToEdit == null) {
            System.out.println("User tidak ditemukan.");
            return;
        }

        System.out.println("Informasi User:");
        System.out.println("NIK: " + userToEdit.getNik());
        System.out.println("Nama Depan: " + userToEdit.getFirstName());
        System.out.println("Nama Belakang: " + userToEdit.getLastName());
        System.out.println("Alamat: " + userToEdit.getAddress());
        System.out.println("No Telpon: " + userToEdit.getPhone());

        System.out.println("\nPilih yang ingin di edit:");
        System.out.println("1. Ganti Nama Depan");
        System.out.println("2. Ganti Nama Belakang");
        System.out.println("3. Ganti Alamat");
        System.out.println("4. Ganti No Telpon");
        System.out.println("5. Batal Edit");

        System.out.print("\nPilih: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                System.out.print("Masukkan Nama Depan: ");
                String newFirstName = scanner.nextLine();
                userToEdit.setFirstName(newFirstName);
                break;
            case 2:
                System.out.print("Masukkan Nama Belakang: ");
                String newLastName = scanner.nextLine();
                userToEdit.setLastName(newLastName);
                break;
            case 3:
                System.out.print("Masukkan Alamat: ");
                String newAddress = scanner.nextLine();
                userToEdit.setAddress(newAddress);
                break;
            case 4:
                System.out.print("Masukkan No Telpon: ");
                String newPhone = scanner.nextLine();
                userToEdit.setPhone(newPhone);
                break;
            case 5:
                System.out.println("Batal Edit.");
                return;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        boolean success = dbManager.updateUser(userToEdit);
        if (success) {
            System.out.println("Data User berhasil di edit.");
        } else {
            System.out.println("Failed to update user information.");
        }
    }

    private void deleteUser() {
        System.out.print("Masukkan NIK yang ingin di hapus: ");
        int nik = scanner.nextInt();
        scanner.nextLine(); 

        User userToDelete = dbManager.getUserByNik(nik);
        if (userToDelete == null) {
            System.out.println("User tidak ditemukan.");
            return;
        }

        System.out.println("Apakah anda yakin ingin menghapus user ini?");
        System.out.println("NIK: " + userToDelete.getNik());
        System.out.println("Nama Depan: " + userToDelete.getFirstName());
        System.out.println("Nama Belakang: " + userToDelete.getLastName());
        System.out.println("Alamat: " + userToDelete.getAddress());
        System.out.println("No Telpon: " + userToDelete.getPhone());
        System.out.print("Ketik 'y' untuk lanjut: ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            boolean success = dbManager.deleteUserByNik(nik);
            if (success) {
                System.out.println("User berhasil dihapus.");
            } else {
                System.out.println("Gagal Menghapus User.");
            }
        } else {
            System.out.println("Batal menghapus User.");
        }
    }

    private void viewAllRekenings() {
        System.out.println("\nSemua Rekening:");
        List<Rekening> rekenings = dbManager.getAllRekenings();
        for (Rekening rekening : rekenings) {
            System.out.println("Rekening ID: " + rekening.getId());
            System.out.println("Nomer Rekening: " + rekening.getAccountNumber());
            System.out.println("PIN: " + rekening.getPin());
            System.out.println("Nama: " + rekening.getFirstName() + " " + rekening.getLastName());
            System.out.println("-------------------------------------");
        }
    }
    
    private void editRekening() {
        System.out.print("\nMasukkan Nomer Rekening: ");
        String accountNumber = scanner.nextLine();

        Rekening rekeningToEdit = dbManager.getRekeningByAccountNumber(accountNumber);
        if (rekeningToEdit == null) {
            System.out.println("Rekening tidak ditemukan.");
            return;
        }

        System.out.println("Nomer Rekening: " + rekeningToEdit.getAccountNumber());
        System.out.println("Nama Pemilik: " + rekeningToEdit.getFirstName() + " " + rekeningToEdit.getLastName());

        System.out.println("\nMasukkan Pin Baru: ");
        int newPin = scanner.nextInt();
        scanner.nextLine(); 

        boolean success = dbManager.updateRekeningPin(rekeningToEdit.getId(), newPin);
        if (success) {
            System.out.println("PIN berhasil diperbarui.");
        } else {
            System.out.println("Failed to update rekening PIN.");
        }
    }
    
    private void deleteRekening() {
        System.out.print("\nMasukkan Nomer Rekening yang akan di hapus: ");
        String accountNumber = scanner.nextLine();

        Rekening rekeningToDelete = dbManager.getRekeningByAccountNumber(accountNumber);
        if (rekeningToDelete == null) {
            System.out.println("Rekening tidak ditemukan.");
            return;
        }

        boolean success = dbManager.deleteRekeningByAccountNumber(accountNumber);
        if (success) {
            System.out.println("Rekening berhasil dihapus.");
        } else {
            System.out.println("Gagal menghapus rekening.");
        }
    }
}