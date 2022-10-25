package com.potus.app.airquality.service;

import com.potus.app.airquality.model.*;
import com.potus.app.airquality.repository.GasRegistryRepository;
import com.potus.app.airquality.repository.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.potus.app.airquality.model.Units.*;
import static com.potus.app.airquality.utils.AirQualityUtils.*;

@Service
public class AirQualityService {

    // ESTO NO VA DA PROBLEMAS DE STATIC ASI QUE DE MOMENTO LO HARDCODEO
    /*
    @Value("#{systemEnvironment['GENERALITAT_API_TOKEN']}")
    private String ApiToken;
    */
    private static final String  ApiToken = "LolDRyxtdtFUO1vCupmTXkRry";


    Logger logger = LoggerFactory.getLogger(AirQualityService.class);

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    GasRegistryRepository gasRegistryRepository;

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


    public List<Region> initializeRegions() {
        List<Region> regions = new ArrayList<>();

        regions.add(new Region(Regions.Alt_Camp,41.28, 1.25,"01"));
        regions.add(new Region(Regions.Alt_Emporda,42.28, 2.93, "02"));
        regions.add(new Region(Regions.Alt_Penedes,41.36, 1.68, "03"));
        regions.add(new Region(Regions.Alt_Urgell,42.24, 1.41,null));
        regions.add(new Region(Regions.Alta_Ribagorca,42.43, 0.86, null));
        regions.add(new Region(Regions.Anoia,41.61, 1.61, "06"));
        regions.add(new Region(Regions.Bages,41.78, 1.86, "07"));
        regions.add(new Region(Regions.Baix_Camp,41.1, 1.1, "08"));
        regions.add(new Region(Regions.Baix_Ebre,40.85, 0.56, "09"));
        regions.add(new Region(Regions.Baix_Emporda,41.95, 3.06, "10"));
        regions.add(new Region(Regions.Baix_Llobregat,41.43, 1.97, "11"));
        regions.add(new Region(Regions.Baix_Penedes,41.22, 1.53, null));
        regions.add(new Region(Regions.Barcelones,41.40, 2.16, "13"));
        regions.add(new Region(Regions.Bergueda,42.11, 1.84, "14"));
        regions.add(new Region(Regions.Cerdanya,42.45, 1.95, "15"));
        regions.add(new Region(Regions.Conca_de_Barbera,41.37, 1.15, null));
        regions.add(new Region(Regions.Garraf,41.32, 1.82, "17"));
        regions.add(new Region(Regions.Garrigues,41.52, 0.87, "18"));
        regions.add(new Region(Regions.Garrotxa,42.17, 2.55, "19"));
        regions.add(new Region(Regions.Girones,41.94, 2.81, "20"));
        regions.add(new Region(Regions.Maresme,41.6, 2.5, "21"));
        regions.add(new Region(Regions.Montsia,40.7, 0.57, "22"));
        regions.add(new Region(Regions.Noguera,41.90, 0.93, "23"));
        regions.add(new Region(Regions.Osona,41.95, 2.25, "24"));
        regions.add(new Region(Regions.Pallars_Jussa,42.28, 0.93, "25"));
        regions.add(new Region(Regions.Pallars_Subira,42.52, 1.19, "26"));
        regions.add(new Region(Regions.Pla_d_Urgell,41.64, 0.91, null));
        regions.add(new Region(Regions.Priorat,41.13, 0.82, "29"));
        regions.add(new Region(Regions.Ribera_d_Ebre,41.08, 0.63, "30"));
        regions.add(new Region(Regions.Ripolles,42.27, 2.26,"31"));
        regions.add(new Region(Regions.Segarra,41.739167, 1.33, null));
        regions.add(new Region(Regions.Segria,42.018056, 2.83, "33"));
        regions.add(new Region(Regions.Selva,41.8647, 2.67,null));
        regions.add(new Region(Regions.Solsones,41.98, 1.51, null));
        regions.add(new Region(Regions.Tarragones,41.15, 1.29, "36"));
        regions.add(new Region(Regions.Terra_Alta,41.05, 0.43, "37"));
        regions.add(new Region(Regions.Urgell,41.66, 1.09, null));
        regions.add(new Region(Regions.Vall_d_Aran,42.72, 0.84, null));
        regions.add(new Region(Regions.Valles_Occidental,41.56, 2.04, "40"));
        regions.add(new Region(Regions.Valles_Oriental,41.65, 2.31, "41"));

        regions.forEach(region -> {
            Map<Gases, GasRegistry> gases = new HashMap<>();
            for(Gases gas : Gases.values()) {
                Units gasUnit = getUnit(gas);
                GasRegistry gasregistry = new GasRegistry(gas, 0.0, gasUnit);
                gases.put(gas, gasregistry);
            }
            gasRegistryRepository.saveAll(gases.values());
            region.setRegistry(gases);
        });
        logger.info("Initialized all the regions");
        return regionRepository.saveAll(regions);
        }

    public List<Region> findAll(){
        return regionRepository.findAll();
    }

    public List<GasRegistry> findAllRegistries(){
        return gasRegistryRepository.findAll();
    }

    public List<Region> updateRegionGasData(){
        List<Region> regions = regionRepository.findAll();

        regions.forEach(region -> {
            if (region.getCode() != null) {
                Map<Gases, Double> gasData = getGasData(region.getCode());

                Map<Gases, GasRegistry> registry = region.getRegistry();

                for (Gases gas : registry.keySet()) {
                    GasRegistry gasRegistryAux = registry.get(gas);
                    Double valueGas = gasData.get(gas);
                    if(valueGas != null) gasRegistryAux.setValue(valueGas);
                    registry.put(gas, gasRegistryAux);
                    gasRegistryRepository.save(gasRegistryAux);
                }
                region.setRegistry(registry);
                regionRepository.save(region);
                logger.info(region.getName() + " UPDATED");
                //System.out.println(region.getName() + "UPDATED");
            }
        });
        return regions;
    }


    public Map<Gases, Double> getGasData(String regionCode) {
        String uri = API_URL+API_TOKEN_PARAM+"={"+API_TOKEN_PARAM+"}&"+API_CODE_PARAM+"={"+API_CODE_PARAM+"}";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> vars = new HashMap<>();


        vars.put(API_TOKEN_PARAM, ApiToken);
        vars.put(API_CODE_PARAM, regionCode);

        Object[] result = restTemplate.getForObject(uri, Object[].class, vars);

        if(result != null) { // Obtener la ultima fecha registrada para tener el dato mas actualizado.
            Map<String, String> query_result = new HashMap<>();

            try {
                query_result = (Map<String, String>) result[0];
            } catch (Exception e) {
                System.out.println("No data for " + regionCode);
                return null;
            }

            String data = query_result.get("data");


            Map<Gases, Double> gases = new EnumMap<>(Gases.class);
            Map<Gases, Integer> gasesCounter = new EnumMap<>(Gases.class);


            for (Object o : result) {
                Map<String, String> row = (Map<String, String>) o;

                if (!Objects.equals(row.get("data"), data)) break;

                // ENUM IS 2_5, THIS CHANGES 2.5 TO 2_5
                if (Objects.equals(row.get("contaminant"), "PM2.5")) {
                    row.put("contaminant", "PM2_5");
                }

                Gases gas = Gases.valueOf(row.get("contaminant"));


                // Retorna la media del gas de todas las horas que hayan
                Double valueGas = getMediaGas(row);

                // If value_gas == -1, it doesn't have any value.
                if (valueGas >= 0) {
                    if (!gases.containsKey(gas)) {
                        gases.put(gas, valueGas);
                        gasesCounter.put(gas, 1);
                    } else {
                        Double aux = gases.get(gas);
                        valueGas += aux;
                        gases.put(gas, valueGas);
                        gasesCounter.put(gas, gasesCounter.get(gas) + 1);
                    }
                }
            }

            for (Gases g : gases.keySet()) {
                Double media = gases.get(g) / gasesCounter.get(g);
                gases.put(g, media);
            }
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
    private void printRegions() {
        List<Region> regions = regionRepository.findAll();
        regions.forEach(region1 -> {
            System.out.println("name");
            System.out.println(region1.getName());
            System.out.println("code");
            System.out.println(region1.getCode());

            Map<Gases, GasRegistry> registry = region1.getRegistry();
            System.out.println("Registry");
            for(GasRegistry g : registry.values()) {
                System.out.println("Name");
                System.out.println(g.getName());
                System.out.println("Value");
                System.out.println(g.getValue());
            }
        });
    }
}




