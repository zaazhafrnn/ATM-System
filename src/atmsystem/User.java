/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atmsystem;

/**
 *
 * @author aza
 */
public class User {
    private int id;
    private String name;
    private String accountNumber;
    private int pin;
    private double balance;

    public User(int id, String name, String accountNumber, int pin, double balance) {
        this.id = id;
        this.name = name;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
    }
    
     public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    
    public int getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

//    public void displayUserInfo() {
//        System.out.println("\nUser Information:");
//        System.out.println("ID: " + id);
//        System.out.println("Name: " + name);
//        System.out.println("Account Number: " + accountNumber);
//        System.out.println("Balance: $" + balance);
//        System.out.println("-------------------------------------");
//    }
}
