package com.mycompany.prolab2_1;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class GraphBuilder {
   private static final double WALKING_THRESHOLD = 3.0; 
   private boolean shouldWalk(double distance) {
    return distance <= WALKING_THRESHOLD;
}
    private String cityName;
    private Taxi taxi;
    private Map<String, Stop> stops = new HashMap<>();
    private Map<String, Vehicle> vehicles = new HashMap<>();
    private List<Route> routes = new ArrayList<>();
    private Gson gson = new Gson();
    private static DistanceCalculator distanceCalculator = new HaversineDistanceCalculator();

    public GraphBuilder() {
        this.stops = new HashMap<>();
        this.vehicles = new HashMap<>();
    }
    
    public Vehicle getVehicle(String type) {
    return vehicles.get(type);
}
    public void loadFromJson(String jsonFilePath) throws IOException {
        StringBuilder jsonContent = new StringBuilder();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(jsonFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        }
        
        JsonObject rootObject = gson.fromJson(jsonContent.toString(), JsonObject.class);
        
        cityName = rootObject.get("city").getAsString();
        
         
    JsonObject taxiObject = rootObject.getAsJsonObject("taxi");
    double openingFee = taxiObject.get("openingFee").getAsDouble();
    double costPerKm = taxiObject.get("costPerKm").getAsDouble();
    Taxi taxi = new Taxi(openingFee, costPerKm);
    
    vehicles.put("TAXI", taxi);
    vehicles.put("BUS", new Bus());
    vehicles.put("TRAM", new Tram());
    
    this.taxi = taxi; 
    
        
        JsonArray stopsArray = rootObject.getAsJsonArray("duraklar");
        
        for (JsonElement stopElement : stopsArray) {
            JsonObject stopObject = stopElement.getAsJsonObject();
            
            String id = stopObject.get("id").getAsString();
            String name = stopObject.get("name").getAsString();
            String type = stopObject.get("type").getAsString();
            double lat = stopObject.get("lat").getAsDouble();
            double lon = stopObject.get("lon").getAsDouble();
            boolean isLastStop = stopObject.get("sonDurak").getAsBoolean();
            
            Stop stop = new Stop(id, name, type, lat, lon, isLastStop);
            stops.put(id, stop);
        }
        
        for (JsonElement stopElement : stopsArray) {
            JsonObject stopObject = stopElement.getAsJsonObject();
            String stopId = stopObject.get("id").getAsString();
            Stop stop = stops.get(stopId);
            
            // Sonraki durakları ekle
            if (stopObject.has("nextStops") && !stopObject.get("nextStops").isJsonNull()) {
                JsonArray nextStopsArray = stopObject.getAsJsonArray("nextStops");
                Stop.NextStop[] nextStops = new Stop.NextStop[nextStopsArray.size()];
                
                for (int j = 0; j < nextStopsArray.size(); j++) {
                    JsonObject nextStopObject = nextStopsArray.get(j).getAsJsonObject();
                    
                    String nextStopId = nextStopObject.get("stopId").getAsString();
                    double distance = nextStopObject.get("mesafe").getAsDouble();
                    int duration = nextStopObject.get("sure").getAsInt();
                    double fare = nextStopObject.get("ucret").getAsDouble();
                    
                    nextStops[j] = new Stop.NextStop(nextStopId, distance, duration, fare);
                }
                
                stop.setNextStops(nextStops);
            } else {
                stop.setNextStops(new Stop.NextStop[0]);
            }
            
            // Aktarma bilgisini ekle
            if (stopObject.has("transfer") && !stopObject.get("transfer").isJsonNull()) {
                JsonObject transferObject = stopObject.getAsJsonObject("transfer");
                
                String transferStopId = transferObject.get("transferStopId").getAsString();
                int transferDuration = transferObject.get("transferSure").getAsInt();
                double transferFare = transferObject.get("transferUcret").getAsDouble();
                
                Transfer transfer = new Transfer(transferStopId, transferDuration, transferFare);
                stop.setTransfer(transfer);
            }
        }
        
        // DistanceCalculator'a stops map'ini aktar
        if (distanceCalculator instanceof HaversineDistanceCalculator) {
            ((HaversineDistanceCalculator) distanceCalculator).setStops(stops);
        }
    }
    private Route createWalkingRoute(double startLat, double startLon, double destLat, double destLon) {
    double distance = calculateDistance(startLat, startLon, destLat, destLon);
    int duration = calculateWalkingDuration(distance);
    
    Route route = new Route(Route.WALKING_ONLY);
    
    if (distance < 0.01) {
        duration = 0;
    }
    
    Route.RoutePart walkingPart = new Route.RoutePart(
        Route.WALKING,
        null, "Başlangıç Noktası",
        null, "Hedef Noktası",
        distance, duration, 0.0
    );
    route.addRoutePart(walkingPart);
    
    return route;
}
  
    public Stop findNearestStop(double lat, double lon) {
        Stop nearestStop = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Stop stop : stops.values()) {
            double distance = stop.calculateDistanceTo(lat, lon);
            if (distance < minDistance) {
                minDistance = distance;
                nearestStop = stop;
            }
        }
        
        return nearestStop;
    }
    
    public Stop findNearestBusStop(double lat, double lon) {
        Stop nearestStop = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Stop stop : stops.values()) {
            if ("bus".equals(stop.getType())) {
                double distance = stop.calculateDistanceTo(lat, lon);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestStop = stop;
                }
            }
        }
        
        return nearestStop;
    }
    
    public Stop findNearestTramStop(double lat, double lon) {
        Stop nearestStop = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Stop stop : stops.values()) {
            if ("tram".equals(stop.getType())) {
                double distance = stop.calculateDistanceTo(lat, lon);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestStop = stop;
                }
            }
        }
        
        return nearestStop;
    }

public List<Route> calculateRoutes(double startLat, double startLon, double destLat, double destLon,
                              String startStopId, String destStopId) throws IOException {
    List<Route> allRoutes = new ArrayList<>();
    
    Stop selectedStartStop = null;
    Stop selectedDestStop = null;
    
    if (startStopId != null && !startStopId.isEmpty()) {
        selectedStartStop = stops.get(startStopId);
    }
    
    if (destStopId != null && !destStopId.isEmpty()) {
        selectedDestStop = stops.get(destStopId);
    }
    
    System.out.println("Rota hesaplama başladı: " + startLat + "," + startLon + " -> " + destLat + "," + destLon);
    if (selectedStartStop != null) {
        System.out.println("Başlangıç durağı: " + selectedStartStop.getName());
    }
    if (selectedDestStop != null) {
        System.out.println("Hedef durağı: " + selectedDestStop.getName());
    }

    Route walkingRoute = calculateWalkingOnlyRoute(startLat, startLon, destLat, destLon);
    if (walkingRoute != null) {
        allRoutes.add(walkingRoute);
        System.out.println("Yürüme rotası bulundu: " + walkingRoute.getTotalDistance() + " km");
    }
    
    Route taxiRoute = calculateTaxiOnlyRoute(startLat, startLon, destLat, destLon);
    if (taxiRoute != null) {
        allRoutes.add(taxiRoute);
        System.out.println("Taksi rotası bulundu: " + taxiRoute.getTotalDistance() + " km");
    }
    
    Route busOnlyRoute = calculateBusOnlyRoute(startLat, startLon, destLat, destLon, selectedStartStop, selectedDestStop);
    if (busOnlyRoute != null) {
        allRoutes.add(busOnlyRoute);
        System.out.println("Sadece otobüs rotası bulundu: " + busOnlyRoute.getTotalDistance() + " km");
    }
    
    Route tramOnlyRoute = calculateTramOnlyRoute(startLat, startLon, destLat, destLon, selectedStartStop, selectedDestStop);
    if (tramOnlyRoute != null) {
        allRoutes.add(tramOnlyRoute);
        System.out.println("Sadece tramvay rotası bulundu: " + tramOnlyRoute.getTotalDistance() + " km");
    }
    
    Route taxiBusRoute = calculateTaxiBusRoute(startLat, startLon, destLat, destLon);
    if (taxiBusRoute != null) {
        allRoutes.add(taxiBusRoute);
        System.out.println("Taksi + Otobüs rotası bulundu: " + taxiBusRoute.getTotalDistance() + " km");
    }
    
    Route taxiTramRoute = calculateTaxiTramRoute(startLat, startLon, destLat, destLon);
    if (taxiTramRoute != null) {
        allRoutes.add(taxiTramRoute);
        System.out.println("Taksi + Tramvay rotası bulundu: " + taxiTramRoute.getTotalDistance() + " km");
    }
    
    Route busTramRoute = calculateBusTramRoute(startLat, startLon, destLat, destLon);
    if (busTramRoute != null) {
        allRoutes.add(busTramRoute);
        System.out.println("Otobüs + Tramvay rotası bulundu: " + busTramRoute.getTotalDistance() + " km");
    }
    
    List<Route> filtered = filterAndSortRoutes(allRoutes);
    
    System.out.println("Toplam " + filtered.size() + " rota bulundu");
    return filtered;
}

private List<Route> filterAndSortRoutes(List<Route> routes) {
    // Rota imzasına göre en iyi rotayı saklamak için Map kullan
    // Key: Rota İmzası (örn: "BUS:Stop1->Stop2->Stop3|TRAM:StopA->StopB")
    // Value: O imzaya ait en iyi Route nesnesi
    Map<String, Route> uniqueBestRoutes = new LinkedHashMap<>(); 

    System.out.println("Filtreleniyor... Toplam rota sayısı: " + routes.size());

    for (Route route : routes) {
        if (route == null || route.getRouteParts().isEmpty()) {
            System.out.println("Null veya boş rota atlanıyor.");
            continue;
        }

        // Geçerlilik kontrolleri (Mesafe, Kısa mesafe karmaşıklığı)
        if (route.getTotalDistance() <= 0.01) {
            if (!(route.getRouteParts().size() == 1 && route.getRouteParts().get(0).getType().equals(Route.WALKING))) {
                System.out.println("Filtrelendi (Mesafe <= 0.01): " + route.getRouteType());
                continue;
            }
        }
        if (route.getTotalDistance() <= WALKING_THRESHOLD) {
            String type = route.getRouteType();
            if (!type.equals(Route.WALKING_ONLY) && !type.equals(Route.ONLY_TAXI)) {
                System.out.println("Filtrelendi (Kısa mesafe karmaşık rota: " + type + ")");
                continue;
            }
        }

        // Rota İmzasını Oluştur
        String signature = generateRouteSignature(route);
        System.out.println("Kontrol ediliyor: Type=" + route.getRouteType() + ", Signature='" + signature + "', Süre=" + route.getTotalDuration());

        // Eğer bu imza daha önce eklenmediyse veya şimdiki rota daha iyiyse (daha kısa süreli) ekle/güncelle
        Route existingRoute = uniqueBestRoutes.get(signature);
        if (existingRoute == null || route.getTotalDuration() < existingRoute.getTotalDuration()) {
            if (existingRoute != null) {
                System.out.println("  -> Daha iyi rota bulundu (Eski Süre: " + existingRoute.getTotalDuration() + "), güncelleniyor: " + signature);
            } else {
                System.out.println("  -> Yeni benzersiz imza, ekleniyor: " + signature);
            }
            uniqueBestRoutes.put(signature, route);
        } else {
            System.out.println("  -> Bu imzaya ait daha iyi bir rota zaten var, atlanıyor: " + signature);
        }
    }

    // Map'teki rotaları List'e çevir
    List<Route> validRoutes = new ArrayList<>(uniqueBestRoutes.values());
    System.out.println("\nFiltreleme sonrası rota sayısı: " + validRoutes.size());
    System.out.println("-----------------------------------------");

    // Rotaları sırala (önce süre, sonra mesafe)
    Collections.sort(validRoutes, Comparator
            .comparingInt(Route::getTotalDuration)
            .thenComparingDouble(Route::getTotalDistance));

    return validRoutes;
}

/**
 * Bir rotanın toplu taşıma duraklarına dayalı benzersiz bir imzasını oluşturur.
 * Başlangıç/bitiş yürüme ve taksi kısımlarını genellikle göz ardı eder.
 */
private String generateRouteSignature(Route route) {
    StringBuilder signature = new StringBuilder();
    String lastTransportType = "";

    // Sadece yürüme veya sadece taksi rotaları için özel imza
    if (Route.WALKING_ONLY.equals(route.getRouteType()) || Route.ONLY_TAXI.equals(route.getRouteType())) {
        return route.getRouteType() + "_" + Math.round(route.getTotalDistance() * 10); // Mesafeyi de ekle
    }

    boolean firstPart = true;
    for (Route.RoutePart part : route.getRouteParts()) {
        String partType = part.getType();

        // Sadece Otobüs ve Tramvay duraklarını imzaya dahil et
        if (Route.BUS.equals(partType) || Route.TRAM.equals(partType)) {
             // Farklı bir taşıma türüne geçildiyse veya ilk parça ise türü ekle
            if (!partType.equals(lastTransportType)) {
                if (!firstPart) {
                    signature.append("|"); // Türler arası ayraç
                }
                signature.append(partType).append(":");
                lastTransportType = partType;
            }
            // Durak ID'lerini ekle (null değilse)
            if (part.getFromId() != null) {
                 if (!signature.toString().endsWith(":")) signature.append("->");
                 signature.append(part.getFromId());
            }
            if (part.getToId() != null) {
                 if (!signature.toString().endsWith(":")) signature.append("->");
                 signature.append(part.getToId());
            }
            firstPart = false;
        } else if (Route.TRANSFER.equals(partType)) {
             // Transferi özel bir işaretleyici ile ekle
             if (!firstPart) signature.append("|");
             signature.append("TRANSFER:"+part.getFromId()+"->"+part.getToId());
             lastTransportType = "TRANSFER"; // Bir sonraki taşıma türünün yazılması için
             firstPart = false;
        }
       
    }
    
    if (signature.length() == 0) {
        return route.getRouteType() + "_Fallback_" + Math.round(route.getTotalDistance() * 10);
    }

    return signature.toString();
}
public Route calculateWalkingOnlyRoute(double startLat, double startLon, double destLat, double destLon) {
    double distance = calculateDistance(startLat, startLon, destLat, destLon);
    
    if (distance > WALKING_THRESHOLD) {
        return null; 
    }
    
    int duration = calculateWalkingDuration(distance);
    
    if (distance < 0.01) {
        duration = 0;
    }
    
    Route route = new Route(Route.WALKING_ONLY);
    
    Route.RoutePart walkingPart = new Route.RoutePart(
        Route.WALKING,
        null, "Başlangıç Noktası",
        null, "Hedef Noktası",
        distance, duration, 0.0 
    );
    route.addRoutePart(walkingPart);
    
    return route;
}

public Route calculateBusOnlyRoute(double startLat, double startLon, double destLat, double destLon,
                               Stop selectedStartStop, Stop selectedDestStop) {
    Stop nearestBusStop = selectedStartStop != null ? 
            selectedStartStop : findNearestBusStop(startLat, startLon);
    
    Stop nearestDestBusStop = selectedDestStop != null ?
            selectedDestStop : findNearestBusStop(destLat, destLon);
    
    if (nearestBusStop == null || nearestDestBusStop == null) {
        return null; 
    }
    
   
    double distanceToStart = selectedStartStop != null ? 0 :
            nearestBusStop.calculateDistanceTo(startLat, startLon);
            
    double distanceFromEnd = selectedDestStop != null ? 0 :
            nearestDestBusStop.calculateDistanceTo(destLat, destLon);
    
    if ((selectedStartStop == null && !shouldWalk(distanceToStart)) || 
        (selectedDestStop == null && !shouldWalk(distanceFromEnd))) {
        return null; 
    }
    
    List<Stop> path = findShortestPath(nearestBusStop.getId(), nearestDestBusStop.getId(), "bus");
    
    if (path.isEmpty()) {
        return null;
    }
    
    boolean hasWalkingToStart = distanceToStart > 0 && shouldWalk(distanceToStart);
    boolean hasTaxiToStart = distanceToStart > 0 && !shouldWalk(distanceToStart);
    
    boolean hasWalkingFromEnd = distanceFromEnd > 0 && shouldWalk(distanceFromEnd);
    boolean hasTaxiFromEnd = distanceFromEnd > 0 && !shouldWalk(distanceFromEnd);
    
    String routeType;
    if (hasTaxiToStart || hasTaxiFromEnd) {
        routeType = Route.TAXI_BUS; 
    } else if (hasWalkingToStart || hasWalkingFromEnd) {
        routeType = Route.WALKING_BUS; 
    } else {
        routeType = Route.ONLY_BUS; 
    }
    
   int passengerType = 0; 
    Route route = new Route(routeType);
    
    if (distanceToStart > 0) {
        if (shouldWalk(distanceToStart)) {
            int walkingDuration = calculateWalkingDuration(distanceToStart);
            Route.RoutePart walkingPart = new Route.RoutePart(
                Route.WALKING,
                null, "Başlangıç Noktası",
                nearestBusStop.getId(), nearestBusStop.getName(),
                distanceToStart, walkingDuration, 0.0 
            );
            route.addRoutePart(walkingPart);
        } else {
            int taxiDuration = taxi.calculateDuration(distanceToStart);
            double taxiFare = taxi.calculateFare(distanceToStart);
            
            Route.RoutePart taxiPart = new Route.RoutePart(
                Route.TAXI,
                null, "Başlangıç Noktası",
                nearestBusStop.getId(), nearestBusStop.getName(),
                distanceToStart, taxiDuration, taxiFare
            );
            route.addRoutePart(taxiPart);
        }
    }
    
    for (int i = 0; i < path.size() - 1; i++) {
        Stop currentStop = path.get(i);
        Stop nextStop = path.get(i + 1);
        
        // İki  arasındaki bağlantıyı bul
        Stop.NextStop connection = findConnection(currentStop, nextStop.getId());
        if (connection != null) {
            Route.RoutePart busPart = new Route.RoutePart(
                Route.BUS,
                currentStop.getId(), currentStop.getName(),
                nextStop.getId(), nextStop.getName(),
                connection.getDistance(), connection.getDuration(), connection.getFare()
            );
            route.addRoutePart(busPart);
        }
    }
    
    if (distanceFromEnd > 0) {
        if (shouldWalk(distanceFromEnd)) {
            int walkingDuration = calculateWalkingDuration(distanceFromEnd);
            Route.RoutePart walkingPart = new Route.RoutePart(
                Route.WALKING,
                nearestDestBusStop.getId(), nearestDestBusStop.getName(),
                null, "Hedef Noktası",
                distanceFromEnd, walkingDuration, 0.0 
            );
            route.addRoutePart(walkingPart);
        } else {
            int taxiDuration = taxi.calculateDuration(distanceFromEnd);
            double taxiFare = taxi.calculateFare(distanceFromEnd);
            
            Route.RoutePart taxiPart = new Route.RoutePart(
                Route.TAXI,
                nearestDestBusStop.getId(), nearestDestBusStop.getName(),
                null, "Hedef Noktası",
                distanceFromEnd, taxiDuration, taxiFare
            );
            route.addRoutePart(taxiPart);
        }
    }
    
    return route;
}

    
    public Route calculateTramOnlyRoute(double startLat, double startLon, double destLat, double destLon,
                                 Stop selectedStartStop, Stop selectedDestStop) {
    Stop nearestTramStop = selectedStartStop != null ? 
            selectedStartStop : findNearestTramStop(startLat, startLon);
    
    Stop nearestDestTramStop = selectedDestStop != null ?
            selectedDestStop : findNearestTramStop(destLat, destLon);
    
    if (nearestTramStop == null || nearestDestTramStop == null) {
        return null; 
    }
    
    double distanceToStart = selectedStartStop != null ? 0 :
            nearestTramStop.calculateDistanceTo(startLat, startLon);
            
    double distanceFromEnd = selectedDestStop != null ? 0 :
            nearestDestTramStop.calculateDistanceTo(destLat, destLon);
    
    if ((selectedStartStop == null && distanceToStart > WALKING_THRESHOLD) || 
        (selectedDestStop == null && distanceFromEnd > WALKING_THRESHOLD)) {
        return null;
    }
    
    List<Stop> tramPath = findShortestPath(nearestTramStop.getId(), nearestDestTramStop.getId(), "tram");
    
    if (tramPath.isEmpty()) {
        return null;
    }
    
    Route route = new Route(Route.ONLY_TRAM);
    
    if (distanceToStart > 0) {
        int walkingDuration = calculateWalkingDuration(distanceToStart);
        Route.RoutePart walkingPart = new Route.RoutePart(
            Route.WALKING,
            null, "Başlangıç Noktası",
            nearestTramStop.getId(), nearestTramStop.getName(),
            distanceToStart, walkingDuration, 0.0 
        );
        route.addRoutePart(walkingPart);
    }
    
    for (int i = 0; i < tramPath.size() - 1; i++) {
        Stop currentStop = tramPath.get(i);
        Stop nextStop = tramPath.get(i + 1);
        
        Stop.NextStop connection = findConnection(currentStop, nextStop.getId());
        if (connection != null) {
            Route.RoutePart tramPart = new Route.RoutePart(
                Route.TRAM,
                currentStop.getId(), currentStop.getName(),
                nextStop.getId(), nextStop.getName(),
                connection.getDistance(), connection.getDuration(), connection.getFare()
            );
            route.addRoutePart(tramPart);
        }
    }
    
    if (distanceFromEnd > 0) {
        int walkingDuration = calculateWalkingDuration(distanceFromEnd);
        Route.RoutePart walkingPart = new Route.RoutePart(
            Route.WALKING,
            nearestDestTramStop.getId(), nearestDestTramStop.getName(),
            null, "Hedef Noktası",
            distanceFromEnd, walkingDuration, 0.0 
        );
        route.addRoutePart(walkingPart);
    }
    
    return route;
}
    
    public Route calculateBusTramRoute(double startLat, double startLon, double destLat, double destLon) {
        Stop nearestBusStop = findNearestBusStop(startLat, startLon);
        Stop nearestDestTramStop = findNearestTramStop(destLat, destLon);
        
        if (nearestBusStop == null || nearestDestTramStop == null) {
            return null; 
        }
        
        double distanceToStart = nearestBusStop.calculateDistanceTo(startLat, startLon);
        double distanceFromEnd = nearestDestTramStop.calculateDistanceTo(destLat, destLon);
        
        if (distanceToStart > WALKING_THRESHOLD || distanceFromEnd > WALKING_THRESHOLD) {
            return null;
        }
        
        List<DistanceCalculator.TransferPoint> transferPoints = findPossibleTransferPoints();
        
        if (transferPoints.isEmpty()) {
            return null; 
        }
        
        Route bestRoute = null;
        double bestTotalScore = Double.MAX_VALUE; 
        
        for (DistanceCalculator.TransferPoint transferPoint : transferPoints) {
            Stop busStop = stops.get(transferPoint.busStopId);
            Stop tramStop = stops.get(transferPoint.tramStopId);
            
            if (busStop == null || tramStop == null) {
                continue; 
            }
            
            Transfer busToTramTransfer = null;
            if (busStop.getTransfer() != null && busStop.getTransfer().getTransferStopId().equals(tramStop.getId())) {
                busToTramTransfer = busStop.getTransfer();
            }
            
            if (busToTramTransfer == null) {
                System.out.println("Uyarı: " + busStop.getName() + " -> " + tramStop.getName() + " aktarması için transfer bilgisi bulunamadı.");
                continue;
            }
            
            List<Stop> busPath = findShortestPath(
                    nearestBusStop.getId(), transferPoint.busStopId, "bus");
            
            List<Stop> tramPath = findShortestPath(
                    transferPoint.tramStopId, nearestDestTramStop.getId(), "tram");
            
            if (!busPath.isEmpty() && !tramPath.isEmpty()) {
                Route route = new Route(Route.BUS_TRAM);
                
                if (distanceToStart > 0) {
                    int walkingDuration = calculateWalkingDuration(distanceToStart);
                    Route.RoutePart walkingPart = new Route.RoutePart(
                        Route.WALKING,
                        null, "Başlangıç Noktası",
                        nearestBusStop.getId(), nearestBusStop.getName(),
                        distanceToStart, walkingDuration, 0.0 
                    );
                    route.addRoutePart(walkingPart);
                }
                
                for (int i = 0; i < busPath.size() - 1; i++) {
                    Stop currentStop = busPath.get(i);
                    Stop nextStop = busPath.get(i + 1);
                    
                    Stop.NextStop connection = findConnection(currentStop, nextStop.getId());
                    if (connection != null) {
                        Route.RoutePart busPart = new Route.RoutePart(
                            Route.BUS,
                            currentStop.getId(), currentStop.getName(),
                            nextStop.getId(), nextStop.getName(),
                            connection.getDistance(), connection.getDuration(), connection.getFare()
                        );
                        route.addRoutePart(busPart);
                    }
                }
                
                Route.RoutePart transferPart = new Route.RoutePart(
                    Route.TRANSFER,
                    busStop.getId(), busStop.getName(),
                    tramStop.getId(), tramStop.getName(),
                    0.0, 
                    busToTramTransfer.getTransferDuration(),
                    busToTramTransfer.getTransferFare()
                );
                route.addRoutePart(transferPart);
                
                for (int i = 0; i < tramPath.size() - 1; i++) {
                    Stop currentStop = tramPath.get(i);
                    Stop nextStop = tramPath.get(i + 1);
                    
                    Stop.NextStop connection = findConnection(currentStop, nextStop.getId());
                    if (connection != null) {
                        Route.RoutePart tramPart = new Route.RoutePart(
                            Route.TRAM,
                            currentStop.getId(), currentStop.getName(),
                            nextStop.getId(), nextStop.getName(),
                            connection.getDistance(), connection.getDuration(), connection.getFare()
                        );
                        route.addRoutePart(tramPart);
                    }
                }
                
                if (distanceFromEnd > 0) {
                    int walkingDuration = calculateWalkingDuration(distanceFromEnd);
                    Route.RoutePart walkingPart = new Route.RoutePart(
                        Route.WALKING,
                        nearestDestTramStop.getId(), nearestDestTramStop.getName(),
                        null, "Hedef Noktası",
                        distanceFromEnd, walkingDuration, 0.0 
                    );
                    route.addRoutePart(walkingPart);
                }
                
                double timeWeight = 0.6;
                double fareWeight = 0.4;
                double totalScore = timeWeight * route.getTotalDuration() + fareWeight * route.getTotalFare();
                
                if (bestRoute == null || totalScore < bestTotalScore) {
                    bestRoute = route;
                    bestTotalScore = totalScore;
                }
            }
        }
        
        return bestRoute;
    }
    
    public Route calculateTaxiBusRoute(double startLat, double startLon, double destLat, double destLon) {
        Stop nearestBusStop = findNearestBusStop(startLat, startLon);
        Stop nearestDestBusStop = findNearestBusStop(destLat, destLon);
        
        if (nearestBusStop == null || nearestDestBusStop == null) {
            return null;
        }
        
        double distanceToStart = nearestBusStop.calculateDistanceTo(startLat, startLon);
        double distanceFromEnd = nearestDestBusStop.calculateDistanceTo(destLat, destLon);
        
        boolean needTaxiToStart = distanceToStart > WALKING_THRESHOLD;
        boolean needTaxiFromEnd = distanceFromEnd > WALKING_THRESHOLD;
        
        if (needTaxiToStart && needTaxiFromEnd) {
            return null;
        }
        
        List<Stop> busPath = findShortestPath(nearestBusStop.getId(), nearestDestBusStop.getId(), "bus");
        
        if (busPath.isEmpty()) {
            return null;
        }
        
        Route route = new Route(Route.TAXI_BUS);
        
        if (distanceToStart > 0) {
            if (needTaxiToStart) {
                int taxiDuration = taxi.calculateDuration(distanceToStart);
                double taxiFare = taxi.calculateFare(distanceToStart);
                
                Route.RoutePart taxiPart = new Route.RoutePart(
                    Route.TAXI,
                    null, "Başlangıç Noktası",
                    nearestBusStop.getId(), nearestBusStop.getName(),
                    distanceToStart, taxiDuration, taxiFare
                );
                route.addRoutePart(taxiPart);
            } else {
                int walkingDuration = calculateWalkingDuration(distanceToStart);
                Route.RoutePart walkingPart = new Route.RoutePart(
                    Route.WALKING,
                    null, "Başlangıç Noktası",
                    nearestBusStop.getId(), nearestBusStop.getName(),
                    distanceToStart, walkingDuration, 0.0
                );
                route.addRoutePart(walkingPart);
            }
        }
        
        for (int i = 0; i < busPath.size() - 1; i++) {
            Stop currentStop = busPath.get(i);
            Stop nextStop = busPath.get(i + 1);
            
            Stop.NextStop connection = findConnection(currentStop, nextStop.getId());
            if (connection != null) {
                Route.RoutePart busPart = new Route.RoutePart(
                    Route.BUS,
                    currentStop.getId(), currentStop.getName(),
                    nextStop.getId(), nextStop.getName(),
                    connection.getDistance(), connection.getDuration(), connection.getFare()
                );
                route.addRoutePart(busPart);
            }
        }
        
        if (distanceFromEnd > 0) {
            if (needTaxiFromEnd) {
                int taxiDuration = taxi.calculateDuration(distanceFromEnd);
                double taxiFare = taxi.calculateFare(distanceFromEnd);
                
                Route.RoutePart taxiPart = new Route.RoutePart(
                    Route.TAXI,
                    nearestDestBusStop.getId(), nearestDestBusStop.getName(),
                    null, "Hedef Noktası",
                    distanceFromEnd, taxiDuration, taxiFare
                );
                route.addRoutePart(taxiPart);
            } else {
                int walkingDuration = calculateWalkingDuration(distanceFromEnd);
                Route.RoutePart walkingPart = new Route.RoutePart(
                    Route.WALKING,
                    nearestDestBusStop.getId(), nearestDestBusStop.getName(),
                    null, "Hedef Noktası",
                    distanceFromEnd, walkingDuration, 0.0
                );
                route.addRoutePart(walkingPart);
            }
        }
        
        return route;
    }
    
    public Route calculateTaxiTramRoute(double startLat, double startLon, double destLat, double destLon) {
        Stop nearestTramStop = findNearestTramStop(startLat, startLon);
        Stop nearestDestTramStop = findNearestTramStop(destLat, destLon);
        
        if (nearestTramStop == null || nearestDestTramStop == null) {
            return null;
        }
        
        double distanceToStart = nearestTramStop.calculateDistanceTo(startLat, startLon);
        double distanceFromEnd = nearestDestTramStop.calculateDistanceTo(destLat, destLon);
        
        boolean needTaxiToStart = distanceToStart > WALKING_THRESHOLD;
        boolean needTaxiFromEnd = distanceFromEnd > WALKING_THRESHOLD;
        
        if (needTaxiToStart && needTaxiFromEnd) {
            return null;
        }
        
        List<Stop> tramPath = findShortestPath(nearestTramStop.getId(), nearestDestTramStop.getId(), "tram");
        
        if (tramPath.isEmpty()) {
            return null;
        }
        
        Route route = new Route(Route.TAXI_TRAM);
        
        if (distanceToStart > 0) {
            if (needTaxiToStart) {
                int taxiDuration = taxi.calculateDuration(distanceToStart);
                double taxiFare = taxi.calculateFare(distanceToStart);
                
                Route.RoutePart taxiPart = new Route.RoutePart(
                    Route.TAXI,
                    null, "Başlangıç Noktası",
                    nearestTramStop.getId(), nearestTramStop.getName(),
                    distanceToStart, taxiDuration, taxiFare
                );
                route.addRoutePart(taxiPart);
            } else {
                int walkingDuration = calculateWalkingDuration(distanceToStart);
                Route.RoutePart walkingPart = new Route.RoutePart(
                    Route.WALKING,
                    null, "Başlangıç Noktası",
                    nearestTramStop.getId(), nearestTramStop.getName(),
                    distanceToStart, walkingDuration, 0.0
                );
                route.addRoutePart(walkingPart);
            }
        }
        
        for (int i = 0; i < tramPath.size() - 1; i++) {
            Stop currentStop = tramPath.get(i);
            Stop nextStop = tramPath.get(i + 1);
            
            Stop.NextStop connection = findConnection(currentStop, nextStop.getId());
            if (connection != null) {
                Route.RoutePart tramPart = new Route.RoutePart(
                    Route.TRAM,
                    currentStop.getId(), currentStop.getName(),
                    nextStop.getId(), nextStop.getName(),
                    connection.getDistance(), connection.getDuration(), connection.getFare()
                );
                route.addRoutePart(tramPart);
            }
        }
        
        if (distanceFromEnd > 0) {
            if (needTaxiFromEnd) {
                int taxiDuration = taxi.calculateDuration(distanceFromEnd);
                double taxiFare = taxi.calculateFare(distanceFromEnd);
                
                Route.RoutePart taxiPart = new Route.RoutePart(
                    Route.TAXI,
                    nearestDestTramStop.getId(), nearestDestTramStop.getName(),
                    null, "Hedef Noktası",
                    distanceFromEnd, taxiDuration, taxiFare
                );
                route.addRoutePart(taxiPart);
            } else {
                int walkingDuration = calculateWalkingDuration(distanceFromEnd);
                Route.RoutePart walkingPart = new Route.RoutePart(
                    Route.WALKING,
                    nearestDestTramStop.getId(), nearestDestTramStop.getName(),
                    null, "Hedef Noktası",
                    distanceFromEnd, walkingDuration, 0.0
                );
                route.addRoutePart(walkingPart);
            }
        }
        
        return route;
    }
    
    public Route calculateTaxiOnlyRoute(double startLat, double startLon, double destLat, double destLon) {
    double distance = calculateDistance(startLat, startLon, destLat, destLon);
    
    double fare = taxi.getOpeningFee() + (distance * taxi.getCostPerKm());
    int duration = taxi.calculateDuration(distance);
    
    Route route = new Route(Route.ONLY_TAXI);
    
    Route.RoutePart taxiPart = new Route.RoutePart(
        Route.TAXI,
        null, "Başlangıç Noktası",
        null, "Hedef Noktası",
        distance, duration, fare
    );
    route.addRoutePart(taxiPart);
    
    return route;
}
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return distanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
    }
    
    private List<Stop> findShortestPath(String startId, String endId, String type) {
        return distanceCalculator.findShortestPath(startId, endId, type);
    }
    
    private Stop.NextStop findConnection(Stop fromStop, String toStopId) {
        return distanceCalculator.findConnection(fromStop, toStopId);
    }
    
    private List<DistanceCalculator.TransferPoint> findPossibleTransferPoints() {
        return distanceCalculator.findPossibleTransferPoints();
    }
    
    private int calculateWalkingDuration(double distance) {
        return distanceCalculator.calculateWalkingDuration(distance);
    }
    
    public static void setDistanceCalculator(DistanceCalculator calculator) {
        distanceCalculator = calculator;
        Stop.setDistanceCalculator(calculator);
    }
    
    public Taxi getTaxi() {
        return taxi;
    }
    
    public Map<String, Stop> getStops() {
        return stops;
    }
    
    public Stop getStop(String stopId) {
        return stops.get(stopId);
    }
    
    public String getCityName() {
        return cityName;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void addStop(Stop stop) {
        stops.put(stop.getId(), stop);
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getType(), vehicle);
    }

    public void addRoute(Route route) {
        routes.add(route);
    }
}