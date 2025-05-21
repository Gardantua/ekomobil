package com.mycompany.prolab2_1;

public class Tram extends Vehicle {
    private static final int TRAM_AVG_SPEED = 30; // km/saat
    
    public Tram(double baseFare, double farePerKm) {
        super("TRAM", baseFare, farePerKm, TRAM_AVG_SPEED);
    }
    
    public Tram() {
        this(2.5, 0.3); 
    }
}