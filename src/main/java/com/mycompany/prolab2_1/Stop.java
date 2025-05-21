package com.mycompany.prolab2_1;

public class Stop {
    private String id;
    private String name;
    private String type;
    private double lat;
    private double lon;
    private boolean isLastStop;
    private NextStop[] nextStops; 
    private Transfer transfer;
    private static DistanceCalculator distanceCalculator = new HaversineDistanceCalculator();
    
    public Stop(String id, String name, String type, double lat, double lon, boolean isLastStop) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lon = lon;
        this.isLastStop = isLastStop;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public double getLat() {
        return lat;
    }
    
    public double getLon() {
        return lon;
    }
    
    public boolean isLastStop() {
        return isLastStop;
    }
    
    public NextStop[] getNextStops() {
        return nextStops;
    }
    
    public void setNextStops(NextStop[] nextStops) {
        this.nextStops = nextStops;
    }
    
    public Transfer getTransfer() {
        return transfer;
    }
    
    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }
    
    public double calculateDistanceTo(Stop otherStop) {
        return distanceCalculator.calculateDistance(
            this.lat, this.lon,
            otherStop.getLat(), otherStop.getLon()
        );
    }
    
    public double calculateDistanceTo(double lat, double lon) {
        return distanceCalculator.calculateDistance(
            this.lat, this.lon,
            lat, lon
        );
    }
    
    public static void setDistanceCalculator(DistanceCalculator calculator) {
        distanceCalculator = calculator;
    }
    
    public static class NextStop {
        private String stopId;
        private double distance;
        private int duration;
        private double fare;
        
        public NextStop(String stopId, double distance, int duration, double fare) {
            this.stopId = stopId;
            this.distance = distance;
            this.duration = duration;
            this.fare = fare;
        }
        
        public String getStopId() {
            return stopId;
        }
        
        public double getDistance() {
            return distance;
        }
        
        public int getDuration() {
            return duration;
        }
        
        public double getFare() {
            return fare;
        }
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}