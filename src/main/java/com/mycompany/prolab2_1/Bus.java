package com.mycompany.prolab2_1;

public class Bus extends Vehicle {
    private static final int BUS_AVG_SPEED = 25; 
    
    public Bus(double baseFare, double farePerKm) {
        super("BUS", baseFare, farePerKm, BUS_AVG_SPEED);
    }
    
    public Bus() {
        this(3.0, 0.4); 
    }
}