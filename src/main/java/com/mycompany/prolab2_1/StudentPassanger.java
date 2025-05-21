package com.mycompany.prolab2_1;

public class StudentPassanger extends Passenger {

    private String studentId;
    private double discountRate;

    public StudentPassanger(String name, String id, String type, double balance, String studentId, double discountRate) {
        super(name, id, type, balance);
        this.studentId = studentId;
        this.discountRate = discountRate;
    }

    @Override
    public String toString() {
        return "StudentPassenger{" +
                "name='" + getName() + '\'' +
                ", id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", balance=" + getBalance() +
                ", studentId='" + studentId + '\'' +
                ", discountRate=" + discountRate +
                '}';
    }
    @Override
    public boolean hasSufficientBalance(double amount) {
        return getBalance() >= amount;
    }
    @Override
    public void deductBalance(double amount) {
        if (hasSufficientBalance(amount)) {
            setBalance(getBalance() - amount);
        } else {
            System.out.println("Insufficient balance for " + getName());
        }
    }
    @Override
    public void addBalance(double amount) {
        setBalance(getBalance() + amount);
    }
    @Override
    public void setBalance(double balance) {
        super.setBalance(balance);
    }
    @Override
    public double getBalance() {
        return super.getBalance();
    }
    @Override
    public String getType() {
        return super.getType();
    }
    @Override
    public String getName() {
        return super.getName();
    }
    @Override
    public String getId() {
        return super.getId();
    }
    @Override
    public void setName(String name) {
        super.setName(name);
    }
}
