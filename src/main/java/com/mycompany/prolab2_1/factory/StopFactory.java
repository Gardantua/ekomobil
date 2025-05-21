package com.mycompany.prolab2_1.factory;

import com.mycompany.prolab2_1.Stop;

public interface StopFactory {

    Stop createStop(String id, String name, String type, double lat, double lon, boolean isLastStop);
} 