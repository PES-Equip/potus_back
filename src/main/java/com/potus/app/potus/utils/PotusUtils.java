package com.potus.app.potus.utils;

import com.potus.app.potus.model.PotusAction;
import com.potus.app.potus.model.Actions;

import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

public final class PotusUtils {

    public static final Integer MAX_HEALTH = 100;
    public static final Integer MIN_HEALTH = 0;

    public static final Integer MAX_WATER_LEVEL = 100;
    public static final Integer MIN_WATER_LEVEL = 0;

    public static final Integer WATERING_BONUS = 25;

    // 5 mins in secs
    public static final Long TIME_REDUCTION = 300L;


    //30 minutes in seconds
    public static final Long ACTION_TIME = 1800L;


    public static final Integer ACTION_CURRENCY = 25;

    //public static Integer getCurrentWaterLevel()
    private PotusUtils(){
    }

    public static Double euclideanDistance(Double x1, Double y1, Double x2, Double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public static Map<Actions, PotusAction> generateDefaultActions(){

        Map<Actions, PotusAction> result = new HashMap<>();

        for(Actions action : Actions.values()){
            PotusAction newPotusAction = new PotusAction(action);
            result.put(action, newPotusAction);
        }



        return result;
    }

}
