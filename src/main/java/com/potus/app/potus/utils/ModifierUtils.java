package com.potus.app.potus.utils;

import com.potus.app.potus.model.Modifier;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.PotusModifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public final class ModifierUtils {

    public static final Integer MODIFIERS_QUANTITY = 8;

    public static Set<PotusModifier> createBuffs(Potus potus, List<Modifier> modifierBuffs) {
        Set<PotusModifier> buffs = new HashSet<>();

        for(Modifier modifierBuff : modifierBuffs) {
            buffs.add(new PotusModifier(potus, modifierBuff, 1));
        }

        System.out.println(buffs);
        return buffs;
    }

    public static void updateBuffs() {

    }

}
