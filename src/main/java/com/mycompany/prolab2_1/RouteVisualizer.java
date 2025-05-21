package com.mycompany.prolab2_1;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

public class RouteVisualizer extends JPanel {
    // Modern renkler
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color GRID_COLOR = new Color(240, 240, 240);
    private static final Color CONNECTION_COLOR = new Color(220, 220, 220);
    private static final Color BUS_COLOR = new Color(52, 152, 219);
    private static final Color TRAM_COLOR = new Color(231, 76, 60);
    private static final Color TAXI_COLOR = new Color(46, 204, 113);
    private static final Color WALKING_COLOR = new Color(241, 196, 15);
    private static final Color TRANSFER_COLOR = new Color(155, 89, 182);
    private static final Color BUS_STOP_COLOR = new Color(52, 152, 219);
    private static final Color TRAM_STOP_COLOR = new Color(231, 76, 60);
    private static final Color SELECTED_STOP_OUTLINE = new Color(44, 62, 80);
    private static final Color LABEL_BACKGROUND = new Color(255, 255, 255, 220);
    
    private Map<String, Stop> allStops;
    private Route selectedRoute;
    private Map<String, Point2D> stopPositions;
    private Font stopFont;
    private Font legendFont;
    
    private double selectedLat = -1;
    private double selectedLon = -1;
    private String selectedPointLabel = "Seçilen Nokta";
    
    private double routeStartLat = -1;
    private double routeStartLon = -1;
    private double routeDestLat = -1;
    private double routeDestLon = -1;
    
    public RouteVisualizer() {
        setPreferredSize(new Dimension(600, 400));
        setBackground(BACKGROUND_COLOR);
        stopPositions = new HashMap<>();
        allStops = new HashMap<>();
        stopFont = new Font("Segoe UI", Font.PLAIN, 11);
        legendFont = new Font("Segoe UI", Font.PLAIN, 12);
    }
    
    public void setAllStops(Map<String, Stop> stops) {
        this.allStops = stops;
        calculateStopPositions();
        repaint();
    }
    
    public void setSelectedRoute(Route route, double startLat, double startLon, double destLat, double destLon) {
        this.selectedRoute = route;
        if (route != null) {
            this.routeStartLat = startLat;
            this.routeStartLon = startLon;
            this.routeDestLat = destLat;
            this.routeDestLon = destLon;
        } else {

            this.routeStartLat = -1;
            this.routeStartLon = -1;
            this.routeDestLat = -1;
            this.routeDestLon = -1;
        }
        repaint();
    }
    
    @Deprecated
    public void setSelectedRoute(Route route) {
     
        this.setSelectedRoute(route, -1, -1, -1, -1);
    }
    
    private void calculateStopPositions() {
        stopPositions.clear();
        
        if (allStops == null || allStops.isEmpty()) {
            return;
        }
        
  
        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;
        
        for (Stop stop : allStops.values()) {
            minLat = Math.min(minLat, stop.getLat());
            maxLat = Math.max(maxLat, stop.getLat());
            minLon = Math.min(minLon, stop.getLon());
            maxLon = Math.max(maxLon, stop.getLon());
        }
        
        int padding = 50;
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;
        
        double latRange = (maxLat - minLat) * 1.5;
        double lonRange = (maxLon - minLon) * 1.5;
        
        double centerLat = (maxLat + minLat) / 2;
        double centerLon = (maxLon + minLon) / 2;
        
        minLat = centerLat - latRange/2;
        maxLat = centerLat + latRange/2;
        minLon = centerLon - lonRange/2;
        maxLon = centerLon + lonRange/2;
        
        for (Stop stop : allStops.values()) {
            double x = padding + (stop.getLon() - minLon) / (maxLon - minLon) * width;
            double y = padding + (maxLat - stop.getLat()) / (maxLat - minLat) * height;
            stopPositions.put(stop.getId(), new Point2D.Double(x, y));
        }
        
        minimizeOverlap();
    }
    
    private void minimizeOverlap() {
        int iterations = 50;
        double minDistance = 40; 
        double repulsionForce = 5;
        
        for (int i = 0; i < iterations; i++) {
            boolean hasOverlap = false;
            
            for (String id1 : stopPositions.keySet()) {
                Point2D p1 = stopPositions.get(id1);
                
                for (String id2 : stopPositions.keySet()) {
                    if (id1.equals(id2)) continue;
                    
                    Point2D p2 = stopPositions.get(id2);
                    double distance = p1.distance(p2);
                    
                    if (distance < minDistance) {
                        hasOverlap = true;
                        
                        double force = (minDistance - distance) / minDistance * repulsionForce;
                        double angle = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
                        
                        double newX1 = p1.getX() - Math.cos(angle) * force;
                        double newY1 = p1.getY() - Math.sin(angle) * force;
                        double newX2 = p2.getX() + Math.cos(angle) * force;
                        double newY2 = p2.getY() + Math.sin(angle) * force;
                        
                        stopPositions.put(id1, new Point2D.Double(newX1, newY1));
                        stopPositions.put(id2, new Point2D.Double(newX2, newY2));
                    }
                }
            }
            
            if (!hasOverlap) break;
        }
    }
    
    public void showCoordinate(double lat, double lon, String label) {
        this.selectedLat = lat;
        this.selectedLon = lon;
        if (label != null && !label.isEmpty()) {
            this.selectedPointLabel = label;
        } else {
            this.selectedPointLabel = "Seçilen Nokta";
        }
        calculateStopPositions(); 
        repaint();
    }
    
    public void clearCoordinate() {
        this.selectedLat = -1;
        this.selectedLon = -1;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
    
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        if (panelWidth <= 0 || panelHeight <= 0) {
            g2d.dispose();
            return; 
        }
        
        if (allStops == null || allStops.isEmpty()) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2d.drawString("Harita verileri yüklenemedi", 20, 30);
            g2d.dispose();
            return;
        }
        
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;
        for (Stop stop : allStops.values()) {
            minLat = Math.min(minLat, stop.getLat());
            maxLat = Math.max(maxLat, stop.getLat());
            minLon = Math.min(minLon, stop.getLon());
            maxLon = Math.max(maxLon, stop.getLon());
        }
        
        if (routeStartLat != -1) {
            minLat = Math.min(minLat, routeStartLat);
            maxLat = Math.max(maxLat, routeStartLat);
            minLon = Math.min(minLon, routeStartLon);
            maxLon = Math.max(maxLon, routeStartLon);
        }
        if (routeDestLat != -1) {
            minLat = Math.min(minLat, routeDestLat);
            maxLat = Math.max(maxLat, routeDestLat);
            minLon = Math.min(minLon, routeDestLon);
            maxLon = Math.max(maxLon, routeDestLon);
        }
        
        double latRange = (maxLat - minLat);
        double lonRange = (maxLon - minLon);
        
        if (latRange == 0) latRange = 0.01;
        if (lonRange == 0) lonRange = 0.01;
        
        latRange *= 1.5; 
        lonRange *= 1.5; 
        
        double centerLat = (maxLat + minLat) / 2;
        double centerLon = (maxLon + minLon) / 2;
        
        final double finalMinLat = centerLat - latRange / 2;
        final double finalMaxLat = centerLat + latRange / 2;
        final double finalMinLon = centerLon - lonRange / 2;
        final double finalMaxLon = centerLon + lonRange / 2;
        
        int padding = 50;
        int mapWidth = panelWidth - 2 * padding;
        int mapHeight = panelHeight - 2 * padding;
        
        java.util.function.BiFunction<Double, Double, Point2D> coordToScreen = (lat, lon) -> {
            if (lat == -1 || lon == -1) return null;
            double x = padding + (lon - finalMinLon) / (finalMaxLon - finalMinLon) * mapWidth;
            double y = padding + (finalMaxLat - lat) / (finalMaxLat - finalMinLat) * mapHeight; 
            return new Point2D.Double(x, y);
        };
        
       
        stopPositions.clear();
        for (Stop stop : allStops.values()) {
            Point2D screenPos = coordToScreen.apply(stop.getLat(), stop.getLon());
            if (screenPos != null) {
                stopPositions.put(stop.getId(), screenPos);
            }
        }
        
        
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, panelWidth, panelHeight);
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(0.5f));
        int gridSize = 30;
        for (int x = 0; x < panelWidth; x += gridSize) g2d.drawLine(x, 0, x, panelHeight);
        for (int y = 0; y < panelHeight; y += gridSize) g2d.drawLine(0, y, panelWidth, y);
        
        // Başlangıç ve bitiş noktaları için ekran pozisyonları
        Point2D startPointPos = coordToScreen.apply(routeStartLat, routeStartLon);
        Point2D endPointPos = coordToScreen.apply(routeDestLat, routeDestLon);
        String startPointName = "Başlangıç Noktası";
        String endPointName = "Hedef Noktası";
        
        
        g2d.setColor(CONNECTION_COLOR);
        g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (Stop stop : allStops.values()) {
            Point2D fromPos = stopPositions.get(stop.getId());
            if (fromPos != null && stop.getNextStops() != null) {
                for (Stop.NextStop next : stop.getNextStops()) {
                    Point2D toPos = stopPositions.get(next.getStopId());
                    if (toPos != null) {
                        g2d.draw(new Line2D.Double(fromPos, toPos));
                    }
                }
            }
        }
        
       
        if (selectedRoute != null) {
            List<Route.RoutePart> parts = selectedRoute.getRouteParts();
            
            for (Route.RoutePart part : parts) {
                Point2D fromPos = null;
                Point2D toPos = null;
                
                
                if (part.getFromId() != null) {
                    fromPos = stopPositions.get(part.getFromId());
                } else if (part.getFromName() != null && part.getFromName().equals(startPointName)) {
                    fromPos = startPointPos; 
                }
                
                
                if (part.getToId() != null) {
                    toPos = stopPositions.get(part.getToId());
                } else if (part.getToName() != null && part.getToName().equals(endPointName)) {
                    toPos = endPointPos; 
                }
                
                if (fromPos != null && toPos != null) {
                    switch (part.getType()) {
                        case Route.BUS: g2d.setColor(BUS_COLOR); break;
                        case Route.TRAM: g2d.setColor(TRAM_COLOR); break;
                        case Route.TAXI: g2d.setColor(TAXI_COLOR); break;
                        case Route.WALKING: g2d.setColor(WALKING_COLOR); break;
                        case Route.TRANSFER: g2d.setColor(TRANSFER_COLOR); break;
                        default: g2d.setColor(Color.GRAY); break; 
                    }
                    
                    g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.draw(new Line2D.Double(fromPos, toPos));
                    drawArrow(g2d, fromPos, toPos);
                    
                    
                    if (part.getType().equals(Route.TAXI) || part.getType().equals(Route.WALKING)) {
                        double dx = toPos.getX() - fromPos.getX();
                        double dy = toPos.getY() - fromPos.getY();
                        double distance = Math.sqrt(dx*dx + dy*dy);
                        
                        int symbolCount = (int)(distance / 20);
                        if (symbolCount > 0) { 
                            double stepX = dx / (symbolCount + 1);
                            double stepY = dy / (symbolCount + 1);
                            
                            for (int i = 1; i <= symbolCount; i++) {
                                double x = fromPos.getX() + stepX * i;
                                double y = fromPos.getY() + stepY * i;
                                
                                if (part.getType().equals(Route.TAXI)) {
                                    g2d.fillOval((int)x - 3, (int)y - 3, 6, 6);
                                } else {
                                    g2d.setStroke(new BasicStroke(2.0f));
                                    g2d.drawLine((int)x - 3, (int)y, (int)x + 3, (int)y);
                                }
                            }
                            g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        }
                    }
                }
            }
            
            if (startPointPos != null) {
                g2d.setColor(new Color(0, 128, 0)); // Yeşil
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.fillOval((int)startPointPos.getX() - 6, (int)startPointPos.getY() - 6, 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.fillOval((int)startPointPos.getX() - 4, (int)startPointPos.getY() - 4, 8, 8);
                g2d.setColor(new Color(0, 128, 0));
                g2d.fillOval((int)startPointPos.getX() - 2, (int)startPointPos.getY() - 2, 4, 4);
                drawLabelWithBackground(g2d, startPointName, (int)startPointPos.getX(), (int)startPointPos.getY() - 15, 4);
            }
            
            if (endPointPos != null) {
                g2d.setColor(new Color(192, 0, 0)); // Kırmızı
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.fillOval((int)endPointPos.getX() - 6, (int)endPointPos.getY() - 6, 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.fillOval((int)endPointPos.getX() - 4, (int)endPointPos.getY() - 4, 8, 8);
                g2d.setColor(new Color(192, 0, 0));
                g2d.fillOval((int)endPointPos.getX() - 2, (int)endPointPos.getY() - 2, 4, 4);
                drawLabelWithBackground(g2d, endPointName, (int)endPointPos.getX(), (int)endPointPos.getY() - 15, 4);
            }
        }
        
        if (selectedLat > 0 && selectedLon > 0) {
            Point2D selectedPointScreen = coordToScreen.apply(selectedLat, selectedLon);
            if (selectedPointScreen != null) {
                g2d.setColor(new Color(255, 69, 0)); 
                g2d.setStroke(new BasicStroke(2.0f));
                int radius = 10;
                g2d.drawOval((int)selectedPointScreen.getX() - radius, (int)selectedPointScreen.getY() - radius, radius*2, radius*2);
                g2d.fillOval((int)selectedPointScreen.getX() - 5, (int)selectedPointScreen.getY() - 5, 10, 10);
                g2d.setColor(new Color(255, 69, 0, 100));
                g2d.drawOval((int)selectedPointScreen.getX() - radius - 3, (int)selectedPointScreen.getY() - radius - 3, (radius+3)*2, (radius+3)*2);
                
                // Etiket
                String coordText = selectedPointLabel + " (" + String.format("%.4f", selectedLat) + ", " + String.format("%.4f", selectedLon) + ")";
                drawLabelWithBackground(g2d, coordText, (int)selectedPointScreen.getX(), (int)selectedPointScreen.getY() - radius - 5, 4);
            }
        }
        
        
        for (Stop stop : allStops.values()) {
            Point2D pos = stopPositions.get(stop.getId());
            if (pos != null) {
                if ("bus".equals(stop.getType())) g2d.setColor(BUS_STOP_COLOR);
                else if ("tram".equals(stop.getType())) g2d.setColor(TRAM_STOP_COLOR);
                else g2d.setColor(Color.DARK_GRAY); 
                
                Ellipse2D.Double circle = new Ellipse2D.Double(pos.getX() - 5, pos.getY() - 5, 10, 10);
                g2d.fill(circle);
                
               
                boolean isInRoute = false;
                if (selectedRoute != null) {
                    for (Route.RoutePart part : selectedRoute.getRouteParts()) {
                        if ((part.getFromId() != null && part.getFromId().equals(stop.getId())) ||
                            (part.getToId() != null && part.getToId().equals(stop.getId()))) {
                            isInRoute = true;
                            break;
                        }
                    }
                }
                
                if (isInRoute) {
                    g2d.setColor(SELECTED_STOP_OUTLINE);
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.draw(circle);
                    Ellipse2D.Double outerCircle = new Ellipse2D.Double(pos.getX() - 8, pos.getY() - 8, 16, 16);
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.fill(outerCircle);
                    g2d.setColor(SELECTED_STOP_OUTLINE);
                    g2d.setStroke(new BasicStroke(1.0f));
                    g2d.draw(outerCircle);
                    
                    drawLabelWithBackground(g2d, stop.getName(), (int)pos.getX(), (int)pos.getY() + 15, 4);
                }
            }
        }
        
        // Lejant çiz
        drawLegend(g2d);
        
        g2d.dispose();
    }
    
    private void drawLabelWithBackground(Graphics2D g2d, String text, int x, int y, int padding) {
        g2d.setFont(stopFont);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int ascent = fm.getAscent();
        
        int rectX = x - textWidth / 2 - padding;
        int rectY = y - ascent + (textHeight - ascent)/2 - padding;
        int rectWidth = textWidth + padding * 2;
        int rectHeight = textHeight + padding;
        
        RoundRectangle2D.Double bg = new RoundRectangle2D.Double(rectX, rectY, rectWidth, rectHeight, 6, 6);
        
        g2d.setColor(LABEL_BACKGROUND);
        g2d.fill(bg);
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(0.5f));
        g2d.draw(bg);
        
        
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x - textWidth / 2, y + (textHeight - ascent)/2 ); 
    }
    
    private void drawArrow(Graphics2D g2d, Point2D from, Point2D to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double angle = Math.atan2(dy, dx);
        
        int arrowSize = 6;
        
        double arrowPos = 0.9; 
        double arrowX = from.getX() + dx * arrowPos;
        double arrowY = from.getY() + dy * arrowPos;
        
        double x1 = arrowX - arrowSize * Math.cos(angle - Math.PI/6);
        double y1 = arrowY - arrowSize * Math.sin(angle - Math.PI/6);
        double x2 = arrowX - arrowSize * Math.cos(angle + Math.PI/6);
        double y2 = arrowY - arrowSize * Math.sin(angle + Math.PI/6);
        
        g2d.fill(new Polygon(
            new int[] {(int)arrowX, (int)x1, (int)x2},
            new int[] {(int)arrowY, (int)y1, (int)y2},
            3
        ));
    }
    
    private void drawLegend(Graphics2D g2d) {
        int legendX = 20;
        int legendY = getHeight() - 150;
        int lineLength = 20;
        int spacing = 22;
        
        g2d.setFont(legendFont);
        
        
        RoundRectangle2D.Double legendBg = new RoundRectangle2D.Double(
            legendX - 10, legendY - 15, 
            160, 150, 
            10, 10);
        g2d.setColor(new Color(255, 255, 255, 230));
        g2d.fill(legendBg);
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(0.5f));
        g2d.draw(legendBg);
        
        // Başlık
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.drawString("Harita Lejantı", legendX, legendY - 5);
        
        g2d.setFont(legendFont);
        
        // Otobüs
        legendY += spacing;
        g2d.setColor(BUS_COLOR);
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new Line2D.Double(legendX, legendY, legendX + lineLength, legendY));
        g2d.fillOval(legendX + lineLength + 5 - 3, legendY - 3, 6, 6);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Otobüs", legendX + lineLength + 13, legendY + 4);
        
        // Tramvay
        legendY += spacing;
        g2d.setColor(TRAM_COLOR);
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new Line2D.Double(legendX, legendY, legendX + lineLength, legendY));
        g2d.fillOval(legendX + lineLength + 5 - 3, legendY - 3, 6, 6);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Tramvay", legendX + lineLength + 13, legendY + 4);
        
        // Taksi
        legendY += spacing;
        g2d.setColor(TAXI_COLOR);
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new Line2D.Double(legendX, legendY, legendX + lineLength, legendY));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Taksi", legendX + lineLength + 13, legendY + 4);
        
        // Yürüme
        legendY += spacing;
        g2d.setColor(WALKING_COLOR);
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new Line2D.Double(legendX, legendY, legendX + lineLength, legendY));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Yürüme", legendX + lineLength + 13, legendY + 4);
        
        // Aktarma
        legendY += spacing;
        g2d.setColor(TRANSFER_COLOR);
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new Line2D.Double(legendX, legendY, legendX + lineLength, legendY));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Aktarma", legendX + lineLength + 13, legendY + 4);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 400);
    }
}
