package com.potus.app.airquality.controller;


import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.model.Regions;
import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.airquality.utils.AirQualityUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value="/api/external/airquality")
@Api(tags= "External API Controller",value = "External endpoints")
public class ExternalAPIController {

    @Autowired
    private Environment env;


    @Value("#{systemEnvironment['GENERALITAT_API_TOKEN']}")
    private String ApiToken;

    @Autowired
    private AirQualityService airQualityService;


    @GetMapping(value = "regions")
    public List<Region> getRegions() {
        return airQualityService.findAll();
    }

    @GetMapping(value = "region")
    public Region getRegion(@RequestParam(value = "latitude") Double latitude , @RequestParam(value = "length") Double length) {
        return airQualityService.getRegion(latitude,length);
    }
}
