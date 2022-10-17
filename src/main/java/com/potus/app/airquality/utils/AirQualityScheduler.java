package com.potus.app.airquality.utils;

import com.potus.app.airquality.service.AirQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AirQualityScheduler {

    @Autowired
    AirQualityService airQualityService;

    @Scheduled(cron = "0 30 1 ? * *")
    public void cronUpdateRegions(){
        airQualityService.UpdateRegionGasData();
    }
}
