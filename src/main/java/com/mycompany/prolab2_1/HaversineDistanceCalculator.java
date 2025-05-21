package com.mycompany.prolab2_1;

import java.util.*;

public class HaversineDistanceCalculator implements DistanceCalculator {
    private static final double EARTH_RADIUS = 6371; 
    private Map<String, Stop> stops;
    
    public HaversineDistanceCalculator() {
        this.stops = new HashMap<>();
    }
    
    public void setStops(Map<String, Stop> stops) {
        this.stops = stops;
    }
    
    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) * 
                  Math.sin(dLon/2) * Math.sin(dLon/2);
                  
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return EARTH_RADIUS * c;
    }
    
    @Override
    public List<Stop> findShortestPath(String startId, String endId, String type) {

        if (startId.equals(endId)) {
            List<Stop> path = new ArrayList<>();
            path.add(stops.get(startId));
            return path;
        }
        
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> unvisited = new HashSet<>();
        
        for (String stopId : stops.keySet()) {
            Stop stop = stops.get(stopId);
            
            if (type.equals(stop.getType())) {
                distances.put(stopId, Double.MAX_VALUE);
                previous.put(stopId, null);
                unvisited.add(stopId);
            }
        }
        
        distances.put(startId, 0.0);
        
        // Dijkstra 
        while (!unvisited.isEmpty()) {
            String current = null;
            double minDist = Double.MAX_VALUE;
            
            for (String stopId : unvisited) {
                double dist = distances.get(stopId);
                if (dist < minDist) {
                    minDist = dist;
                    current = stopId;
                }
            }
            
            if (current == null) {
                break;
            }
            
            if (current.equals(endId)) {
                break;
            }
            
            unvisited.remove(current);
            
            Stop currentStop = stops.get(current);
            if (currentStop.getNextStops() != null) {
                for (Stop.NextStop nextStop : currentStop.getNextStops()) {
                    String neighborId = nextStop.getStopId();
                    
                    if (unvisited.contains(neighborId)) {
                        double alt = distances.get(current) + nextStop.getDuration();
                        
                        // Daha kısa bir yol bulduk mu?
                        if (alt < distances.getOrDefault(neighborId, Double.MAX_VALUE)) {
                            distances.put(neighborId, alt);
                            previous.put(neighborId, current);
                        }
                    }
                }
            }
        }
        
        List<Stop> path = new ArrayList<>();
        String current = endId;
        
        if (previous.get(endId) == null && !startId.equals(endId)) {
            return path;
        }
        
        while (current != null) {
            path.add(0, stops.get(current));
            current = previous.get(current);
        }
        
        return path;
    }
    
    @Override
    public Stop.NextStop findConnection(Stop fromStop, String toStopId) {
        if (fromStop.getNextStops() != null) {
            for (Stop.NextStop nextStop : fromStop.getNextStops()) {
                if (nextStop.getStopId().equals(toStopId)) {
                    return nextStop;
                }
            }
        }
        return null;
    }
    
    @Override
    public List<DistanceCalculator.TransferPoint> findPossibleTransferPoints() {
        List<DistanceCalculator.TransferPoint> transferPoints = new ArrayList<>();
        
        for (Stop stop : stops.values()) {
            if (stop.getTransfer() != null) {
                Transfer transfer = stop.getTransfer();
                String fromStopId = stop.getId();
                String toStopId = transfer.getTransferStopId();
                
                Stop toStop = stops.get(toStopId);
                if (toStop == null) {
                    System.out.println("UYARI: Transfer için hedef durak bulunamadı: " + toStopId);
                    continue;
                }
                
                DistanceCalculator.TransferPoint transferPoint = new DistanceCalculator.TransferPoint();
                
                // Otobüs -> Tramva
                if (stop.getType().equals("bus") && toStop.getType().equals("tram")) {
                    transferPoint.busStopId = fromStopId;
                    transferPoint.tramStopId = toStopId;
                    transferPoint.transferDuration = transfer.getTransferDuration();
                    transferPoint.transferFare = transfer.getTransferFare();
                    transferPoints.add(transferPoint);
                    
                    System.out.println("Aktarma noktası eklendi: " + stop.getName() + " -> " + toStop.getName() + 
                                      " (Süre: " + transfer.getTransferDuration() + ", Ücret: " + transfer.getTransferFare() + ")");
                } 
                
                else if (stop.getType().equals("tram") && toStop.getType().equals("bus")) {
                    transferPoint.busStopId = toStopId;
                    transferPoint.tramStopId = fromStopId;
                    transferPoint.transferDuration = transfer.getTransferDuration();
                    transferPoint.transferFare = transfer.getTransferFare();
                    transferPoints.add(transferPoint);
                    
                    System.out.println("Aktarma noktası eklendi: " + stop.getName() + " -> " + toStop.getName() + 
                                      " (Süre: " + transfer.getTransferDuration() + ", Ücret: " + transfer.getTransferFare() + ")");
                }
            }
        }
        
        System.out.println("Bulunan aktarma noktası sayısı: " + transferPoints.size());
        return transferPoints;
    }
    
    @Override
    public int calculateWalkingDuration(double distance) {
        return (int) (distance * 12);
    }
    
    public static class TransferPoint extends DistanceCalculator.TransferPoint {
        public Stop busStop;
        public Stop tramStop; 
        public Transfer transfer;
    }
} 