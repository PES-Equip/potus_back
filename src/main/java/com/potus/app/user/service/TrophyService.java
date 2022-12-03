package com.potus.app.user.service;

import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.user.model.Trophy;
import com.potus.app.user.model.TrophyType;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserTrophy;
import com.potus.app.user.repository.TrophyRepository;
import com.potus.app.user.repository.UserTrophyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.potus.app.user.utils.UserExceptionMessages.TROPHY_DOES_NOT_EXISTS;
import static com.potus.app.user.utils.UserUtils.calculateTrophyNextLevel;
import static com.potus.app.user.utils.UserUtils.monthsBetween;

@Service
public class TrophyService {

    @Autowired
    TrophyRepository trophyRepository;

    @Autowired
    UserTrophyRepository userTrophyRepository;


    public void initTrophies(){
        List<Trophy> trophies = new ArrayList<>();
        trophies.add(new Trophy(5, TrophyType.PRUNE));
        trophies.add(new Trophy(5, TrophyType.WATERING));
        trophies.add(new Trophy(3, TrophyType.POTUS_TIME));
        trophies.add(new Trophy(3, TrophyType.GARDEN_TIME));
        trophies.add(new Trophy(1, TrophyType.DEAD));
        trophies.add(new Trophy(100, TrophyType.CURRENCY));
        trophies.add(new Trophy(100, TrophyType.TOTAL_CURRENCY));
        trophies.add(new Trophy(1, TrophyType.UPGRADES));
        trophies.add(new Trophy(1, TrophyType.MAX_UPGRADE));
        trophyRepository.saveAll(trophies);
    }

    public Trophy findTrophyByType(TrophyType type){
        return trophyRepository.findByType(type).orElseThrow(() -> new ResourceNotFoundException(TROPHY_DOES_NOT_EXISTS));
    }

    public List<Trophy> findAll() {
        return trophyRepository.findAll();
    }

    public void initUserTrophies(User user) {
        List<Trophy> trophies = trophyRepository.findAll();
        List<UserTrophy> userTrophies = new ArrayList<>();
        trophies.forEach(trophy -> {
            userTrophies.add(new UserTrophy(trophy,user));
        });

        userTrophyRepository.saveAll(userTrophies);
    }

    public UserTrophy findUserTrophy(User user, Trophy trophy){
        return userTrophyRepository.findByUserAndTrophy(user,trophy).orElseThrow(() -> new ResourceNotFoundException(TROPHY_DOES_NOT_EXISTS));
    }


    public UserTrophy findUserTrophyByType(User user, TrophyType type){
        return  findUserTrophy(user, findTrophyByType(type));
    }

    public void updateTrophy(User user, TrophyType type, int amount) {
        UserTrophy trophy = findUserTrophyByType(user,type);

        int updatedCurrent = trophy.getCurrent() + amount;
        int diff = trophy.getNextLevel() - updatedCurrent;

        if(diff < 0){
            trophy.setCurrent(updatedCurrent);
        }
        else{
            trophy.setCurrent(diff);
            trophy.upgradeLevel();
        }
        userTrophyRepository.save(trophy);
    }

    public void conditionalUpdateTrophy(User user, TrophyType type, Integer updatedCurrent) {
        UserTrophy trophy = findUserTrophyByType(user,type);

        if(trophy.getCurrent() < updatedCurrent){
            int diff = trophy.getNextLevel() - updatedCurrent;

            if(diff < 0){
                trophy.setCurrent(updatedCurrent);
            }
            else{
                trophy.setCurrent(diff);
                trophy.upgradeLevel();
            }
            userTrophyRepository.save(trophy);
        }
    }

    public void updateTrophyDateBased(User user, TrophyType type, Date startDate) {
        UserTrophy trophy = findUserTrophyByType(user,type);
        Date current = new Date();

        int diff = monthsBetween(startDate,current);

        if(diff > 0){

            int levelNeeded = 0;
            for(int i = 1; i < trophy.getLevel(); ++i){
                levelNeeded += calculateTrophyNextLevel(trophy.getTrophy().getBase(), i);
            }
            levelNeeded += trophy.getCurrent();
            if(diff - levelNeeded > 0){
                updateTrophy(user, type,diff);
            }

        }

    }
}
