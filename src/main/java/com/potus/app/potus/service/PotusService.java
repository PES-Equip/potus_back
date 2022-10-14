package com.potus.app.potus.service;

import com.potus.app.potus.model.Potus;
import com.potus.app.potus.repository.PotusRepository;
import com.potus.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.potus.app.potus.utils.PotusUtils.TIME_REDUCTION;

@Service
public class PotusService {

    @Autowired
    PotusRepository potusRepository;

    public Potus savePotus(Potus potus){
        return potusRepository.save(potus);
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
}
