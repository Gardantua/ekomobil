package com.mycompany.prolab2_1;

public class PassengerType {
    public static final int REGULAR = 0;
    public static final int STUDENT = 1;
    public static final int ELDERLY = 2;
    
    private int type;
    private String name;
    
    public PassengerType(int type) {
        this.type = type;
        
        switch (type) {
            case STUDENT:
                this.name = "Öğrenci";
                break;
            case ELDERLY:
                this.name = "Yaşlı (65+)";
                break;
            default:
                this.name = "Genel";
                break;
        }
    }
    
    public double calculateDiscount(double originalFare) {
        if (HolidayManager.isTodayFreeTransportationDay()) {
            return originalFare;
        }
        
        switch (type) {
            case STUDENT:
                return originalFare * 0.5; 
            case ELDERLY:
                return originalFare * 0.75; 
            default:
                return 0; 
        }
    }
    
    public double calculateFare(double originalFare) {
        if (HolidayManager.isTodayFreeTransportationDay()) {
            return 0.0;
        }
        
        return originalFare - calculateDiscount(originalFare);
    }
    
    public String getDiscountDescription() {
        if (HolidayManager.isTodayFreeTransportationDay()) {
            return "Bayram günü - Ücretsiz";
        }
        
        // Normal açıklama
        switch (type) {
            case STUDENT:
                return "50% indirimli";
            case ELDERLY:
                return "75% indirimli";
            default:
                return "Standart tarife";
        }
    }
    
    // Getter metodları
    public int getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
}