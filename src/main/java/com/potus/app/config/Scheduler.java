package com.potus.app.config;

import com.potus.app.airquality.service.AirQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    AirQualityService airQualityService;

    @Scheduled(cron = "0 30 1 ? * *")
    public void cronUpdateRegions(){
        airQualityService.updateRegionGasData();
    }
}
