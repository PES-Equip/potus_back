package com.potus.app.potus.service;

import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.GeneralExceptionMessages;
import com.potus.app.exception.TooManyRequestsException;
import com.potus.app.potus.model.CurrencyGenerators;
import com.potus.app.potus.model.PotusAction;
import com.potus.app.potus.model.Actions;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.States;
import com.potus.app.potus.repository.ActionsRepository;
import com.potus.app.potus.repository.PotusRepository;
import com.potus.app.potus.utils.PotusUtils;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.potus.app.user.service.UserService;

import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.potus.app.potus.utils.PotusExceptionMessages.ACTION_ALREADY_DID_IT;
import static com.potus.app.potus.utils.PotusUtils.*;

@Service
public class PotusService {

    @Autowired
    PotusRepository potusRepository;

    @Autowired
    ActionsRepository actionsRepository;


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
        return potus;
    }


    public void updatePotusStats(Potus potus){
        System.out.println("before update: " + potus.getIgnored());
        Date now = new Date();
        Date lastModified = potus.getLastModified();

        Integer previousWater = potus.getWaterLevel();

        Long diff = TimeUnit.SECONDS.convert(Math.abs(now.getTime() - lastModified.getTime()),TimeUnit.MILLISECONDS)/TIME_REDUCTION;
        if (diff > 0){

            Integer damage = subtractWaterLevel(potus,diff.intValue());

            if (previousWater >= MIN_WATER_FOR_RECOVERY && potus.getHealth() < MAX_HEALTH) addHealth(potus, potus.getWaterLevel(), previousWater);

            subtractHealth(potus,damage);
            if (!(potus.getWaterLevel() == 0 && damage == 0 )) potus.setLastModified(now);
            else System.out.println("That's the case you are looking for " + potus.getLastModified());

            System.out.println("after update: " + potus.getIgnored());

            savePotus(potus);
        }
    }

    @Transactional
    public void deletePotus(Potus potus){
        potusRepository.delete(potus);
    }

    private void addHealth(Potus potus, Integer actualWater, Integer previousWater) {
        Integer health;

        if (actualWater < 90) health = previousWater - MIN_WATER_FOR_RECOVERY * HEALTH_RECOVERY;
        else health = previousWater - actualWater * HEALTH_RECOVERY;
    }

    public Integer subtractWaterLevel(Potus potus, Integer debt){
        Integer result = 0;
        Integer waterLevel = potus.getWaterLevel();
        Integer current = waterLevel - debt;

        potus.setWaterLevel(Math.abs(current));

        if(current < 0){
            potus.setWaterLevel(0);
            result = Math.abs(current) / 7;
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
            System.out.println("ignored");
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

        int waterLevel = potus.getWaterLevel() + potus.getWaterRecovery() + getRandomWateringBonus();

        if(waterLevel > 100)
            waterLevel = 100;

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

        System.out.println("currency : " +currency);
        return currency;
    }

    private Integer calculateCurrencyBonus(Potus potus, PotusAction action) {
        Date now = new Date();

        Long currency = (TimeUnit.SECONDS.convert(Math.abs
                        (now.getTime() - action.getLastTime().getTime()),
                TimeUnit.MILLISECONDS) / PRUNING_ACTION_TIME);

        Integer multiplier = getCurrencyMultiplier(potus.getCurrencyGenerators(), potus.getCurrencyMultiplier());
        currency = currency * multiplier;
        if (currency > potus.getPruningMaxCurrency()) currency = potus.getPruningMaxCurrency();

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

    public Potus restartPotus(Potus potus, String name) {
        potus.initialize(name);
        return potusRepository.save(potus);
    }
}
