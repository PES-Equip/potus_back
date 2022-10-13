package com.potus.app.airquality.service;

import com.potus.app.airquality.model.*;
import com.potus.app.airquality.repository.GasRegistryRepository;
import com.potus.app.airquality.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.potus.app.airquality.model.Units.*;

@Service
public class AirQualityService {

    // ESTO NO VA DA PROBLEMAS DE STATIC ASI QUE DE MOMENTO LO HARDCODEO
    /*
    @Value("#{systemEnvironment['GENERALITAT_API_TOKEN']}")
    private String ApiToken;
    */
    private static final String  ApiToken = "LolDRyxtdtFUO1vCupmTXkRry";

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    GasRegistryRepository gasRegistryRepository;

    public static Map<String, Region> Regions_map;

    // CAMBIAR ESTO PARA QUE RETORNA LA UNIDAD CORRECTA EN BASE AL GAS QUE SEA!!!!!!!!!!!!!!
    public static Units getUnit(Gases gas) {
        Units unit;
        switch (gas) {
            //case Cl2, HCl, PS, NO, NO2, NOX, O3, H2S, PM1, PM2_5, PM10, C6H6, SO2 -> unit = µg_m3;
            case CO -> unit = mg_m3;
            case Hg -> unit = ng_m3;
            case HCNM, HCT -> unit = ppm;
            default -> unit = µg_m3;
        }

        return unit;
    }


    public void InitializeGases() {

        List<Region> regions = new ArrayList<>();

        regions.add(new Region(Regions.Alt_Camp,41.28, 1.25,  new HashMap<>()));

        result.put("Alt_Camp", getAuxMap(41.28, 1.25));
        result.put("Alt_Emporda", getAuxMap(42.28, 2.93));
        result.put("Alt_Penedes", getAuxMap(41.36, 1.68));
        result.put("Alt_Urgell", getAuxMap(42.24, 1.41));
        result.put("Alta_Ribagorca", getAuxMap(42.43, 0.86));
        result.put("Anoia", getAuxMap(41.61, 1.61));
        result.put("Bages", getAuxMap(41.78, 1.86));
        result.put("Baix_Camp", getAuxMap(41.1, 1.1));
        result.put("Baix_Ebre", getAuxMap(40.85, 0.56));
        result.put("Baix_Emporda", getAuxMap(41.95, 3.06));
        result.put("Baix_Llobregat", getAuxMap(41.43, 1.97));
        result.put("Baix_Penedes", getAuxMap(41.22, 1.53));
        result.put("Barcelones", getAuxMap(41.40, 2.16));
        result.put("Bergueda", getAuxMap(42.11, 1.84));
        result.put("Cerdanya", getAuxMap(42.45, 1.95));
        result.put("Conca_de_Barbera", getAuxMap(41.37, 1.15));
        result.put("Garraf", getAuxMap(41.32, 1.82));
        result.put("Garrigues", getAuxMap(41.52, 0.87));
        result.put("Garrotxa", getAuxMap(42.17, 2.55));
        result.put("Girones", getAuxMap(41.94, 2.81));
        result.put("Maresme", getAuxMap(41.6, 2.5));
        result.put("Montsia", getAuxMap(40.7, 0.57));
        result.put("Noguera", getAuxMap(41.90, 0.93));
        result.put("Osona", getAuxMap(41.95, 2.25));
        result.put("Pallars_Jussa", getAuxMap( 42.28, 0.93));
        result.put("Pallars_Subira", getAuxMap(42.52, 1.19));
        result.put("Pla_d_Urgell", getAuxMap(41.64, 0.91));
        result.put("Priorat", getAuxMap(41.13, 0.82));
        result.put("Ribera_d_Ebre", getAuxMap(41.08, 0.63));
        result.put("Ripolles", getAuxMap(42.27, 2.26));
        result.put("Segarra", getAuxMap(41.739167, 1.33));
        result.put("Serria", getAuxMap(42.018056, 2.83));
        result.put("Selva", getAuxMap(41.8647, 2.67));
        result.put("Solsones", getAuxMap(41.98, 1.51));
        result.put("Tarragones", getAuxMap(41.15, 1.29));
        result.put("Terra_Alta", getAuxMap(41.05, 0.43));
        result.put("Urgell", getAuxMap(41.66, 1.09));
        result.put("Vall_d_Aran", getAuxMap(42.72, 0.84));
        result.put("Valles_Occidental", getAuxMap(41.56, 2.04));
        result.put("Valles_Oriental", getAuxMap(41.65, 2.31));

        regions.forEach(region -> {
            for(Gases gas : Gases.values()) {

                Map<Gases, GasRegistry> gases = new HashMap<>();
                Units gasUnit = getUnit(gas);
                GasRegistry gasregistry = new GasRegistry(gas, 0.0, gasUnit);
                gases.put(gas, gasregistry);

                gasRegistryRepository.saveAll(gases.values());
            }
        });

        regionRepository.saveAll(regions);
        /*
        Regions_map = new HashMap<String, Region>();
        Map<String, Map<String, Double>> RegionsLatitudeLenghtMap = Utils.getRegionData();

        for (Regions region: Regions.values()) {
            String regionFixed = FixRegionName(String.valueOf(region)); // Fixeamos el nombre en caso de haber algun fallo de espacios, puntos etc
            Double latitude = RegionsLatitudeLenghtMap.get(String.valueOf(region)).get("latitude");
            Double lenght = RegionsLatitudeLenghtMap.get(String.valueOf(region)).get("length");

            Map<Gases, GasRegistry> gases = new HashMap<Gases, GasRegistry>();
            for(Gases gas : Gases.values()) {
                Units gasUnit = getUnit(gas);
                GasRegistry gasregistry = new GasRegistry(gas, 0.0, gasUnit);
                gases.put(gas, gasregistry);
            }

            // Creamos la region con los valores de los gases inicializados a 0
            Region r = new Region(regionFixed, latitude, lenght, gases);
            Regions_map.put(regionFixed, r);
        }
            System.out.println(Regions_map);
        */
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
            System.out.println(regionFixed);
            gasData = getGasData(regionFixed); //Mapa con los datos del gas para la region dada
            //Actualizamos los datos para la region
            if(gasData != null) UpdateRegionData(regionFixed, gasData);
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
            case "Alt_Emporda" -> comarcaFixed = "Alt Empordà";
            case "Alt_Penedes" -> comarcaFixed = "Alt Penedès";
            case "Alt_Urgell" -> comarcaFixed = "Alt Urgell";
            case "Alta_Ribagorca" -> comarcaFixed = "Alta Ribagorça";
            case "Baix_Camp" -> comarcaFixed = "Baix Camp";
            case "Baix_Ebre" -> comarcaFixed = "Baix Ebre";
            case "Baix_Emporda" -> comarcaFixed = "Baix Empordà";
            case "Baix_Llobregat" -> comarcaFixed = "Baix Llobregat";
            case "Baix_Penedes" -> comarcaFixed = "Baix Penedès";
            case "Conca_de_Barbera" -> comarcaFixed = "Conca de Barberà";
            case "Pallars_Jussa" -> comarcaFixed = "Pallars Jussà";
            case "Pallars_Subira" -> comarcaFixed = "Pallars Subirà";
            case "Pla_d_Urgell" -> comarcaFixed = "Pla d'Urgell";
            case "Ribera_d_Ebre" -> comarcaFixed = "Ribera d'Ebre";
            case "Terra_Alta" -> comarcaFixed = "Terra Alta";
            case "Vall_d_Aran" -> comarcaFixed = "Vall d'Aran";
            case "Valles_Occidental" -> comarcaFixed = "Vallès Occidental";
            case "Valles_Oriental" -> comarcaFixed = "Vallès Oriental";
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

        if(result != null) { // Obtener la ultima fecha registrada para tener el dato mas actualizado.
            Map<String, String> m = new HashMap<String,String>();
            try {
                m = (Map<String, String>) result[0];
            } catch (Exception e) {
                System.out.println("No data for that comarca");
            }

            String data = m.get("data");

            Map<Gases, Double> gases = new EnumMap<Gases, Double>(Gases.class);
            Map<Gases, Integer> gasesContador = new EnumMap<Gases, Integer>(Gases.class);


            for (Object o : result) {
                Map<String, String> dato = (Map<String, String>) o;
                if (!Objects.equals(dato.get("data"), data)) break;

                // ENUM IS 2_5, THIS CHANGES 2.5 TO 2_5
                if (Objects.equals(dato.get("contaminant"), "PM2.5")) {
                    dato.put("contaminant", "PM2_5");
                }

                Gases gas = Gases.valueOf(dato.get("contaminant"));


                // Retorna la media del gas de todas las horas que hayan
                Double valueGas = getMediaGas(dato);

                // If value_gas == -1, it doesn't have any value.
                if (valueGas >= 0) {
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

            for (Gases g : gases.keySet()) {
                Double media = gases.get(g) / gasesContador.get(g);
                gases.put(g, media);
            }

            System.out.println(gases);
            return gases;
        }
        return null;
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


