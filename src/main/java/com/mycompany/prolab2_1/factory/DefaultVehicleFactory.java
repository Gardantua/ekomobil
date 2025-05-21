package com.mycompany.prolab2_1.factory;

import com.mycompany.prolab2_1.Vehicle;
import com.mycompany.prolab2_1.Bus;
import com.mycompany.prolab2_1.Tram;
import com.mycompany.prolab2_1.Taxi;

public class DefaultVehicleFactory implements VehicleFactory {
    @Override
    public Vehicle createVehicle(String type, double fare) {
        switch (type.toLowerCase()) {
            case "bus":
                return new Bus(fare, fare / 10.0); 
            case "tram":
                return new Tram(fare, fare / 10.0); 
            case "taxi":
                return new Taxi(fare, fare / 2.5); 
            default:
                throw new IllegalArgumentException("Geçersiz araç tipi: " + type);
        }
    }
} 