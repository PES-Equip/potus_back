package com.potus.app.potus.service;

import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.potus.model.Modifier;
import com.potus.app.potus.model.ModifierEffectType;
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

    public List<Modifier> getPermanentBuffs() {
        return modifierRepository.findByType(ModifierType.PERMANENT_BUFF);
    }

    public List<Modifier> getDebuffs() {
        return modifierRepository.findByType(ModifierType.TEMPORAL_DEBUFF);
    }

    public List<Modifier> initializeModifiers() {
        List<Modifier> modifiers = new ArrayList<>();

        modifiers.add(new Modifier("WATER_PERMANENT_INCREASE", ModifierEffectType.WATERING_MODIFIER, 1.0, 120.0, ModifierType.PERMANENT_BUFF));
        modifiers.add(new Modifier("WATER_DECREASE", ModifierEffectType.WATERING_MODIFIER, 5.0, null, ModifierType.TEMPORAL_DEBUFF));
        modifiers.add(new Modifier("WATERING_TIME", ModifierEffectType.WATERING_TIME,  240.0, null, ModifierType.TEMPORAL_DEBUFF));
        modifiers.add(new Modifier("PRUNE_CURRENCY_PERMANENT_BUFF", ModifierEffectType.PRUNE_CURRENCY_GENERATION, 1.0, 120.0, ModifierType.PERMANENT_BUFF));
        modifiers.add(new Modifier("PRUNE_CURRENCY_DEBUFF", ModifierEffectType.PRUNE_CURRENCY_GENERATION, 0.5, null, ModifierType.TEMPORAL_DEBUFF));
        modifiers.add(new Modifier("PRUNE_MAX_CURRENCY_PERMANENT_BUFF", ModifierEffectType.MAX_CURRENCY_GENERATION,120.0, 240.0, ModifierType.PERMANENT_BUFF));
        modifiers.add(new Modifier("HEALTH_FASTER_REDUCTION", ModifierEffectType.HEALTH_REDUCTION,0.5, null, ModifierType.TEMPORAL_DEBUFF));
        modifiers.add(new Modifier("HEALTH_REDUCED_GENERATION", ModifierEffectType.HEALTH_GENERATION, 0.5, null, ModifierType.TEMPORAL_DEBUFF));
        modifiers.add(new Modifier("PRUNE_CURRENCY_MULTIPLIER_BUFF", ModifierEffectType.PRUNE_CURRENCY_GENERATION, 2.0, null, ModifierType.TEMPORAL_BUFF));
        modifiers.add(new Modifier("PRUNE_CURRENCY_FESTIVITY_BUFF", ModifierEffectType.PRUNE_CURRENCY_GENERATION, 3.0, null, ModifierType.FESTIVITY_BUFF));

        logger.info("Initialized all bonuses");

        return modifierRepository.saveAll(modifiers);
    }
}
