package com.mycompany.prolab2_1;

public class Taxi extends Vehicle {
    private static final int TAXI_AVG_SPEED = 40; // km/saat
    
    public Taxi(double openingFee, double costPerKm) {
        super("TAXI", openingFee, costPerKm, TAXI_AVG_SPEED);
    }
    
    public Taxi() {
        this(10.0, 4.0); // Varsayılan: Açılış ücreti: 10 TL, 4 TL/km
    }
    
    @Override
    public double calculateFare(double distance) {
        return getBaseFare() + (distance * getFarePerKm());
    }
    
    public double getOpeningFee() {
        return getBaseFare();
    }
    
    public double getCostPerKm() {
        return getFarePerKm();
    }
}