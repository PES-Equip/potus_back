package com.potus.app.potus.controller;

import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.potus.service.PotusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/potus/events")
public class PotusEventsController {

    @Autowired
    PotusService potusService;

    @Autowired
    AirQualityService airQualityService;

}
