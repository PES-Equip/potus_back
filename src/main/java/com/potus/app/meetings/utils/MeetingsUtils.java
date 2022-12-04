package com.potus.app.meetings.utils;

import org.springframework.web.client.RestTemplate;

public class MeetingsUtils {
    public static final String BUSCAT_API_URL = "https://webapp-buscat.onrender.com/api";
    public static final String ID = "codi";
    public static final String START_DATE = "data_inici";
    public static final String END_DATE = "data_fi";
    public static final String ADDRESS = "adre_a";
    public static final String CITY = "city";

    public static final String TITLE = "denominaci";
    public static final String SUBTITLE = "subt_tol";
    public static final String LATITUDE = "latitud";
    public static final String LENGTH = "longitud";
    public static final String COMARCA_I_MUNICIPI = "comarca_i_municipi";
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static Object[] getMeetingsInformation() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(BUSCAT_API_URL, Object[].class);
    }




}
