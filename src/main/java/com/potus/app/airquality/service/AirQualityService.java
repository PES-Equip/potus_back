package com.potus.app.airquality.service;

import com.potus.app.airquality.model.Gases;
import com.potus.app.airquality.model.GasesHours;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class AirQualityService {
    @Value("#{systemEnvironment['GENERALITAT_API_TOKEN']}")
    private static String ApiToken;


    public static Map<Gases, Double> getGasData(String nom_comarca) {
        String uri = "https://analisi.transparenciacatalunya.cat/resource/tasf-thgu.json?$$app_token={$$app_token}&nom_comarca={nom_comarca}";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> vars = new HashMap<String, String>();

        vars.put("$$app_token", ApiToken);
        vars.put("nom_comarca", nom_comarca);

        Object[] result = restTemplate.getForObject(uri, Object[].class, vars);

        assert result != null;

        // Obtener la ultima fecha registrada para tener el dato mas actualizado.
        Map<String,String> m = (Map<String, String>) result[0];
        String data = m.get("data");
        
        Map<Gases, Double> gases = new EnumMap<Gases, Double>(Gases.class);
        Map<Gases, Integer> gases_contador = new EnumMap<Gases, Integer>(Gases.class);


        for(Object o : result) {
            Map<String,String> dato = (Map<String, String>) o;
            if(!Objects.equals(dato.get("data"), data)) break;

            // ENUM IS 2_5, THIS CHANGES 2.5 TO 2_5
            if (Objects.equals(dato.get("contaminant"), "PM2.5")) {
                dato.put("contaminant", "PM2_5");
            }

            Gases gas = Gases.valueOf(dato.get("contaminant"));


            // AQUÍ HABRÁ QUE PILLAR TODOS LOS VALORES DEL GAS DE MOMENTO COJO SOLO EL PRIMERO)
            //Double value_gas = Double.valueOf(dato.get("h01"));
            Double value_gas = getMediaGas(dato);

            // If value_gas == -1, it doesn't have any value.
            if(value_gas >= 0) {
                if (!gases.containsKey(gas)) {
                    gases.put(gas, value_gas);
                    gases_contador.put(gas, 1);
                } else {
                    Double aux = gases.get(gas);
                    value_gas += aux;
                    gases.put(gas, value_gas);
                    gases_contador.put(gas, gases_contador.get(gas) + 1);
                }
        }
        }

        for(Gases g:gases.keySet()) {
            Double media = gases.get(g)/gases_contador.get(g);
            gases.put(g, media);
        }

        System.out.println(gases);


        return gases;
     }

    private static Double getMediaGas(Map<String,String> dato) {
        double media = 0.0;
        Integer cont = 0;
        if(!dato.containsKey(String.valueOf(GasesHours.h01))) media = -1.0;

        for(GasesHours hour: GasesHours.values()) {
            if(!dato.containsKey(String.valueOf(hour))) break;
            media += Double.parseDouble(dato.get(String.valueOf(hour)));
            ++cont;
        }
        media = media/cont;
        System.out.println(dato.get("codi_eoi"));
        System.out.println(dato.get("contaminant"));
        System.out.println(media);

        return media;
    }
}


