package com.potus.app.config;

import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.meetings.service.MeetingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    AirQualityService airQualityService;

    @Autowired
    MeetingsService meetingsService;

    @Scheduled(cron = "0 30 1 ? * *")
    public void cronUpdateRegions(){
        airQualityService.updateRegionGasData();
    }

    @Scheduled(cron = "0 45 1 ? * *")
    public void cronUpdateMeetings(){
        meetingsService.updateMeetingsInformation();
    }

    @Scheduled(cron = "0 55 1 ? * *")
    public void cronDeleteOldMeetings(){
        meetingsService.deleteOldMeetings();
    }





}
