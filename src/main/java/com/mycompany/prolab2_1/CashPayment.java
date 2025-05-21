package com.mycompany.prolab2_1;

public class CashPayment extends Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " in cash.");
    }
}