package com.potus.app.potus.utils;

import com.potus.app.airquality.model.Gases;
import com.potus.app.potus.model.GasesAndStates;
import com.potus.app.potus.model.States;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.potus.app.potus.model.States.*;

public final class EventsUtils {

    public static final Integer StringPositionMonth1Beginning = 0;
    public static final Integer StringPositionMonth1Ending = 2;

    public static final Integer StringPositionDay1Beginning = 3;
    public static final Integer StringPositionDay1Ending = 5;

    public static final Integer StringPositionMonth2Beginning = 6;
    public static final Integer StringPositionMonth2Ending = 8;

    public static final Integer StringPositionDay2Beginning = 9;
    public static final Integer StringPositionDay2Ending = 11;

    public static final String NewYearBeginning = "01-01"; //Month-Day format
    public static final String NewYearEnding = "01-08"; //Month-Day format
    public static final String NewYear = "01-01/01-08"; //Month-Day format

    //Modify annually
    public static final String ChineseNewYearBeginning = "01-22"; //Month-Day format
    public static final String ChineseNewYearEnding = "01-29"; //Month-Day format
    public static final String ChineseNewYear = "01-22/01-29";

    //Modify annually
    public static final String MardiGrasBeginning = "02-17"; //Month-Day format
    public static final String MardiGrasEnding = "02-21"; //Month-Day format
    public static final String MardiGras = "02-17/02-21";

    public static final String SaintPatrick = "03-17/03-17"; //Month-Day format

    public static final String SantJoanBeginning = "06-23"; //Month-Day format
    public static final String SantJoanEnding = "06-24"; //Month-Day format
    public static final String SantJoan = "06-23/06-24";

    public static final String HalloweenBeginning = "10-28"; //Month-Day format
    public static final String HalloweenEnding = "10-31"; //Month-Day format
    public static final String Halloween = "10-28/10-31";

    public static final String ChristmasBeginning = "12-17"; //Month-Day format
    public static final String ChristmasEnding = "12-31"; //Month-Day format
    public static final String Christmas = "12-17/12-31";


    public static Map<String, GasesAndStates> getAllFestivities () {
        Map<String, GasesAndStates> result = new HashMap<>();

        result.put(NewYear, NEW_YEAR);
        result.put(ChineseNewYear, CHINESE_NEW_YEAR);
        result.put(MardiGras, MARDI_GRAS);
        result.put(SaintPatrick, SAINT_PATRICK);
        result.put(SantJoan, SANT_JOAN);
        result.put(Halloween, HALLOWEEN);
        result.put(Christmas, CHRISTMAS);

        return result;
    }

    public static Map<String, GasesAndStates> getMonthFestivities (String month) {
        Map<String, GasesAndStates> result = new HashMap<>();
        Map<String, GasesAndStates> festivities = getAllFestivities();

        for (Map.Entry<String, GasesAndStates> festivity : festivities.entrySet()) {
            if (month.equals(festivity.getKey().substring(StringPositionMonth1Beginning, StringPositionMonth1Ending)) ||
                    month.equals(festivity.getKey().substring(StringPositionMonth2Beginning, StringPositionMonth2Ending))) {
                result.put(festivity.getKey(), festivity.getValue());
            }
        }

        return result;
    }

    public static String getDate() {
        return Instant.now().toString();
    }

    public static GasesAndStates getStateValue (String state) {
        GasesAndStates result = DEFAULT;

        try {
            result = States.valueOf(state);
        } catch (Exception ignored) {}

        try {
            result = Gases.valueOf(state);
        } catch (Exception ignored) {}

        return result;
    }
}

