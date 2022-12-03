package com.potus.app.potus.service;


import com.potus.app.airquality.model.*;
import com.potus.app.airquality.repository.RegionRepository;
import com.potus.app.airquality.utils.AirQualityUtils;
import com.potus.app.potus.model.*;
import com.potus.app.potus.repository.ModifierRepository;
import com.potus.app.potus.repository.PotusModifierRepository;
import com.potus.app.potus.repository.PotusRepository;
import com.potus.app.potus.utils.EventsUtils;
import com.potus.app.potus.utils.PotusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import static com.potus.app.potus.utils.PotusUtils.generatePotusModifiers;


@Service
public class PotusEventsService {

    @Autowired
    PotusRepository potusRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    ModifierRepository modifierRepository;

    @Autowired
    PotusModifierRepository potusModifierRepository;


    public Potus doEvent (Potus potus, Double latitude, Double length) {

        GasesAndStates state = checkFestivity(potus);

        if (state == States.DEFAULT) {
            potus.setFestivityBonus(PotusUtils.FESTIVITY_DEFAULT_CURRENCY);

            List<Region> closestRegions = getClosestRegions(latitude, length);

            List<GasRegistry> gasValues = getGasValues(closestRegions);


            Map<DangerLevel, List<GasesAndStates>> dangerousGases = getDangerousGases(gasValues);

            System.out.println(state);

            state = chooseDangerousGas(dangerousGases);

            addDebuffs(potus, state);
        }
        else assignFestivityBonus(potus, state);

        applyState(potus, state);

        return potusRepository.save(potus);
    }

    private void addDebuffs(Potus potus, GasesAndStates state) {
        List<Modifier> chosenDebuffs = selectDebuffs(state);

        potus.setDebuffs(generatePotusModifiers(potus,chosenDebuffs));

        potusModifierRepository.saveAll(potus.getDebuffs());
    }

    private List<Modifier> selectDebuffs(GasesAndStates state) {
        List<Modifier> chosenDebuffs;

        if (Gases.NO2.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.WATERING_MODIFIER, false));
        } else if (Gases.NOX.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.WATERING_TIME, false));
        } else if (Gases.O3.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.HEALTH_GENERATION, false));
        } else if (Gases.PM1.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.PRUNE_CURRENCY_GENERATION, false));
        } else if (Gases.PM2_5.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.PRUNE_CURRENCY_GENERATION, false));
        } else if (Gases.PM10.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.PRUNE_CURRENCY_GENERATION, false));
        } else if (Gases.SO2.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.WATERING_MODIFIER, false));
        } else if (Gases.CO.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.HEALTH_REDUCTION, false));
        } else if (Gases.C6H6.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.HEALTH_GENERATION, false));
        } else if (Gases.Hg.equals(state)) {
            chosenDebuffs = new ArrayList<>(modifierRepository.findByTypeAndBuff(ModifierEffectType.HEALTH_REDUCTION, false));
        }
        else chosenDebuffs = new ArrayList<>();

        for(Modifier m : chosenDebuffs) {
            System.out.println(m.getName() + " " + m.getValue());
        }

        return chosenDebuffs;
    }

    private void assignFestivityBonus(Potus potus, GasesAndStates state) {
        potus.setFestivityBonus(PotusUtils.FESTIVITY_ADDITIONAL_CURRENCY);

    }

    private void applyState (Potus potus, GasesAndStates state) {
        potus.setState(state);

        System.out.println("Potus State: " + potus.getState());
    }

    private GasesAndStates checkFestivity(Potus potus) {
        GasesAndStates resultantState = States.DEFAULT;

        String date = EventsUtils.getDate();

        String month = (date.substring(5, 7));
        Integer day = Integer.valueOf(date.substring(8, 10));

        System.out.println(month);
        System.out.println(day);

        Map<String, GasesAndStates> festivitiesMonth = EventsUtils.getMonthFestivities(month);

        boolean festivityFound = false;
        Iterator<Map.Entry<String, GasesAndStates>> iterator = festivitiesMonth.entrySet().iterator();

        while (iterator.hasNext() && !festivityFound) {
            Map.Entry<String, GasesAndStates> entry = iterator.next();

            if (day >= Integer.parseInt(entry.getKey().substring(EventsUtils.StringPositionDay1Beginning, EventsUtils.StringPositionDay1Ending)) &&
            day <= Integer.parseInt(entry.getKey().substring(EventsUtils.StringPositionDay2Beginning, EventsUtils.StringPositionDay2Ending))) {
                festivityFound = true;
                resultantState = entry.getValue();
            }
        }

        return resultantState;
    }

    private GasesAndStates chooseDangerousGas(Map<DangerLevel, List<GasesAndStates>> dangerousGases) {
        Random rand = new SecureRandom();
        GasesAndStates randomGas = States.DEFAULT;

        Map<DangerLevel, List<GasesAndStates>> sortedMap = new TreeMap<>(dangerousGases);

        System.out.println(sortedMap);

        boolean found = false;
        Iterator<List<GasesAndStates>> iterator = sortedMap.values().iterator();
        while(! found && iterator.hasNext()){
            List<GasesAndStates> gasList = iterator.next();
            if(gasList.size() > 0){
                randomGas = gasList.get(rand.nextInt(gasList.size()));
                found = true;
            }
        }
        return randomGas;
    }


    private Map<DangerLevel, List<GasesAndStates>> getDangerousGases(List<GasRegistry> gasValues) {
        Map<DangerLevel, List<GasesAndStates>> dangerousGases = new HashMap<>();
        //initializeDangerousGases(dangerousGases);
        //Map<GasesAndStates, DangerLevel> dangerousGases = new HashMap<>();

        List<GasRegistry> dangerousGasesList = gasValues.stream().filter(gasRegistry ->
                !gasRegistry.getDangerLevel().equals(DangerLevel.NoDanger)).toList();

        for (DangerLevel danger : DangerLevel.values()) {

            List<Gases> values = gasValues.stream().filter(gasRegistry ->
                    gasRegistry.getDangerLevel().equals(danger)).map(GasRegistry::getName).toList();

            dangerousGases.put(danger, new ArrayList<>(values));
        }

        return dangerousGases;
    }

    private List<GasRegistry> getGasValues(List<Region> closestRegions) {
        List<Gases> remainingGases = AirQualityUtils.getGases();
        List<Gases> remainingGasesCopy = new ArrayList<>(remainingGases);
        List<GasRegistry> result = new ArrayList<>();
        int counter = 0;

        Iterator<Region> iterator = closestRegions.iterator();
        while (iterator.hasNext() && counter < 3) {
            Map<Gases, GasRegistry> regionGases = iterator.next().getRegistry();

            for (Gases remainingGas : remainingGases) {
                GasRegistry gasValues = regionGases.get(remainingGas);
                System.out.println(gasValues.getValue());
                if (gasValues.getValue() != null) {
                    remainingGasesCopy.remove(remainingGas);
                    result.add(gasValues);
                }
            }

            remainingGases = new ArrayList<>(remainingGasesCopy);
            counter += 1;

        }

        for (GasRegistry g : result) {
            System.out.println(g.getName() + " : " + g.getValue());
        }
        return result;
    }

    private List<Region> getClosestRegions(Double latitude, Double length) {
        Map<Region, Double> regionsDistance = new HashMap<>();
        List<Region> regions = regionRepository.findAll();

        for (Region region : regions) {
            String code = region.getCode();
            if (code != null) {
                regionsDistance.put(region, PotusUtils.euclideanDistance(latitude, length, region.getLatitude(), region.getLength()));
            }
        }

        Map<Region, Double> sortedRegions = regionsDistance.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return new ArrayList<Region>(sortedRegions.keySet());
    }
}
