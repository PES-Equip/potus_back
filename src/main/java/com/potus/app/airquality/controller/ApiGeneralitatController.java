package com.potus.app.airquality.controller;



import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.model.Regions;
import com.potus.app.airquality.utils.AirQualityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import com.potus.app.airquality.service.AirQualityService;


@RestController
@RequestMapping(value="/api/airquality")
public class ApiGeneralitatController {

    @Autowired
    private Environment env;


    @Value("#{systemEnvironment['GENERALITAT_API_TOKEN']}")
    private String ApiToken;

    @Autowired
    private AirQualityService airQualityService;

    @GetMapping(value = "initialize")
    private List<Region> initializeRegions() {
        return airQualityService.initializeRegions();
    }


    @GetMapping(value = "update")
    private List<Region> updateGasDataRegions(){
        return airQualityService.updateRegionGasData();
    }

    @GetMapping(value = "regions")
    private List<Region> getRegions() {return airQualityService.findAll();}

    @GetMapping(value = "getcodes")
    private Map<Regions, String> getCodes(){
        Map<Regions,String> regionCodes = new HashMap<>();
        for(Regions r : Regions.values()){
            String uri = "https://analisi.transparenciacatalunya.cat/resource/tasf-thgu.json?$$app_token={$$app_token}&nom_comarca={nom_comarca}";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("$$app_token", ApiToken);
            vars.put("nom_comarca", AirQualityUtils.FixRegionName(r));

            Object[] result = restTemplate.getForObject(uri, Object[].class, vars);
            Map<String, String> m = new HashMap<String,String>();
            try {
                m = (Map<String, String>) result[0];
            } catch (Exception e) {
                System.out.println("No data for that comarca");
            }

            regionCodes.put(r, m.get("codi_comarca"));
        }
        return regionCodes;
    }



}
