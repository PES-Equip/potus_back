
package com.potus.app.meetings.controller;

import com.potus.app.meetings.model.Meeting;
import com.potus.app.meetings.service.MeetingsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/api/meetings")
@Api(tags= "Meetings",value = "Meetings endpoints")
public class MeetingsController {
    @Autowired
    private MeetingsService meetingsService;

    // THIS HAVE TO BE MOVED TO THE SCHEDULER - this is only to test that update works
    @GetMapping(value="information")
    public List<Meeting> updateMeetings() throws ParseException {
        meetingsService.updateMeetingsInformation();
        return meetingsService.findAll();
    }

    // THIS HAVE TO BE MOVED TO THE SCHEDULER - this is only to test that update works
    @GetMapping(value="/delete")
    public void aaaa() {
        meetingsService.deleteOldMeetings();
    }

    @GetMapping(value="")
    public List<Meeting> getMeetings() throws ParseException {
        return meetingsService.findAll();
    }

    @GetMapping(value="/date_interval/{start_date}&{end_date}")
    public List<Meeting> getMeetingsDateInterval(@PathVariable String start_date, @PathVariable String end_date) throws ParseException {
        return meetingsService.getMeetingDateInterval(start_date, end_date);
    }

    @GetMapping(value="/region/{latitude}&{length}")
    public List<Meeting> getClosedMeetingsLatitudeAndLength(@PathVariable Double latitude, @PathVariable Double length) {
        return meetingsService.getMeetingsLatLen(latitude,length);
    }




}
