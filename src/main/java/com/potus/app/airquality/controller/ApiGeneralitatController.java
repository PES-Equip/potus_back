package com.potus.app.airquality.controller;



import com.potus.app.airquality.model.GasRegistry;
import com.potus.app.airquality.model.Gases;
import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.model.Regions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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



    @GetMapping(value = "")
    private List<Object> getQualityAir(){
        String uri = "https://analisi.transparenciacatalunya.cat/resource/tasf-thgu.json?$$app_token={$$app_token}";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> vars = new HashMap<String, String>();
        vars.put("$$app_token", ApiToken);

        Object[] result = restTemplate.getForObject(uri, Object[].class, vars);
        return Arrays.asList(result);
    }

    @GetMapping(value = "municipi")
    private List<Object> getQualityAir_municipi(@RequestParam(required = true) String municipi){
        String uri = "https://analisi.transparenciacatalunya.cat/resource/tasf-thgu.json?$$app_token={$$app_token}&municipi={municipi}";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> vars = new HashMap<String, String>();


        vars.put("$$app_token", ApiToken);
        vars.put("municipi", municipi);

        Object[] result = restTemplate.getForObject(uri, Object[].class, vars);
        return Arrays.asList(result);
    }



    @GetMapping(value = "aux")
    private List<Region> aux() {
        airQualityService.InitializeGases();

        return airQualityService.findAll();
    }

    @GetMapping(value = "find")
    private List<Region> find() {
        return airQualityService.findAll();
    }

    @GetMapping(value = "updategas")
    private List<Region> updategas(){
        airQualityService.UpdateRegionGasData();

        return airQualityService.findAll();
    }

    @GetMapping(value = "getcodes")
    private Map<Regions, String> getcodes(){
        Map<Regions,String> regionCodes = new HashMap<>();
        for(Regions r : Regions.values()){
            String uri = "https://analisi.transparenciacatalunya.cat/resource/tasf-thgu.json?$$app_token={$$app_token}&nom_comarca={nom_comarca}";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("$$app_token", ApiToken);
            vars.put("nom_comarca", airQualityService.FixRegionName(r));

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
