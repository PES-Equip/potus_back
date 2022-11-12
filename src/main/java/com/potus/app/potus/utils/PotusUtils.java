package com.potus.app.potus.utils;

import com.potus.app.potus.model.CurrencyGenerators;
import com.potus.app.potus.model.PotusAction;
import com.potus.app.potus.model.Actions;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class PotusUtils {

    public static final Integer MAX_HEALTH = 100;
    public static final Integer MIN_HEALTH = 0;
    public static final Integer HEALTH_RECOVERY = 2;

    public static final Integer MAX_WATER_LEVEL = 100;
    public static final Integer MIN_WATER_FOR_RECOVERY = 90;
    public static final Integer MIN_WATER_LEVEL = 0;

    public static final Integer WATERING_BONUS = 25;

    public static final Integer PRUNNING_CURRENCY_BONUS = 2;
    public static final Integer FESTIVITY_DEFAULT_CURRENCY = 0;
    public static final Integer FESTIVITY_ADDITIONAL_CURRENCY = 1;

    // 14 mins in secs
    public static final Long TIME_REDUCTION = 840L;


    //30 minutes in seconds
    public static final Long ACTION_TIME = 30L;//1800L;
    public static final Long PRUNING_ACTION_TIME = 60L;
    public static final Long WATERING_ACTION_TIME = 1800L;



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

    public static String getActionRemainingTime (Long now, Long actionDate, PotusAction action) {
        Long actionTime = getActionTime(action);

        Long timePassed = now - actionDate;
        System.out.println(timePassed);
        Long remainingTime = actionTime - timePassed;
        System.out.println(remainingTime);

        return convertTimeToString(remainingTime);
    }

    public static String convertTimeToString(Long seconds) {
        String result;

        int day = (int)TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24L);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        result = day + "d " + hours + "h " + minute + "m " + second + "s";

        return result;
    }

    public static Long getActionTime(PotusAction action) {
        Long time;

        if (action.getName().equals(Actions.WATERING)) time = WATERING_ACTION_TIME;
        else time = PRUNING_ACTION_TIME;

        return time;
    }

    public static Integer getCurrencyMultiplier(Map<CurrencyGenerators, Integer> currencyMultipliers, Integer multiplier) {
        Integer result = 0;

        for (Integer  currency: currencyMultipliers.values()) {
            result += currency;
        }
        return result * multiplier;
    }

    public static Integer getRandomWateringBonus () {
        Random rand = new SecureRandom();

        return rand.nextInt(11);
    }

}
