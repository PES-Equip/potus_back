package com.potus.app.potus.service;

import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.GeneralExceptionMessages;
import com.potus.app.exception.TooManyRequestsException;
import com.potus.app.potus.model.*;
import com.potus.app.potus.repository.ActionsRepository;
import com.potus.app.potus.repository.ModifierRepository;
import com.potus.app.potus.repository.PotusModifierRepository;
import com.potus.app.potus.repository.PotusRepository;
import com.potus.app.potus.utils.ModifierUtils;
import com.potus.app.potus.utils.PotusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;


import static com.potus.app.potus.utils.PotusUtils.*;

@Service
public class PotusService {

    @Autowired
    PotusRepository potusRepository;

    @Autowired
    ActionsRepository actionsRepository;

    @Autowired
    ModifierRepository modifierRepository;

    @Autowired
    PotusModifierRepository potusModifierRepository;


    public Potus savePotus(Potus potus){
        return potusRepository.save(potus);
    }

    public Potus saveFullPotus(Potus potus){
        actionsRepository.saveAll(potus.getActions().values());
        return potusRepository.save(potus);
    }


    @Transactional
    public Potus createPotus(String name){
        Potus potus = new Potus(name);

        Map<Actions, PotusAction> actions = PotusUtils.generateDefaultActions();
        potus.setActions(actions);
        saveFullPotus(potus);

        List<Modifier> buffModifiers = modifierRepository.findByModifierType(ModifierType.PERMANENT_BUFF);

        potus.setBuffs(generatePotusModifiers(potus,buffModifiers));

        potusModifierRepository.saveAll(potus.getBuffs());
        savePotus(potus);
        return potus;
    }


    public void updatePotusStats(Potus potus){
        Date now = new Date();
        Date lastModified = potus.getLastModified();

        Integer previousWater = potus.getWaterLevel();
        Long debuffTimeReduction = ModifierUtils.getModifierValue(potus, ModifierEffectType.WATERING_TIME).longValue();

        //System.out.println("Time : " + (TIME_REDUCTION + debuffTimeReduction));

        Long diff = TimeUnit.SECONDS.convert(Math.abs(now.getTime() - lastModified.getTime()),TimeUnit.MILLISECONDS)/
                (TIME_REDUCTION + debuffTimeReduction);
        if (diff > 0){

            Integer damage = subtractWaterLevel(potus,diff.intValue());

            if (previousWater >= MIN_WATER_FOR_RECOVERY && potus.getHealth() < MAX_HEALTH) {
                addHealth(potus, potus.getWaterLevel(), previousWater);
            }

            subtractHealth(potus,damage);
            if (!(potus.getWaterLevel() == 0 && damage == 0 )) potus.setLastModified(now);

            savePotus(potus);
        }
    }

    @Transactional
    public void deletePotus(Potus potus){
        potusRepository.delete(potus);
    }

    private void addHealth(Potus potus, Integer actualWater, Integer previousWater) {

        Double addedHealth;
        Integer health;
        Double healthGenerationDebuff = ModifierUtils.getModifierValue(potus, ModifierEffectType.HEALTH_GENERATION);

        System.out.println("Previous health: " + potus.getHealth());
        System.out.println("Health Generation Debuff : " + healthGenerationDebuff);
        System.out.println("Expected Healths: " + (previousWater - MIN_WATER_FOR_RECOVERY) * HEALTH_RECOVERY +
                " " + (previousWater - actualWater) * HEALTH_RECOVERY);

        if (actualWater < 90) addedHealth = ((previousWater - MIN_WATER_FOR_RECOVERY) * HEALTH_RECOVERY) * healthGenerationDebuff;
        else addedHealth = ((previousWater - actualWater) * HEALTH_RECOVERY) * healthGenerationDebuff;

        System.out.println("Real Added Health: " + addedHealth);

        health = potus.getHealth() + addedHealth.intValue();

        System.out.println("Health: " + health);

        if (health > 100) health = 100;
        potus.setHealth(health);
    }

    public Integer subtractWaterLevel(Potus potus, Integer debt){
        Integer result = 0;
        Integer waterLevel = potus.getWaterLevel();
        Integer current = waterLevel - debt;

        potus.setWaterLevel(Math.abs(current));

        if(current < 0){
            potus.setWaterLevel(0);

            Double healthReductionDebuff = ModifierUtils.getModifierValue(potus, ModifierEffectType.HEALTH_REDUCTION);
            Double healthReduction = HEALTH_REDUCTION_TIME * healthReductionDebuff;

            System.out.println("Health Reduction Debuff: " + healthReductionDebuff);
            System.out.println("Health Reduction: " + healthReduction);

            result = (Math.abs(current) / healthReduction.intValue());

            System.out.println("Damage Without Reduction: " + (Math.abs(current) / HEALTH_REDUCTION_TIME));
            System.out.println("Damage With Reduction: " + result);
        }
        return result;
    }

    public void subtractHealth(Potus potus, Integer debt){
        Integer result = 0;
        Integer health = potus.getHealth();
        Integer current = health - debt;

        if (current <= 0 && !potus.getIgnored()) {
            current = 1;
            potus.setIgnored(true);
        }


        potus.setHealth(Math.abs(current));

        if(current <= 0){
            potus.setHealth(0);
            potus.setAlive(false);
        }

    }

    public Integer doWatering(Potus potus) throws BadRequestException{
        if (potus.getIgnored()) potus.setIgnored(false);

        PotusAction action = potus.getAction(Actions.WATERING);

        doAction(action);

        System.out.println("Current water level: " + potus.getWaterLevel());
        System.out.println("Water recovery constant: " + WATER_RECOVERY);
        System.out.println("Modifier: " + ModifierUtils.getModifierValue(potus, ModifierEffectType.WATERING_MODIFIER).intValue());

        int waterLevel = potus.getWaterLevel() + WATER_RECOVERY + getRandomWateringBonus() +
                ModifierUtils.getModifierValue(potus, ModifierEffectType.WATERING_MODIFIER).intValue();

        if(waterLevel > 100)
            waterLevel = 100;


        System.out.println("New Water Level: " + waterLevel);
        potus.setWaterLevel(waterLevel);
        saveFullPotus(potus);

        return 0;
    }

    private static void doAction(PotusAction action) throws BadRequestException{
        Date now = new Date();

        boolean canAction;

        Long actualTime = TimeUnit.SECONDS.convert(Math.abs(now.getTime()), TimeUnit.MILLISECONDS);
        Long lastActionTime = TimeUnit.SECONDS.convert(Math.abs(action.getLastTime().getTime()), TimeUnit.MILLISECONDS);

        if (action.getName().equals(Actions.WATERING)) {
            canAction = (actualTime - lastActionTime) / WATERING_ACTION_TIME > 0;
        }
        else {//Pruning
            canAction = (actualTime - lastActionTime) / PRUNING_ACTION_TIME > 0;
        }


        if(!canAction) {
            String remainingTime = getActionRemainingTime(actualTime, lastActionTime, action);
            throw new TooManyRequestsException(GeneralExceptionMessages.TOO_MANY_REQUESTS + ". Try again in: " + remainingTime);
        }


        action.setLastTime(now);
    }

    public Integer doPrune(Potus potus){
        PotusAction action = potus.getAction(Actions.PRUNE);

        Integer currency = calculateCurrencyBonus(potus, action);
        doAction(action);
        saveFullPotus(potus);

        return currency;
    }

    private Integer calculateCurrencyBonus(Potus potus, PotusAction action) {
        Date now = new Date();

        Long currency = (TimeUnit.SECONDS.convert(Math.abs
                        (now.getTime() - action.getLastTime().getTime()),
                TimeUnit.MILLISECONDS) / PRUNING_ACTION_TIME);

        Integer multiplier = ModifierUtils.getModifierValue(potus, ModifierEffectType.PRUNE_CURRENCY_GENERATION).intValue();

        System.out.println("Generated currency: " + currency);
        System.out.println("Multiplier: " + multiplier);
        System.out.println("Total currency: " + (currency * multiplier));

        currency = currency * multiplier;
        Integer maxCurrency = ModifierUtils.getModifierValue(potus, ModifierEffectType.MAX_CURRENCY_GENERATION).intValue();
        System.out.println("Maximum currency generating allowed: " + maxCurrency);

        if (currency > maxCurrency) currency = potus.getPruningMaxCurrency();

        return currency.intValue();
    }

    public Integer doFilterAction(Potus potus, Actions action){
        Integer currency = 0;
        switch (action){
            case PRUNE -> currency = doPrune(potus);
            case WATERING -> currency = doWatering(potus);
        }
        return currency;
    }

    @Transactional
    public Potus restartPotus(Potus potus, String name) {
        potus.initialize(name);

        List<PotusModifier> potusModifiers = potusModifierRepository.findByPotus(potus);
        potusModifierRepository.deleteAll(potusModifiers);

        Map<Actions, PotusAction> actions = PotusUtils.generateDefaultActions();

        List<Modifier> buffModifiers = modifierRepository.findByModifierType(ModifierType.PERMANENT_BUFF);

        potus.setBuffs(generatePotusModifiers(potus,buffModifiers));
        potus.setActions(actions);
        actionsRepository.saveAll(actions.values());
        potusModifierRepository.saveAll(potus.getBuffs());
        return potusRepository.save(potus);
    }

    public List<PotusModifier> getPotusBuffs(Potus potus) {
        return potusModifierRepository.findByPotus(potus);
    }

    public PotusModifier upgradeModifier(PotusModifier selectedModifier) {
        selectedModifier.setLevel(selectedModifier.getLevel() + 1);
        return potusModifierRepository.save(selectedModifier);
    }
}
