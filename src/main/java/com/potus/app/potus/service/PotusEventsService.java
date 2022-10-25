package com.potus.app.potus.service;


import com.potus.app.airquality.model.*;
import com.potus.app.airquality.repository.RegionRepository;
import com.potus.app.airquality.utils.AirQualityUtils;
import com.potus.app.potus.model.GasesAndStates;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.States;
import com.potus.app.potus.repository.PotusRepository;
import com.potus.app.potus.utils.PotusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PotusEventsService {

    @Autowired
    PotusRepository potusRepository;

    @Autowired
    RegionRepository regionRepository;


    public Potus doEvent (Potus potus, Double latitude, Double length) {
        List<Region> closestRegions = getClosestRegions(latitude, length);

        List<GasRegistry> gasValues = getGasValues(closestRegions);

        Map<DangerLevel, List<GasesAndStates>> dangerousGases = getDangerousGases(gasValues);

        GasesAndStates state = chooseDangerousGas(dangerousGases);

        applyState(potus, state);

        return potusRepository.save(potus);
    }

    private void applyState (Potus potus, GasesAndStates state) {
        potus.setState(state);

        System.out.println("Potus State: " + potus.getState());
    }

    private GasesAndStates chooseDangerousGas(Map<DangerLevel, List<GasesAndStates>> dangerousGases) {
        Random rand = new SecureRandom();
        GasesAndStates randomGas = States.DEFAULT;

        for (List<GasesAndStates> gasList: dangerousGases.values()) {
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
                    if (gasValue >= 25 && gasValue < 50) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= 50 && gasValue < 100) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= 100 && gasValue < 200) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= 200) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case O3:
                    if (gasValue >= 60 && gasValue < 120) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= 120 && gasValue < 180) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= 180 && gasValue < 240) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= 240) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case PM1:
                    if (gasValue >= 10 && gasValue < 20) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= 20 && gasValue < 30) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= 30 && gasValue < 60) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= 60) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case PM2_5:
                    if (gasValue >= 5 && gasValue < 15) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= 15 && gasValue < 25) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= 25 && gasValue < 50) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= 50) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case PM10:
                    if (gasValue >= 15 && gasValue < 30) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= 30 && gasValue < 50) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= 50 && gasValue < 80) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= 80) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case SO2:
                    if (gasValue >= 40 && gasValue < 80) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= 80 && gasValue < 120) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= 120 && gasValue < 240) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= 240) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case CO:
                    if (gasValue >= 4 && gasValue < 8) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= 8 && gasValue < 12) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= 12 && gasValue < 24) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= 24) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case C6H6:
                    if (gasValue >= 1.7 && gasValue < 3.4) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= 3.4 && gasValue < 7) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= 7 && gasValue < 15) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= 15) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
                    break;
                case Hg:
                    if (gasValue >= 10 && gasValue < 20) dangerousGases.get(DangerLevel.Low).add(gasName);
                    else if (gasValue >= 20 && gasValue < 40) dangerousGases.get(DangerLevel.Moderate).add(gasName);
                    else if (gasValue >= 40 && gasValue < 80) dangerousGases.get(DangerLevel.High).add(gasName);
                    else if (gasValue >= 80) dangerousGases.get(DangerLevel.Hazardous).add(gasName);
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

        for (Region region : closestRegions) {
            Map<Gases, GasRegistry> regionGases = region.getRegistry();

            for (Gases remainingGas : remainingGases) {
                GasRegistry gasValues = regionGases.get(remainingGas);
                if (gasValues != null) {
                    remainingGasesCopy.remove(remainingGas);
                    result.add(gasValues);
                }
            }
            remainingGases = remainingGasesCopy;

            if (remainingGases.isEmpty()) break;
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
