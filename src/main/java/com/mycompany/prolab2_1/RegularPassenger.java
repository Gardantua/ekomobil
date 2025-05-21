package com.mycompany.prolab2_1;

public class RegularPassenger extends Passenger {

    private String vehicleType;
    private double farePerKm;

    public RegularPassenger(String name, String id, String type, double balance, String vehicleType, double farePerKm) {
        super(name, id, type, balance);
        this.vehicleType = vehicleType;
        this.farePerKm = farePerKm;
    }

    @Override
    public String toString() {
        return "RegularPassenger{" +
                "name='" + getName() + '\'' +
                ", id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", balance=" + getBalance() +
                ", vehicleType='" + vehicleType + '\'' +
                ", farePerKm=" + farePerKm +
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
    
}
