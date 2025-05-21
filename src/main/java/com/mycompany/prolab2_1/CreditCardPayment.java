package com.mycompany.prolab2_1;

public class CreditCardPayment extends Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " with credit card.");
    }
} 