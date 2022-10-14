package com.potus.app.airquality.service;

import com.potus.app.airquality.model.*;
import com.potus.app.airquality.repository.GasRegistryRepository;
import com.potus.app.airquality.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.potus.app.airquality.model.Regions.*;
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

        /*
        regions.add(new Region(Alt_Camp,41.28, 1.25));
        regions.add(new Region(Regions.Alt_Emporda,42.28, 2.93));
        regions.add(new Region(Regions.Alt_Penedes,41.36, 1.68));
        regions.add(new Region(Regions.Alt_Urgell,42.24, 1.41));
        regions.add(new Region(Regions.Alta_Ribagorca,42.43, 0.86));
        regions.add(new Region(Regions.Anoia,41.61, 1.61));
        regions.add(new Region(Regions.Bages,41.78, 1.86));
        regions.add(new Region(Regions.Baix_Camp,41.1, 1.1));
        regions.add(new Region(Regions.Baix_Ebre,40.85, 0.56));
        regions.add(new Region(Regions.Baix_Emporda,41.95, 3.06));
        regions.add(new Region(Regions.Baix_Llobregat,41.43, 1.97));
        regions.add(new Region(Regions.Baix_Penedes,41.22, 1.53));
        regions.add(new Region(Regions.Barcelones,41.40, 2.16));
        regions.add(new Region(Regions.Bergueda,42.11, 1.84));
        regions.add(new Region(Regions.Cerdanya,42.45, 1.95));
        regions.add(new Region(Regions.Conca_de_Barbera,41.37, 1.15));
        regions.add(new Region(Regions.Garraf,41.32, 1.82));
        regions.add(new Region(Regions.Garrigues,41.52, 0.87));
        regions.add(new Region(Regions.Garrotxa,42.17, 2.55));
        regions.add(new Region(Regions.Girones,41.94, 2.81));
        regions.add(new Region(Regions.Maresme,41.6, 2.5));
        regions.add(new Region(Regions.Montsia,40.7, 0.57));
        regions.add(new Region(Regions.Noguera,41.90, 0.93));
        regions.add(new Region(Regions.Osona,41.95, 2.25));
        regions.add(new Region(Regions.Pallars_Jussa,42.28, 0.93));
        regions.add(new Region(Regions.Pallars_Subira,42.52, 1.19));
        regions.add(new Region(Regions.Pla_d_Urgell,41.64, 0.91));
        regions.add(new Region(Regions.Priorat,41.13, 0.82));
        regions.add(new Region(Regions.Ribera_d_Ebre,41.08, 0.63));
        regions.add(new Region(Regions.Ripolles,42.27, 2.26));
        regions.add(new Region(Regions.Segarra,41.739167, 1.33));
        regions.add(new Region(Regions.Serria,42.018056, 2.83));
        regions.add(new Region(Regions.Selva,41.8647, 2.67));
        regions.add(new Region(Regions.Solsones,41.98, 1.51));
        regions.add(new Region(Regions.Tarragones,41.15, 1.29));
        regions.add(new Region(Regions.Terra_Alta,41.05, 0.43));
        regions.add(new Region(Regions.Urgell,41.66, 1.09));
        regions.add(new Region(Regions.Vall_d_Aran,42.72, 0.84));
        regions.add(new Region(Regions.Valles_Occidental,41.56, 2.04));
        regions.add(new Region(Regions.Valles_Oriental,41.65, 2.31));

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

        regionRepository.saveAll(regions);

         */
        }

    public List<Region> findAll(){
        return regionRepository.findAll();
    }

    public List<GasRegistry> findAllRegistries(){
        return gasRegistryRepository.findAll();
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

    public String FixRegionName(Regions comarca) {
        // Aquí habrá que fixear las comarcas que puedan dar problemas
        String comarcaFixed = "";

        switch (comarca) {
            case Alt_Camp -> comarcaFixed = "Alt Camp";
            case Alt_Emporda -> comarcaFixed = "Alt Empordà";
            case Alt_Penedes -> comarcaFixed = "Alt Penedès";
            case Alt_Urgell -> comarcaFixed = "Alt Urgell";
            case Alta_Ribagorca -> comarcaFixed = "Alta Ribagorça";
            case Baix_Camp -> comarcaFixed = "Baix Camp";
            case Baix_Ebre -> comarcaFixed = "Baix Ebre";
            case Baix_Emporda -> comarcaFixed = "Baix Empordà";
            case Baix_Llobregat -> comarcaFixed = "Baix Llobregat";
            case Baix_Penedes -> comarcaFixed = "Baix Penedès";
            case Conca_de_Barbera -> comarcaFixed = "Conca de Barberà";
            case Pallars_Jussa -> comarcaFixed = "Pallars Jussà";
            case Pallars_Subira -> comarcaFixed = "Pallars Subirà";
            case Pla_d_Urgell -> comarcaFixed = "Pla d'Urgell";
            case Ribera_d_Ebre -> comarcaFixed = "Ribera d'Ebre";
            case Terra_Alta -> comarcaFixed = "Terra Alta";
            case Vall_d_Aran -> comarcaFixed = "Vall d'Aran";
            case Valles_Occidental -> comarcaFixed = "Vallès Occidental";
            case Valles_Oriental -> comarcaFixed = "Vallès Oriental";
            //default -> comarcaFixed = comarca;
        }

        return comarcaFixed;
    }



    public String getGasData(String nom_comarca) {
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

                String code = dato.get("codi_comarca");
                if(code != null)
                    return code;

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
            //return gases;
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


