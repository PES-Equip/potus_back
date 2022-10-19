package com.potus.app.airquality.utils;

import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.service.AirQualityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InitialDataConfiguration {

    Logger logger = LoggerFactory.getLogger(InitialDataConfiguration.class);

    @Autowired
    AirQualityService airQualityService;

    @Bean
    CommandLineRunner runner(){
        List<Region> regions = airQualityService.findAll();

        if(regions.size() == 0) {
            logger.info("Initializing regions");
            airQualityService.initializeRegions();
            airQualityService.updateRegionGasData();
        }
        return null;
    }
}
