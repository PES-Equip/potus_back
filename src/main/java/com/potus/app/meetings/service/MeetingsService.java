package com.potus.app.meetings.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.meetings.model.Meeting;
import com.potus.app.meetings.repository.MeetingsRepository;
import com.potus.app.meetings.utils.MeetingsUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import static com.potus.app.airquality.utils.AirQualityUtils.*;
import static com.potus.app.airquality.utils.AirQualityUtils.API_CODE_PARAM;
import static com.potus.app.meetings.utils.MeetingsUtils.*;

@Service
public class MeetingsService {
    Logger logger = LoggerFactory.getLogger(AirQualityService.class);

    @Autowired
    MeetingsRepository meetingsRepository;

    @Autowired
    AirQualityService airQualityService;

    public List<Meeting> processMeetingsInformation() throws ParseException {

        Object[] MeetingsInformationList = MeetingsUtils.getMeetingsInformation();
        List<Meeting> meetings = new ArrayList<>();
        if(MeetingsInformationList != null) {
            for(Object meetingObject : MeetingsInformationList) {
                Map<String, Object> meeting = (Map<String, Object>) meetingObject;
                Long id = Long.parseLong(String.valueOf(meeting.get(ID)));

                Date startDate;
                try {
                    startDate = new SimpleDateFormat(DATE_PATTERN)
                            .parse(String.valueOf(meeting.get(START_DATE)));
                } catch (ParseException e) {
                    startDate = null;
                    break;
                }
                Date endDate;
                try {
                    endDate = new SimpleDateFormat(DATE_PATTERN)
                            .parse(String.valueOf(meeting.get(END_DATE)));
                } catch (ParseException e) {
                    endDate = null;
                    break;
                }

                Region region = airQualityService.getRegion(Double.valueOf(String.valueOf(meeting.get(LATITUDE))),
                                                            Double.valueOf(String.valueOf(meeting.get(LENGTH))));
                String address = String.valueOf(meeting.get(ADDRESS));
                String city;
                try {
                    List<String> region_municipality_city = (List) meeting.get(COMARCA_I_MUNICIPI);
                    city = region_municipality_city.get(2);
                } catch (Exception e) {
                    city="";
                    break;
                }

                String title = String.valueOf(meeting.get(TITLE));
                String subtitle = String.valueOf(meeting.get(SUBTITLE));
                String url = "www.google.com";

                Meeting meetingCreated = new Meeting(id, startDate, endDate, region,
                                                    address, city, title, subtitle, url);
                meetings.add(meetingCreated);
            }
        }
        return meetings;
    }

}


