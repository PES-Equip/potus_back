package com.potus.app.potus.service;

import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.potus.model.Modifier;
import com.potus.app.potus.model.ModifierType;
import com.potus.app.potus.repository.ModifierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModifierService {

    @Autowired
    ModifierRepository modifierRepository;

    Logger logger = LoggerFactory.getLogger(AirQualityService.class);

    public List<Modifier> findAll() {
        return modifierRepository.findAll();
    }

    public List<Modifier> getBuffs() {
        List<Modifier> result = new ArrayList<>();
        List<Modifier> modifiers = findAll();

        for (Modifier modifier : modifiers) {
            if (modifier.isBuff()) result.add(modifier);
        }

        return result;
    }

    public List<Modifier> getDebuffs() {
        List<Modifier> result = new ArrayList<>();
        List<Modifier> modifiers = findAll();

        for (Modifier modifier : modifiers) {
            if (!modifier.isBuff()) result.add(modifier);
        }

        return result;
    }

    public List<Modifier> initializeModifiers() {
        List<Modifier> modifiers = new ArrayList<>();

        modifiers.add(new Modifier("WATER_INCREASE", ModifierType.WATERING_MODIFIER, 1.0, 120.0, true));
        modifiers.add(new Modifier("WATER_DECREASE", ModifierType.WATERING_MODIFIER, 5.0, -1.0, false));
        modifiers.add(new Modifier("WATERING_TIME", ModifierType.WATERING_TIME, 240.0, -1.0, false));
        modifiers.add(new Modifier("PRUNE_CURRENCY_BUFF", ModifierType.PRUNE_CURRENCY_GENERATION, 1.0, 120.0, true));
        modifiers.add(new Modifier("PRUNE_CURRENCY_DEBUFF", ModifierType.PRUNE_CURRENCY_GENERATION, 0.5, -1.0, false));
        modifiers.add(new Modifier("PRUNE_MAX_CURRENCY_BUFF", ModifierType.MAX_CURRENCY_GENERATION, 120.0, 240.0, true));
        modifiers.add(new Modifier("HEALTH_FASTER_REDUCTION", ModifierType.HEALTH_REDUCTION, 2.0, -1.0, false));
        modifiers.add(new Modifier("HEALTH_REDUCED_GENERATION", ModifierType.HEALTH_GENERATION, 2.0, -1.0, false));

        logger.info("Initialized all bonuses");

        return modifierRepository.saveAll(modifiers);
    }
}
