package com.mycompany.prolab2_1;

import java.util.List;

public interface RouteCalculator {

    List<Route> calculateRoutes(String startId, String endId, String passengerType);
    
    default List<Route> templateMethod(String startId, String endId, String passengerType) {
        validatePoints(startId, endId);
        
        validatePassengerType(passengerType);
        
        List<Route> routes = calculateRoutes(startId, endId, passengerType);
        
        routes = filterRoutes(routes);
        
        routes = sortRoutes(routes);
        
        return routes;
    }
    
    void validatePoints(String startId, String endId);
    
    void validatePassengerType(String passengerType);
    
    List<Route> filterRoutes(List<Route> routes);
    
    List<Route> sortRoutes(List<Route> routes);
} 