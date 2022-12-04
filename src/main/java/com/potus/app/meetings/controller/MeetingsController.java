
package com.potus.app.meetings.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.potus.app.meetings.model.Meeting;
import com.potus.app.meetings.service.MeetingsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/api/meetings")
@Api(tags= "Meetings",value = "Meetings endpoints")
public class MeetingsController {
    @Autowired
    private MeetingsService meetingsService;

    @GetMapping(value="information")
    public List<Meeting> getMeetingsInformation() throws ParseException {
        return meetingsService.processMeetingsInformation();
    }
}
