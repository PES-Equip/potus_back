package com.potus.app.potus.service;


import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.model.Regions;
import com.potus.app.airquality.repository.RegionRepository;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.repository.PotusRepository;
import com.potus.app.potus.utils.PotusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class PotusEventsService {

    @Autowired
    PotusRepository potusRepository;

    @Autowired
    RegionRepository regionRepository;

    public void doEvent (Potus potus, Double latitude, Double length) {
        List<Region> closestRegions = getClosestRegions(latitude, length);

    }

    public List<Region> getClosestRegions(Double latitude, Double length) {
        Map<Region, Double> regionsDistance = new HashMap<>();
        List<Region> regions = regionRepository.findAll();

        for (Region region : regions) {
            System.out.println(region.getCode());

            regionsDistance.put(region, PotusUtils.euclideanDistance(latitude, length, region.getLatitude(), region.getLength()));
            
        }
        Stream<Map.Entry<Region,Double>> sortedRegions = regionsDistance.entrySet().stream().sorted(Map.Entry.comparingByValue());


        return null;
    }
}
