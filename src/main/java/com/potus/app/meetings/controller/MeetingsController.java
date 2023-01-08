
package com.potus.app.meetings.controller;

import com.potus.app.exception.BadRequestException;
import com.potus.app.meetings.model.Meeting;
import com.potus.app.meetings.service.MeetingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.potus.app.exception.GeneralExceptionMessages.UNAUTHENTICATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

@RestController
@RequestMapping(value="/api/meetings")
@Api(tags= "Meetings",value = "Meetings endpoints")
public class MeetingsController {
    @Autowired
    private MeetingsService meetingsService;

    @ApiOperation(value = "GET MEETINGS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Meeting list"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping
    public List<Meeting> getMeetings(@RequestParam(value = "start_date", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
                                     @RequestParam(value = "end_date", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
                                     @RequestParam(value = "latitude", required = false) Double latitude,  @RequestParam(value = "length", required = false) Double length){

        if(endDate != null && startDate != null)
            return meetingsService.getMeetingDateInterval(startDate, endDate);

        if(latitude != null && length != null)
            return meetingsService.getMeetingsLatLen(latitude,length);

        return meetingsService.findAll();
    }

}
