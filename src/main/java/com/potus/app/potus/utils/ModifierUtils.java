package com.potus.app.potus.utils;


import com.potus.app.potus.model.ModifierEffectType;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.PotusModifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

    public static Double getModifierValue(Potus potus, ModifierEffectType type) {
        Double value = 0.0;

        Map<Boolean, PotusModifier> buffs = findPotusModifiersByType(potus, type, true);
        Map<Boolean, PotusModifier> debuffs = findPotusModifiersByType(potus, type, false);

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
                for (PotusModifier modifier : buffs.values()) {
                    if (modifier != null) value = value + getCurrentValue(modifier.getModifier().getValue(), modifier.getLevel()).intValue();
                }
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

    public static Map<Boolean, PotusModifier> findPotusModifiersByType(Potus potus, ModifierEffectType type, boolean isBuff) {
        Map<Boolean, PotusModifier> requestedModifiers = new HashMap<>();
        Map<Boolean, Set<PotusModifier>> modifiers = new HashMap<>();

        if (isBuff) {
            Set<PotusModifier> buffs = potus.getBuffs();
            modifiers.put(Boolean.TRUE, buffs);
        }
        else {
            Set<PotusModifier> debuffs = potus.getDebuffs();
            modifiers.put(Boolean.FALSE, debuffs);
        }

        Iterator<PotusModifier>  setIterator;

        for (Map.Entry<Boolean, Set<PotusModifier>> entry : modifiers.entrySet()) {
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
        }

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
