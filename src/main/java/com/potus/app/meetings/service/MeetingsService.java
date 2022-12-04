package com.potus.app.meetings.service;

import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.meetings.model.Meeting;
import com.potus.app.meetings.repository.MeetingsRepository;
import com.potus.app.meetings.utils.MeetingsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.potus.app.meetings.utils.MeetingsUtils.*;

@Service
public class MeetingsService {
    Logger logger = LoggerFactory.getLogger(AirQualityService.class);

    @Autowired
    MeetingsRepository meetingsRepository;

    @Autowired
    AirQualityService airQualityService;

    public List<Meeting> findAll() {
        List<Meeting> sortedMeetings = meetingsRepository.findAll();
        sortedMeetings.sort(Comparator.comparing(Meeting::getStartDate));
        return sortedMeetings;
    }

    public void updateMeetingsInformation() {
        Object[] MeetingsInformationList = MeetingsUtils.getMeetingsInformation();
        List<Meeting> meetings = new ArrayList<>();
        if(MeetingsInformationList != null) {
            for(Object meetingObject : MeetingsInformationList) {
                Boolean add = Boolean.TRUE;
                Map<String, Object> meeting = (Map<String, Object>) meetingObject;
                Long id = Long.parseLong(String.valueOf(meeting.get(ID)));

                Date startDate = new Date();
                try {
                    startDate = new SimpleDateFormat(DATE_PATTERN)
                            .parse(String.valueOf(meeting.get(START_DATE)));
                } catch (ParseException e) {
                    add = Boolean.FALSE;
                }
                Date endDate = new Date();
                try {
                    endDate = new SimpleDateFormat(DATE_PATTERN)
                            .parse(String.valueOf(meeting.get(END_DATE)));
                } catch (ParseException e) {
                    add = Boolean.FALSE;
                }

                Region region = new Region();
                try {
                    region = airQualityService.getRegion(Double.valueOf(String.valueOf(meeting.get(LATITUDE))),
                            Double.valueOf(String.valueOf(meeting.get(LENGTH))));
                } catch (NumberFormatException e) {
                    add = Boolean.FALSE;
                }

                String address = String.valueOf(meeting.get(ADDRESS));
                String city = "";
                try {
                    List<String> region_municipality_city = (List) meeting.get(COMARCA_I_MUNICIPI);
                    city = region_municipality_city.get(2);
                } catch (Exception e) {
                    add = Boolean.FALSE;
                }

                String title = String.valueOf(meeting.get(TITLE));
                String subtitle = String.valueOf(meeting.get(SUBTITLE));
                String url = "www.google.com";

                if(add) {
                    Meeting meetingCreated = new Meeting(id, startDate, endDate, region,
                            address, city, title, subtitle, url);
                    meetings.add(meetingCreated);
                    meetingsRepository.save(meetingCreated);
                }
            }
        }
    }

    public void deleteOldMeetings() {
        List<Meeting> meetings = findAll();
    }

    public List<Meeting> getMeetingDateInterval(String StartDate, String EndDate) throws ParseException {
        List<Meeting> meetingsDateInterval = new ArrayList<>();
        List<Meeting> meetings = findAll();
        Date StartDateParsed = new SimpleDateFormat(DATE_PATTERN)
                .parse(String.valueOf(StartDate));
        Date EndDateParsed = new SimpleDateFormat(DATE_PATTERN)
                .parse(String.valueOf(EndDate));

        for(Meeting meeting: meetings) {
            if((StartDateParsed.before(meeting.getStartDate()) || StartDateParsed.equals(meeting.getStartDate())) &&
                    (EndDateParsed.after(meeting.getEndDate()) || EndDateParsed.equals(meeting.getEndDate())))
                meetingsDateInterval.add(meeting);
        }
        meetingsDateInterval.sort(Comparator.comparing(Meeting::getStartDate));
        return meetingsDateInterval;
    }

}


