package com.mycompany.prolab2_1;

public class Walking extends Vehicle {
    private static final int WALKING_SPEED = 5; 
    private static final double WALKING_THRESHOLD = 3.0; 
    
    public Walking() {
        super("WALKING", 0.0, 0.0, WALKING_SPEED); 
    }
    
    public boolean isWalkable(double distance) {
        return distance <= WALKING_THRESHOLD;
    }
    
    @Override
    public int calculateDuration(double distance) {
        return (int) (distance * 12);
    }
}