package com.potus.app.airquality.utils;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static Map<String, Double> getAuxMap(Double lat, Double len) {
        Map<String, Double> aux = new HashMap<>();
        aux.put("latitude", lat);
        aux.put("length", len);
        return aux;
    }

    public static Map<String, Map<String, Double>> getRegionData() {
        Map<String, Map<String, Double>> result = new HashMap<>();


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


        return result;
    }
}
