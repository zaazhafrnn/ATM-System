/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atmsystem;

/**
 *
 * @author aza
 */
public class Rekening {
    private int id;
    private int userId;
    private String accountNumber;
    private int pin;
    private double balance;
    private String firstName;
    private String lastName;

    public Rekening(int id, int userId, String accountNumber, int pin, double balance, String firstName, String lastName) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
