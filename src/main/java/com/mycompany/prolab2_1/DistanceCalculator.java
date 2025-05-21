package com.mycompany.prolab2_1;

import java.util.List;


public interface DistanceCalculator {
    
 double calculateDistance(double lat1, double lon1, double lat2, double lon2);
    
    

    List<Stop> findShortestPath(String startId, String endId, String type);
    
    
    Stop.NextStop findConnection(Stop fromStop, String toStopId);
    
  
    List<TransferPoint> findPossibleTransferPoints();
    
  
    int calculateWalkingDuration(double distance);
    

    class TransferPoint {
        public String busStopId;
        public String tramStopId;
        public int transferDuration;
        public double transferFare;
    }
} 