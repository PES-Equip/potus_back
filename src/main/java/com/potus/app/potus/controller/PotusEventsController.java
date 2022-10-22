package com.potus.app.potus.controller;

import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.exception.BadRequestException;
import com.potus.app.potus.model.Actions;
import com.potus.app.potus.model.Events;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.payload.request.PotusActionRequest;
import com.potus.app.potus.payload.request.PotusEventRequest;
import com.potus.app.potus.service.PotusEventsService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.potus.app.potus.utils.PotusExceptionMessages.ACTION_IS_NULL;
import static com.potus.app.potus.utils.PotusExceptionMessages.COORDINATES_ARE_NULL;

@RestController
@RequestMapping(value="/api/potus/events")
public class PotusEventsController {

    @Autowired
    PotusEventsService potusEventsService;

    @Autowired
    AirQualityService airQualityService;

    @PostMapping("/event")
    public Potus doEvent(Authentication auth, @RequestBody @Valid PotusEventRequest body, Errors errors) {

        if(errors.hasErrors())
            throw new BadRequestException(COORDINATES_ARE_NULL);

        User user = (User) auth.getPrincipal();
        Potus potus = (Potus) user.getPotus();
        Double latitude = body.getLatitude();
        Double length = body.getLength();

        potusEventsService.doEvent(potus, latitude, length);

        return potus;
    }

    }
