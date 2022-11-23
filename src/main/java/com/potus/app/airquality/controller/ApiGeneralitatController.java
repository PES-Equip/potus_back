package com.potus.app.airquality.controller;



import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.model.Regions;
import com.potus.app.airquality.utils.AirQualityUtils;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.garden.model.GardenMember;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
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
    public List<Region> initializeRegions() {
        return airQualityService.initializeRegions();
    }


    @GetMapping(value = "update")
    public List<Region> updateGasDataRegions(){
        return airQualityService.updateRegionGasData();
    }

    @GetMapping(value = "regions")
    public List<Region> getRegions() {return airQualityService.findAll();}

    @GetMapping(value = "getcodes")
    public Map<Regions, String> getCodes(){
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

    @Autowired
    UserService userService;
    @GetMapping("/lel")
    public void test(@RequestParam String token){

        String uri = "https://oauth2.googleapis.com/tokeninfo?id_token="+ token;
        RestTemplate restTemplate = new RestTemplate();

        try {
            Object result = restTemplate.getForObject(uri, Object.class);
            Map<String,String> parsedResult = (Map<String, String>) result;

            String email = parsedResult.get("email");
            User user = userService.findByEmail(email);

            GardenMember member = user.getGarden();
            if(member != null){
                System.out.println(Objects.equals(member.getGarden().getName(), "tet"));
            }
            else{
                System.out.println("BADDDD");
            }
            System.out.println(user.getStatus());
            System.out.println(token);
            System.out.println(uri);
            System.out.println(result);
        }
        catch (HttpClientErrorException exception){
            System.out.println(exception.getLocalizedMessage());
        }
        catch (ResourceNotFoundException exception){
            System.out.println("YEPA");
        }
    }



}
