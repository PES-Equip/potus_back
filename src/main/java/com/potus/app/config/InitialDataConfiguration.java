package com.potus.app.config;

import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.potus.model.Modifier;
import com.potus.app.potus.service.ModifierService;
import com.potus.app.potus.utils.ModifierUtils;
import com.potus.app.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("!test")
public class InitialDataConfiguration {

    Logger logger = LoggerFactory.getLogger(InitialDataConfiguration.class);

    @Autowired
    AirQualityService airQualityService;

    @Autowired
    ModifierService modifierService;

    @Autowired
    UserService userService;

    @Bean
    CommandLineRunner runner(){
        userService.addAdmins();
        List<Region> regions = airQualityService.findAll();
        List<Modifier> modifiers = modifierService.findAll();

        if(regions.size() == 0) {
            logger.info("Initializing regions");
            airQualityService.initializeRegions();
            airQualityService.updateRegionGasData();
        }

        if (modifiers.size() != ModifierUtils.MODIFIERS_QUANTITY) {
            logger.info("Adding modifiers");
            modifierService.initializeModifiers();
            
        }

        return null;
    }
}
