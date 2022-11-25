package com.potus.app.potus.utils;


public final class ModifierUtils {

    public static final Integer MODIFIERS_QUANTITY = 8;

    public static Double getCurrentValue(Double initialValue, Integer level){

        if(initialValue > 30){
            return initialValue*Math.pow(2,level-1.);
        }
        return initialValue*level;
    }

    public static Double getCurrentPrice(Double initialCost, Integer level){

        return initialCost*Math.pow(2,level-1.);
    }



}
