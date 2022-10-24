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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.potus.app.potus.utils.PotusExceptionMessages.*;
import static com.potus.app.potus.utils.PotusUtils.ACTION_CURRENCY;
import static com.potus.app.user.utils.UserUtils.getUser;

@RestController
@RequestMapping(value="/api/potus")
public class PotusActionsController {

    @Autowired
    PotusService potusService;

    @Autowired
    UserService userService;

    @GetMapping("")
    public Potus getPotus() {
        User user = getUser();
        return user.getPotus();
    }


    @PostMapping("/action")
    public User doAction(@RequestBody @Valid PotusActionRequest body, Errors errors){

        if(errors.hasErrors())
            throw new BadRequestException(ACTION_IS_NULL);

        Actions action = body.getAction();
        User user = getUser();
        Potus potus = (Potus) user.getPotus();

        Integer reward = potusService.doFilterAction(potus,action);

        userService.addCurrency(user, reward);
        return user;
    }
}
