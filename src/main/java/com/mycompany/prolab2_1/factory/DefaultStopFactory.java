package com.mycompany.prolab2_1.factory;

import com.mycompany.prolab2_1.Stop;


public class DefaultStopFactory implements StopFactory {
    @Override
    public Stop createStop(String id, String name, String type, double lat, double lon, boolean isLastStop) {
        return new Stop(id, name, type, lat, lon, isLastStop);
    }
} 