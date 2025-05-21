package com.mycompany.prolab2_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Route {
    public static final String ONLY_BUS = "ONLY_BUS";
    public static final String ONLY_TRAM = "ONLY_TRAM";
    public static final String BUS_TRAM = "BUS_TRAM";
    public static final String TAXI_BUS = "TAXI_BUS";
    public static final String TAXI_TRAM = "TAXI_TRAM";
    public static final String ONLY_TAXI = "ONLY_TAXI";
 
    public static final String WALKING_BUS = "WALKING_BUS";
    
    public static final String WALKING = "WALKING";
    public static final String BUS = "BUS";
    public static final String TRAM = "TRAM";
    public static final String TAXI = "TAXI";
    public static final String TRANSFER = "TRANSFER";
    public static final String WALKING_ONLY = "WALKING_ONLY";
    
    private String routeType;
    private List<RoutePart> routeParts;
    private double totalDistance;
    private int totalDuration;
    private double totalFare;
    private int transferCount;
    private double discountedFare;
    private PassengerType passengerType;

    
    
    public void setDiscountedFare(double discountedFare) {
        this.discountedFare = discountedFare;
    }

    public double getDiscountedFare() {
        return discountedFare;
    }
    
    public PassengerType getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(PassengerType passengerType) {
        this.passengerType = passengerType;
    }
    
    public Route(String routeType) {
        this.routeType = routeType;
        this.routeParts = new ArrayList<>();
        this.totalDistance = 0;
        this.totalDuration = 0;
        this.totalFare = 0;
        this.transferCount = 0;
        this.passengerType = null;
    }
    
    public void addRoutePart(RoutePart routePart) {
        routeParts.add(routePart);
        totalDistance += routePart.getDistance();
        totalDuration += routePart.getDuration();
        totalFare += routePart.getFare();
        
        if (TRANSFER.equals(routePart.getType())) {
            transferCount++;
        }
    }
    
    public double getDiscountedTotalFare() {
        double finalFare = totalFare;
        

        if (transferCount > 0) {
            finalFare -= 1.0; 
            if (finalFare < 0) {
                finalFare = 0;
            }
        }
        
        if (HolidayManager.isTodayFreeTransportationDay()) {
            double taxiFare = 0.0;
            for (RoutePart part : routeParts) {
                if (TAXI.equals(part.getType())) {
                    taxiFare += part.getFare(); 
                }
            }
            return taxiFare;
        }
        
        if (passengerType != null) {
            return passengerType.calculateFare(finalFare);
        }
        
        return finalFare;
    }
    
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        
        switch (routeType) {
            case ONLY_BUS:
                summary.append("🚌 Sadece Otobüs");
                break;
            case ONLY_TRAM:
                summary.append("🚋 Sadece Tramvay");
                break;
            case BUS_TRAM:
                summary.append("🚌 + 🚋 Otobüs + Tramvay");
                break;
            case TAXI_BUS:
                summary.append("🚕 + 🚌 Taksi + Otobüs");
                break;
            case TAXI_TRAM:
                summary.append("🚕 + 🚋 Taksi + Tramvay");
                break;
            case ONLY_TAXI:
                summary.append("🚕 Sadece Taksi");
                break;
            case WALKING_ONLY:
                summary.append("🚶 Sadece Yürüme");
                break;
            case WALKING_BUS:
                summary.append("🚶 + 🚌 Yürüme + Otobüs");
                break;
            default:
                summary.append(routeType);
        }
        
        summary.append("\nToplam Mesafe: ").append(String.format("%.1f", totalDistance)).append(" km");
        summary.append("\nToplam Süre: ").append(totalDuration).append(" dakika");
        
        double discountedFare = getDiscountedTotalFare();
        
        if (passengerType != null && passengerType.getType() != PassengerType.REGULAR) {
            summary.append("\nToplam Ücret: ").append(String.format("%.1f", discountedFare)).append(" TL");
            summary.append(" (").append(passengerType.getDiscountDescription());
            
            if (transferCount > 0) {
                summary.append(", Aktarma -1.0 TL");
            }
            
            summary.append(")");
            summary.append("\nNormal Fiyat: ").append(String.format("%.1f", totalFare)).append(" TL");
        } else {
            if (transferCount > 0 && !HolidayManager.isTodayFreeTransportationDay()) {
                summary.append("\nToplam Ücret: ").append(String.format("%.1f", discountedFare)).append(" TL");
                summary.append(" (Aktarma indirimi: -1.0 TL)");
                summary.append("\nNormal Fiyat: ").append(String.format("%.1f", totalFare)).append(" TL");
            } else {
                summary.append("\nToplam Ücret: ").append(String.format("%.1f", discountedFare)).append(" TL");
            }
        }
        
        summary.append("\nAktarma Sayısı: ").append(transferCount);
        
        return summary.toString();
    }
    
    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append(getSummary()).append("\n\nRota Detayları:\n");
        
        for (int i = 0; i < routeParts.size(); i++) {
            RoutePart part = routeParts.get(i);
            
            details.append(i + 1).append(".");
            
            switch (part.getType()) {
                case WALKING:
                    details.append(" 🚶 Yürüme: ");
                    details.append(part.getFromName()).append(" → ");
                    details.append(part.getToName());
                    break;
                
                case BUS:
                    details.append(" 🚌 Otobüs: ");
                    details.append(part.getFromName()).append(" → ");
                    details.append(part.getToName());
                    break;
                
                case TRAM:
                    details.append(" 🚋 Tramvay: ");
                    details.append(part.getFromName()).append(" → ");
                    details.append(part.getToName());
                    break;
                
                case TAXI:
                    details.append(" 🚕 Taksi: ");
                    details.append(part.getFromName()).append(" → ");
                    details.append(part.getToName());
                    break;
                
                case TRANSFER:
                    details.append(" 🔄 Transfer: ");
                    details.append(part.getFromName()).append(" → ");
                    details.append(part.getToName());
                    break;
            }
            
            details.append("\n");
            details.append("   Mesafe: ").append(String.format("%.1f", part.getDistance())).append(" km\n");
            details.append("   Süre: ").append(part.getDuration()).append(" dakika\n");
            
            details.append("   Ücret: ").append(String.format("%.2f", part.getFare())).append(" TL");
            
            if (part.getFare() > 0 && HolidayManager.isTodayFreeTransportationDay() && !TAXI.equals(part.getType())) {
                details.append(" (Bayram günü → 0.00 TL)");
            }
            else if (part.getFare() > 0 && passengerType != null && passengerType.getType() != PassengerType.REGULAR) {
                double partDiscountedFare = 0;
                String description = "";
                
                switch (passengerType.getType()) {
                    case PassengerType.STUDENT:
                        partDiscountedFare = part.getFare() * 0.5; 
                        description = "Öğrenci %50";
                        break;
                    case PassengerType.ELDERLY:
                        partDiscountedFare = part.getFare() * 0.25; 
                        description = "65+ %75";
                        break;
                }
                
                details.append(" (").append(description).append(" → ")
                      .append(String.format("%.2f", partDiscountedFare)).append(" TL)");
            }
            
            details.append("\n");
        }
        
        details.append("\nToplam:\n");
        
        double discountedFare = getDiscountedTotalFare();
        boolean hasTransferDiscount = (transferCount > 0 && !HolidayManager.isTodayFreeTransportationDay());
        
        if (HolidayManager.isTodayFreeTransportationDay()) {
            double taxiFare = 0.0;
            for (RoutePart part : routeParts) {
                if (TAXI.equals(part.getType())) {
                    taxiFare += part.getFare(); 
                }
            }
            
            details.append("• 💰 Ücret: ").append(String.format("%.2f", taxiFare)).append(" TL");
            details.append(" (").append("Bayram günü - Toplu taşıma ücretsiz").append(")");
            details.append(" | Normal: ").append(String.format("%.2f", totalFare)).append(" TL\n");
        } else if (passengerType != null && passengerType.getType() != PassengerType.REGULAR) {
            details.append("• 💰 Ücret: ").append(String.format("%.2f", discountedFare)).append(" TL");
            details.append(" (").append(passengerType.getDiscountDescription());
            
            if (hasTransferDiscount) {
                details.append(", Aktarma -1.0 TL");
            }
            
            details.append(")");
            details.append(" | Normal: ").append(String.format("%.2f", totalFare)).append(" TL\n");
        } else {
            details.append("• 💰 Ücret: ").append(String.format("%.2f", discountedFare)).append(" TL");
            
            if (hasTransferDiscount) {
                details.append(" (Aktarma indirimi: -1.0 TL)");
                details.append(" | Normal: ").append(String.format("%.2f", totalFare)).append(" TL\n");
            } else {
                details.append("\n");
            }
        }
        
        details.append("• ⏱ Süre: ").append(getTotalDuration()).append(" dk\n");
        details.append("• 📏 Mesafe: ").append(String.format("%.1f", getTotalDistance())).append(" km\n");
        
        return details.toString();
    }

    private String findBusLineBetween(String fromId, String toId) {

        if (fromId != null && toId != null) {
            return "500T"; 
        }
        return "";
    }

    private boolean isSameBusLine(String fromId, String toId) {
        String line1 = findBusLineBetween(fromId, toId);
        if (line1 == null || line1.isEmpty()) {
            return false;
        }
        
        for (int i = 0; i < routeParts.size() - 1; i++) {
            RoutePart part = routeParts.get(i);
            if (BUS.equals(part.getType())) {
                String line2 = findBusLineBetween(part.getFromId(), part.getToId());
                if (line1.equals(line2)) {
                    return true;
                }
            }
        }
        return false;
    }


    public String getRouteType() {
        return routeType;
    }
    
    public List<RoutePart> getRouteParts() {
        return routeParts;
    }
    
    public double getTotalDistance() {
        return totalDistance;
    }
    
    public int getTotalDuration() {
        return totalDuration;
    }
    
    public double getTotalFare() {
        return totalFare;
    }
    
    public int getTransferCount() {
        return transferCount;
    }
    
    public static class RoutePart {
        private String type;
        private String fromId;
        private String fromName;
        private String toId;
        private String toName;
        private double distance;
        private int duration;
        private double fare;
        
        public RoutePart(String type, String fromId, String fromName, String toId, String toName, 
                         double distance, int duration, double fare) {
            this.type = type;
            this.fromId = fromId;
            this.fromName = fromName;
            this.toId = toId;
            this.toName = toName;
            this.distance = distance;
            this.duration = duration;
            this.fare = fare;
        }
        
  
        public String getType() {
            return type;
        }
        
        public String getFromId() {
            return fromId;
        }
        
        public String getFromName() {
            return fromName;
        }
        
        public String getToId() {
            return toId;
        }
        
        public String getToName() {
            return toName;
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

        public double getDiscountedFare() {
            // Bayram günüyse ve taksi değilse ücretsiz
            if (HolidayManager.isTodayFreeTransportationDay() && !TAXI.equals(type)) {
                return 0.0;
            }
            return fare;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RoutePart routePart = (RoutePart) o;
            return Double.compare(routePart.distance, distance) == 0 &&
                   duration == routePart.duration &&
                   Double.compare(routePart.fare, fare) == 0 &&
                   Objects.equals(type, routePart.type) &&
                   Objects.equals(fromId, routePart.fromId) &&
                   Objects.equals(fromName, routePart.fromName) &&
                   Objects.equals(toId, routePart.toId) &&
                   Objects.equals(toName, routePart.toName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, fromId, fromName, toId, toName, distance, duration, fare);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;

        return Objects.equals(routeType, route.routeType) &&
               Objects.equals(routeParts, route.routeParts) && 
               Math.round(totalDistance * 10) == Math.round(route.totalDistance * 10) && 
               totalDuration == route.totalDuration;
    }

    @Override
    public int hashCode() {
        
        return Objects.hash(routeType, routeParts, Math.round(totalDistance * 10), totalDuration);
    }
}