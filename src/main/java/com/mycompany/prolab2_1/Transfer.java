package com.mycompany.prolab2_1;

public class Transfer {
    private String transferStopId;
    private int transferDuration;
    private double transferFare;
    
    public Transfer(String transferStopId, int transferDuration, double transferFare) {
        this.transferStopId = transferStopId;
        this.transferDuration = transferDuration;
        this.transferFare = transferFare;
    }
    
    public String getTransferStopId() {
        return transferStopId;
    }
    
    public int getTransferDuration() {
        return transferDuration;
    }
    
    public double getTransferFare() {
        return transferFare;
    }
}