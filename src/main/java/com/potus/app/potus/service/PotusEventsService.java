package com.potus.app.potus.service;


import com.potus.app.airquality.model.*;
import com.potus.app.airquality.repository.RegionRepository;
import com.potus.app.airquality.utils.AirQualityUtils;
import com.potus.app.potus.model.GasesAndStates;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.States;
import com.potus.app.potus.repository.PotusRepository;
import com.potus.app.potus.utils.EventsUtils;
import com.potus.app.potus.utils.PotusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import static com.potus.app.potus.utils.EventsUtils.*;


@Service
public class PotusEventsService {

    @Autowired
    PotusRepository potusRepository;

    @Autowired
    RegionRepository regionRepository;


    public Potus doEvent (Potus potus, Double latitude, Double length) {

        GasesAndStates state = checkFestivity(potus);

        if (state == States.DEFAULT) {
            potus.setFestivityBonus(PotusUtils.FESTIVITY_DEFAULT_CURRENCY);

            List<Region> closestRegions = getClosestRegions(latitude, length);

            List<GasRegistry> gasValues = getGasValues(closestRegions);

            Map<DangerLevel, List<GasesAndStates>> dangerousGases = getDangerousGases(gasValues);

            System.out.println(state);

            state = chooseDangerousGas(dangerousGases);
        }
        else assignFestivityBonus(potus, state);

        applyState(potus, state);

        return potusRepository.save(potus);
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

        if (sortedMap.get(DangerLevel.Hazardous).size() > 1) sortedMap.get(DangerLevel.Hazardous).remove(States.DEFAULT);

        for (List<GasesAndStates> gasList: sortedMap.values()) {
            randomGas = gasList.get(rand.nextInt(gasList.size()));
            if (randomGas != States.DEFAULT) break;
        }

        return randomGas;
    }

    private Map<DangerLevel, List<GasesAndStates>> getDangerousGases(List<GasRegistry> gasValues) {
        Map<DangerLevel, List<GasesAndStates>> dangerousGases = new HashMap<>();
            initializeDangerousGases(dangerousGases);
        //Map<GasesAndStates, DangerLevel> dangerousGases = new HashMap<>();

        for (GasRegistry gas : gasValues) {

            determineDangerousness(gas.getName(), gas.getValue(), dangerousGases);

        }

        return dangerousGases;
    }

    private void initializeDangerousGases(Map<DangerLevel, List<GasesAndStates>> dangerousGases) {
        for (DangerLevel danger : DangerLevel.values()) {
            dangerousGases.put(danger, new ArrayList<>());
            dangerousGases.get(danger).add(States.DEFAULT);
        }

    }

    private void determineDangerousness(Gases gasName, Double gasValue, Map<DangerLevel, List<GasesAndStates>> dangerousGases) {
        if (gasValue != null) {
            switch(gasName) {
                case NO2, NOX:
                    if (gasValue >= NOXLow && gasValue < NOXModerate) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= NOXModerate && gasValue < NOXHigh) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= NOXHigh && gasValue < NOXHazardous) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= NOXHazardous) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case O3:
                    if (gasValue >= O3Low && gasValue < O3Moderate) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= O3Moderate && gasValue < O3High) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= O3High && gasValue < O3Hazardous) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= O3Hazardous) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case PM1:
                    if (gasValue >= PM1Low && gasValue < PM1Moderate) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= PM1Moderate && gasValue < PM1High) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= PM1High && gasValue < PM1Hazardous) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= PM1Hazardous) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case PM2_5:
                    if (gasValue >= PM2_5Low && gasValue < PM2_5Moderate) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= PM2_5Moderate && gasValue < PM2_5High) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= PM2_5High && gasValue < PM2_5Hazardous) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= PM2_5Hazardous) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case PM10:
                    if (gasValue >= PM10Low && gasValue < PM10Moderate) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= PM10Moderate && gasValue < PM10High) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= PM10High && gasValue < PM10Hazardous) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= PM10Hazardous) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case SO2:
                    if (gasValue >= SO2Low && gasValue < SO2Moderate) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= SO2Moderate && gasValue < SO2High) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= SO2High && gasValue < SO2Hazardous) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= SO2Hazardous) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case CO:
                    if (gasValue >= COLow && gasValue < COModerate) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= COModerate && gasValue < COHigh) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= COHigh && gasValue < COHazardous) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= COHazardous) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case C6H6:
                    if (gasValue >= C6H6Low && gasValue < C6H6Moderate) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= C6H6Moderate && gasValue < C6H6High) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= C6H6High && gasValue < C6H6Hazardous) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= C6H6Hazardous) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case Hg:
                    if (gasValue >= HgLow && gasValue < HgModerate) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= HgModerate && gasValue < HgHigh) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= HgHigh && gasValue < HgHazardous) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= HgHazardous) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                default:
                    break;
            }
        }

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
                if (!gasValues.getValue().equals(0.0)) {
                    remainingGasesCopy.remove(remainingGas);
                    result.add(gasValues);
                }
            }

            remainingGases = new ArrayList<>(remainingGasesCopy);
            counter += 1;

        }

        /*for (Region region : closestRegions) {
            System.out.println("counter :" + counter);
            Map<Gases, GasRegistry> regionGases = region.getRegistry();

            for (Gases remainingGas : remainingGases) {
                GasRegistry gasValues = regionGases.get(remainingGas);
                System.out.println(gasValues.getValue());
                if (!gasValues.getValue().equals(0.0)) {
                    remainingGasesCopy.remove(remainingGas);
                    result.add(gasValues);
                }
            }
            remainingGases = remainingGasesCopy;
            counter += 1;

            if (remainingGases.isEmpty() || counter > 2) break;
        } */

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
