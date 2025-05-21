package com.mycompany.prolab2_1;

public abstract class Passenger {

    private String name;
    private String id;
    private String type;
    private double balance;

    public Passenger(String name, String id, String type, double balance) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    public void addBalance(double amount) {
        this.balance += amount;
    }
    public void deductBalance(double amount) {
        this.balance -= amount;
    }
    public boolean hasSufficientBalance(double amount) {
        return this.balance >= amount;
    }
    public void setName(String name) {
        this.name = name;
    }   
    
}
