package com.potus.app.airquality.controller;


import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.model.Regions;
import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.airquality.utils.AirQualityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import static com.potus.app.exception.GeneralExceptionMessages.UNAUTHENTICATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;


@RestController
@RequestMapping(value="/api/external/airquality")
@Api(tags= "External",value = "External endpoints")
public class ExternalAPIController {

    @Autowired
    private Environment env;


    @Value("#{systemEnvironment['GENERALITAT_API_TOKEN']}")
    private String ApiToken;

    @Autowired
    private AirQualityService airQualityService;

    @ApiOperation(value = "GET REGIONS AND THEIR GAS DATA")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Regions and their gas data information"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping(value = "regions")
    public List<Region> getRegions() {
        return airQualityService.findAll();
    }

    @ApiOperation(value = "GET A REGION AND THEIR GAS DATA GIVEN A LATITUDE AND LENGTH")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Closed region to the given latitude and length and the gas data information"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping(value = "region")
    public Region getRegion(@RequestParam(value = "latitude") Double latitude , @RequestParam(value = "length") Double length) {
        return airQualityService.getRegion(latitude,length);
    }
}
