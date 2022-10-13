package com.potus.app.airquality.service;

import com.potus.app.airquality.model.*;
import org.springframework.web.client.RestTemplate;

import java.rmi.registry.Registry;
import java.util.*;

import static com.potus.app.airquality.model.Units.*;

public class AirQualityService {

    // ESTO NO VA DA PROBLEMAS DE STATIC ASI QUE DE MOMENTO LO HARDCODEO
    /*
    @Value("#{systemEnvironment['GENERALITAT_API_TOKEN']}")
    private String ApiToken;
    */
    private static final String  ApiToken = "LolDRyxtdtFUO1vCupmTXkRry";

    public static Map<String, Region> Regions_map;

    // CAMBIAR ESTO PARA QUE RETORNA LA UNIDAD CORRECTA EN BASE AL GAS QUE SEA!!!!!!!!!!!!!!
    public static Units getUnit(Gases gas) {
        Units unit = µg_m3;
        switch (gas) {
            case Cl2:
            case HCl:
            case PS:
            case NO:
            case NO2:
            case NOX:
            case O3:
            case H2S:
            case PM1:
            case PM2_5:
            case PM10:
            case C6H6:
            case SO2:
                unit = µg_m3;
                break;
            case CO:
                unit = mg_m3;
                break;
            case Hg:
                unit = ng_m3;
                break;
            case HCNM:
            case HCT:
                unit = ppm;
                break;
            default:
                System.out.println("Mistake");
                break;
        }

        return unit;
    }

    public static void InitializeGases() {
        Regions_map = new HashMap<String, Region>();
        for (Regions region: Regions.values()) {
            String regionFixed = FixRegionName(String.valueOf(region)); // Fixeamos el nombre en caso de haber algun fallo de espacios, puntos etc

            // ESTO HAY QUE CAMBIARLO PARA PONER LA LATITUD Y LENGHT CORRECTO PARA CADA REGION !!!!!!!!!!!!!!
            Double latitude = 0.0;
            Double lenght = 0.0;

            Map<Gases, GasRegistry> gases = new HashMap<Gases, GasRegistry>();
            for(Gases gas : Gases.values()) {
                Units gasUnit = getUnit(gas);
                System.out.println(gasUnit);
                GasRegistry gasregistry = new GasRegistry(gas, 0.0, gasUnit);
                gases.put(gas, gasregistry);
            }

            // Creamos la region con los valores de los gases inicializados a 0
            Region r = new Region(regionFixed, latitude, lenght, gases);
            Regions_map.put(regionFixed, r);
        }
            System.out.println(Regions_map);
            printRegionMap();
        }

    private static void printRegionMap() {
        for (Region r : Regions_map.values()) {
            Map<Gases, GasRegistry> gr = r.getRegistry();
            System.out.println(r.getName());
            for (GasRegistry x : gr.values()) {
                System.out.println(x.getName());
                System.out.println(x.getValue());
                System.out.println(x.getUnit());
            }
        }
    }


    public static void UpdateRegions() {
        // PENDING : ACABAR EL ENUM DE REGIONS (Comarcas)
        // Habrá que hacer casos especiales para las comarcas que no se pueden poner bien en el enum.

        for (Regions region: Regions.values()) {
            String regionFixed = FixRegionName(String.valueOf(region)); // Fixeamos el nombre en caso de haber algun fallo de espacios, puntos etc
            Map<Gases, Double> gasData = new HashMap<Gases, Double>();
            gasData = getGasData(regionFixed); //Mapa con los datos del gas para la region dada
            System.out.println(gasData);
            //Actualizamos los datos para la region
            UpdateRegionData(regionFixed, gasData);

        }
    }

    public static void UpdateRegionData(String regionName, Map<Gases, Double> gasData) {
            Region region = Regions_map.get(regionName);
            Map<Gases, GasRegistry> gases = region.getRegistry();
            for(Gases gas: gases.keySet()) {

                Double data = gasData.get(gas);

                if(data != null) {
                    GasRegistry gasRegistry = gases.get(gas);
                    gasRegistry.setValue(data);
                }
            }




    }

    private static String FixRegionName(String comarca) {
        // Aquí habrá que fixear las comarcas que puedan dar problemas
        String comarcaFixed = "";

        switch (comarca) {
            case "Alt_Camp" -> comarcaFixed = "Alt Camp";
            case "Alt_Empordà" -> comarcaFixed = "Alt Empordà";
            case "Alt_Penedès" -> comarcaFixed = "Alt Penedès";
            case "Alt_Urgell" -> comarcaFixed = "Alt Urgell";
            case "Alta_Ribagorça" -> comarcaFixed = "Alta Ribagorça";
            case "Baix_Camp" -> comarcaFixed = "Baix Camp";
            case "Baix_Ebre" -> comarcaFixed = "Baix Ebre";
            case "Baix_Empordà" -> comarcaFixed = "Baix Empordà";
            case "Baix_Llobregat" -> comarcaFixed = "Baix Llobregat";
            case "Baix_Penedès" -> comarcaFixed = "Baix Penedès";
            case "Conca_de_Barberà" -> comarcaFixed = "Conca de Barberà";
            case "Pallars_Jussà" -> comarcaFixed = "Pallars Jussà";
            case "Pallars_Subirà" -> comarcaFixed = "Pallars Subirà";
            case "Pla_d_Urgell" -> comarcaFixed = "Pla d'Urgell";
            case "Ribera_d_Ebre" -> comarcaFixed = "Ribera d'Ebre";
            case "Terra_Alta" -> comarcaFixed = "Terra Alta";
            case "Vall_d_Aran" -> comarcaFixed = "Vall d'Aran";
            case "Vallès_Occidental" -> comarcaFixed = "Vallès Occidental";
            case "Vallès_Oriental" -> comarcaFixed = "Vallès Oriental";
            default -> comarcaFixed = comarca;
        }

        return comarcaFixed;
    }



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
        Map<Gases, Integer> gasesContador = new EnumMap<Gases, Integer>(Gases.class);


        for(Object o : result) {
            Map<String,String> dato = (Map<String, String>) o;
            if(!Objects.equals(dato.get("data"), data)) break;

            // ENUM IS 2_5, THIS CHANGES 2.5 TO 2_5
            if (Objects.equals(dato.get("contaminant"), "PM2.5")) {
                dato.put("contaminant", "PM2_5");
            }

            Gases gas = Gases.valueOf(dato.get("contaminant"));


            // Retorna la media del gas de todas las horas que hayan
            Double valueGas = getMediaGas(dato);

            // If value_gas == -1, it doesn't have any value.
            if(valueGas >= 0) {
                if (!gases.containsKey(gas)) {
                    gases.put(gas, valueGas);
                    gasesContador.put(gas, 1);
                } else {
                    Double aux = gases.get(gas);
                    valueGas += aux;
                    gases.put(gas, valueGas);
                    gasesContador.put(gas, gasesContador.get(gas) + 1);
                }
        }
        }

        for(Gases g:gases.keySet()) {
            Double media = gases.get(g)/gasesContador.get(g);
            gases.put(g, media);
        }

        System.out.println(gases);

        return gases;
     }

    private static Double getMediaGas(Map<String,String> dato) {
        double media = 0.0;
        int cont = 0;
        if(!dato.containsKey(String.valueOf(GasesHours.h01))) media = -1.0;

        for(GasesHours hour: GasesHours.values()) {
            if(!dato.containsKey(String.valueOf(hour))) break;
            media += Double.parseDouble(dato.get(String.valueOf(hour)));
            ++cont;
        }
        media = media/cont;

        return media;
    }
}


