package com.potus.app.potus.controller;

import com.potus.app.exception.BadRequestException;
import com.potus.app.potus.model.Actions;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.payload.request.PotusActionRequest;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.potus.app.potus.utils.PotusExceptionMessages.*;
import static com.potus.app.potus.utils.PotusUtils.ACTION_CURRENCY;

@RestController
@RequestMapping(value="/api/potus")
public class PotusActionsController {

    @Autowired
    PotusService potusService;

    @Autowired
    UserService userService;

    @GetMapping("")
    public Potus getPotus(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return user.getPotus();
    }

    @PostMapping("/action")
    public Potus doAction(Authentication auth, @RequestBody @Valid PotusActionRequest body, Errors errors){

        if(errors.hasErrors())
            throw new BadRequestException(ACTION_IS_NULL);

        Actions action = body.getAction();
        User user = (User) auth.getPrincipal();
        Potus potus = (Potus) user.getPotus();

        potusService.doFilterAction(potus,action);
        userService.addCurrency(user, ACTION_CURRENCY);
        return potus;
    }
}
