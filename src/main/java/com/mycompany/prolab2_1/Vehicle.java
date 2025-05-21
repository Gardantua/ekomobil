package com.mycompany.prolab2_1;

public abstract class Vehicle {
    private String type;
    private double baseFare;
    private double farePerKm;
    private int avgSpeed;
    

    public Vehicle(String type, double baseFare, double farePerKm, int avgSpeed) {
        this.type = type;
        this.baseFare = baseFare;
        this.farePerKm = farePerKm;
        this.avgSpeed = avgSpeed;
    }
    
    public double calculateFare(double distance) {
        return baseFare + (distance * farePerKm);
    }
    
    public int calculateDuration(double distance) {
        return (int) (distance / avgSpeed * 60);
    }
    
    public String getType() {
        return type;
    }
    
    public double getBaseFare() {
        return baseFare;
    }
    
    public double getFarePerKm() {
        return farePerKm;
    }
    
    public int getAvgSpeed() {
        return avgSpeed;
    }
}