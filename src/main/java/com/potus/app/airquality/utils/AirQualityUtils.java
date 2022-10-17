package com.potus.app.airquality.utils;

import com.potus.app.airquality.model.Regions;

import java.util.HashMap;
import java.util.Map;

public class AirQualityUtils {


    public static final String API_TOKEN_PARAM = "$$app_token";

    public static final String API_CODE_PARAM = "codi_comarca";

    public static final String API_URL = "https://analisi.transparenciacatalunya.cat/resource/tasf-thgu.json?";

    private AirQualityUtils(){}


    public static String FixRegionName(Regions comarca) {
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
            case Pallars_Subira -> comarcaFixed = "Pallars Sobirà";
            case Pla_d_Urgell -> comarcaFixed = "Pla d'Urgell";
            case Ribera_d_Ebre -> comarcaFixed = "Ribera d'Ebre";
            case Terra_Alta -> comarcaFixed = "Terra Alta";
            case Vall_d_Aran -> comarcaFixed = "Vall d'Aran";
            case Valles_Occidental -> comarcaFixed = "Vallès Occidental";
            case Valles_Oriental -> comarcaFixed = "Vallès Oriental";
            case Segarra -> comarcaFixed = "Segarrà";
            case Segria -> comarcaFixed = "Segrià";
            case Girones -> comarcaFixed = "Gironès";
            case Tarragones -> comarcaFixed = "Tarragonès";
            case Ripolles -> comarcaFixed = "Ripollès";
            case Montsia -> comarcaFixed = "Montsià";
            case Barcelones -> comarcaFixed = "Barcelonès";
            case Bergueda -> comarcaFixed = "Berguedà";
            default -> comarcaFixed = String.valueOf(comarca);
        }

        return comarcaFixed;
    }

}
