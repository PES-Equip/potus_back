package com.potus.app.potus.service;

import com.potus.app.exception.BadRequestException;
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
import java.util.Date;
import java.util.Map;
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

    /**
     * Updates (WATER LEVEL AND HEALTH) in time function
     * @param potus
     */
    public void updatePotusStats(Potus potus){
        Date now = new Date();
        Date lastModified = potus.getLastModified();

        Long diff = TimeUnit.SECONDS.convert(Math.abs(now.getTime() - lastModified.getTime()),TimeUnit.MILLISECONDS)/TIME_REDUCTION;
        if (diff > 0){

            Integer damage = subtractWaterLevel(potus,diff.intValue());
            subtractHealth(potus,damage);
            potus.setLastModified(now);
            savePotus(potus);
        }
    }

    @Transactional
    public void deletePotus(Potus potus){
        potusRepository.delete(potus);
    }

    public Integer subtractWaterLevel(Potus potus, Integer debt){
        Integer result = 0;
        Integer waterLevel = potus.getWaterLevel();
        Integer current = waterLevel - debt;

        potus.setWaterLevel(Math.abs(current));

        if(current < 0){
            potus.setWaterLevel(0);
            result = Math.abs(current);
        }
        return result;
    }

    public void subtractHealth(Potus potus, Integer debt){
        Integer result = 0;
        Integer health = potus.getHealth();
        Integer current = health - debt;

        potus.setHealth(Math.abs(current));

        if(current <= 0){
            potus.setHealth(0);
            potus.setAlive(false);
        }

    }

    public Integer doWatering(Potus potus) throws BadRequestException{
        PotusAction action = potus.getAction(Actions.WATERING);

        doAction(action);


        int waterLevel = potus.getWaterLevel() + WATERING_BONUS;

        if(waterLevel > 100)
            waterLevel = 100;

        potus.setWaterLevel(waterLevel);
        saveFullPotus(potus);

        return 0;
    }

    private static void doAction(PotusAction action) throws BadRequestException{
        Date now = new Date();

        boolean canAction = TimeUnit.SECONDS.convert(Math.abs
                (now.getTime() - action.getLastTime().getTime()),
                TimeUnit.MILLISECONDS)/ACTION_TIME > 0;

        if(! canAction)
            throw new BadRequestException(ACTION_ALREADY_DID_IT);

        action.setLastTime(now);
    }

    public Integer doPrune(Potus potus){
        PotusAction action = potus.getAction(Actions.PRUNE);

        doAction(action);
        saveFullPotus(potus);

        return PRUNNING_CURRENCY_BONUS;
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
