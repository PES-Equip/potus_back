package com.potus.app.potus.controller;

import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.potus.model.Actions;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.payload.request.PotusActionRequest;
import com.potus.app.potus.payload.request.PotusCreationRequest;
import com.potus.app.potus.payload.request.PotusEventRequest;
import com.potus.app.potus.service.PotusEventsService;
import com.potus.app.potus.service.PotusRegistryService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.potus.app.exception.GeneralExceptionMessages.*;
import static com.potus.app.potus.utils.PotusExceptionMessages.*;
import static com.potus.app.user.utils.UserUtils.getActionFormatted;
import static com.potus.app.user.utils.UserUtils.getUser;
import static java.net.HttpURLConnection.*;
import static java.net.HttpURLConnection.HTTP_CONFLICT;

@RestController
@RequestMapping(value="/api/potus")
@Api(tags= "Potus",value = "Potus endpoints")
public class PotusController {

    @Autowired
    private PotusService potusService;

    @Autowired
    private UserService userService;

    @Autowired
    private PotusEventsService potusEventsService;


    @ApiOperation(value = "GET POTUS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User potus"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping()
    public Potus getPotus() {
        User user = getUser();
        return user.getPotus();
    }

    @ApiOperation(value = "POTUS ACTION")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "ACTION RESULT"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = BAD_REQUEST),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @PostMapping("/action")
    public User doAction(@RequestBody @Valid PotusActionRequest body, Errors errors){

        if(errors.hasErrors())
            throw new BadRequestException(ACTION_IS_NULL);

        Actions action = getActionFormatted(body.getAction());
        User user = getUser();
        Potus potus = user.getPotus();

        Integer reward = potusService.doFilterAction(potus,action);
        return userService.addCurrency(user, reward);
    }

    @ApiOperation(value = "POTUS EVENT")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "EVENT RESULT"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = BAD_REQUEST),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @PostMapping("/events")
    public Potus doEvent(@RequestBody @Valid PotusEventRequest body, Errors errors) {

        if(errors.hasErrors())
            throw new BadRequestException(COORDINATES_ARE_NULL);

        User user = getUser();
        Double latitude = body.getLatitude();
        Double length = body.getLength();



        return potusEventsService.doEvent(user.getPotus(), latitude, length);
    }
}
