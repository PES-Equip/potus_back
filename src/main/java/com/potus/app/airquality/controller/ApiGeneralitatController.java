package com.potus.app.airquality.controller;



import com.potus.app.airquality.model.Gases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping(value = "updategas")
    private Map<Gases, Double> updateGasComarcas(){
        return AirQualityService.getGasData("Baix Llobregat");
    }

    @GetMapping(value = "aux")
    private void aux() {
        airQualityService.InitializeGases();
        //airQualityService.UpdateRegions();
    }


}
