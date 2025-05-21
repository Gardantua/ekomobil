package com.mycompany.prolab2_1.factory;

import com.mycompany.prolab2_1.Vehicle;

public interface VehicleFactory {

    Vehicle createVehicle(String type, double fare);
} 