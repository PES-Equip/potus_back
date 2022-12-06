package com.potus.app.potus.utils;


import com.potus.app.potus.model.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ModifierUtils {

    public static final Integer MODIFIERS_QUANTITY = 10;

    public static Double getCurrentValue(Double initialValue, Integer level){

        if(initialValue > 30){
            return initialValue*Math.pow(2,level-1.);
        }
        return initialValue*level;
    }

    public static Double getCurrentPrice(Double initialCost, Integer level){

        return initialCost*Math.pow(2,level-1.);
    }

    public static Double getModifierValue(Potus potus, ModifierEffectType type) {
        Double value = 0.0;

        Map<ModifierType, PotusModifier> buffs = findPotusModifiersByType(potus, type, true);
        Map<ModifierType, PotusModifier> debuffs = findPotusModifiersByType(potus, type, false);

        switch (type) {
            case WATERING_MODIFIER:
                for (PotusModifier modifier : buffs.values()) {
                    if (modifier != null) value = value + getCurrentValue(modifier.getModifier().getValue(), modifier.getLevel()).intValue();
                }

                for (PotusModifier modifier : debuffs.values()) {
                    if (modifier != null) value = value - modifier.getModifier().getValue().intValue();
                }
                break;
            case WATERING_TIME:
                for (PotusModifier modifier : debuffs.values()) {
                    if (modifier != null) value = value - modifier.getModifier().getValue().intValue();
                }
                break;
            case PRUNE_CURRENCY_GENERATION:
                Integer multiplier = 1;
                for (PotusModifier modifier : buffs.values()) {
                    if (modifier != null) {
                        if (!modifier.getModifier().getModifierType().equals(ModifierType.TEMPORAL_BUFF))
                            value = value + getCurrentValue(modifier.getModifier().getValue(), modifier.getLevel()).intValue();
                        else multiplier = modifier.getModifier().getValue().intValue();

                    }
                }
                value = value * multiplier;
                for (PotusModifier modifier : debuffs.values()) {
                    if (modifier != null) value = value * modifier.getModifier().getValue();
                }
                break;
            case MAX_CURRENCY_GENERATION:
                for (PotusModifier modifier : buffs.values()) {
                    if (modifier != null) value = value + getCurrentValue(modifier.getModifier().getValue(), modifier.getLevel()).intValue();
                }
                break;
            case HEALTH_GENERATION, HEALTH_REDUCTION:
                value = 1.0;
                for (PotusModifier modifier : debuffs.values()) {
                    if (modifier != null) {
                        if (value > modifier.getModifier().getValue()) {
                            value = modifier.getModifier().getValue();
                        }
                    }
                }
                break;
            default:
                break;
        }



        return value;
    }

    public static Map<ModifierType, PotusModifier> findPotusModifiersByType(Potus potus, ModifierEffectType type, boolean isBuff) {
        Map<ModifierType, PotusModifier> requestedModifiers = new HashMap<>();
        Map<ModifierType, Set<PotusModifier>> modifiers = new HashMap<>();

        if (isBuff) {
            Set<PotusModifier> buffs = potus.getBuffs();

            for (PotusModifier buff : buffs) {
                if (buff.getModifier().getType().equals(type)) {
                    if (buff.getModifier().getModifierType().equals(ModifierType.PERMANENT_BUFF))
                        requestedModifiers.put(ModifierType.PERMANENT_BUFF, buff);
                    else if (buff.getModifier().getModifierType().equals(ModifierType.TEMPORAL_BUFF))
                        requestedModifiers.put(ModifierType.PERMANENT_BUFF, buff);
                    else requestedModifiers.put(ModifierType.FESTIVITY_BUFF, buff);
                }
            }
        }
        else {
            Set<PotusModifier> debuffs = potus.getDebuffs();
            for (PotusModifier debuff : debuffs) {
                if (debuff.getModifier().getType().equals(type)) {
                    if (debuff.getModifier().getModifierType().equals(ModifierType.TEMPORAL_DEBUFF))
                        requestedModifiers.put(ModifierType.TEMPORAL_DEBUFF, debuff);
                }
            }
        }
/*
        Iterator<PotusModifier>  setIterator;

        for (Map.Entry<ModifierType, Set<PotusModifier>> entry : modifiers.entrySet()) {
            boolean found = false;
            PotusModifier modifier = null;
            setIterator = entry.getValue().iterator();
            while (setIterator.hasNext() && !found) {
                PotusModifier currentModifier = setIterator.next();
                if (currentModifier.getModifier().getType().equals(type)) {
                    modifier = currentModifier;
                    found = true;
                }
            }
            requestedModifiers.put(entry.getKey(), modifier);
        } */

        if (requestedModifiers.size() != 0) System.out.println(requestedModifiers);

        return requestedModifiers;
    }


    public static PotusModifier findPotusBuff(Potus potus, ModifierEffectType type) {
        PotusModifier potusBuff = null;
        Set<PotusModifier> buffs = potus.getBuffs();
        Iterator<PotusModifier> buffsIterator = buffs.iterator();

        boolean found = false;

        while (buffsIterator.hasNext() && !found) {
            PotusModifier currentDebuff = buffsIterator.next();
            if (currentDebuff.getModifier().getType().equals(type)) {
                potusBuff = currentDebuff;
                found = true;
            }
        }

        return potusBuff;
    }

    public static PotusModifier findPotusDebuff (Potus potus, ModifierEffectType type) {
        PotusModifier potusDebuff = null;
        Set<PotusModifier> debuffs = potus.getDebuffs();
        Iterator<PotusModifier> debuffsIterator = debuffs.iterator();

        boolean found = false;

        while (debuffsIterator.hasNext() && !found) {
            PotusModifier currentDebuff = debuffsIterator.next();
            if (currentDebuff.getModifier().getType().equals(type)) {
                potusDebuff = currentDebuff;
                found = true;
            }
        }

        return potusDebuff;
    }

}
