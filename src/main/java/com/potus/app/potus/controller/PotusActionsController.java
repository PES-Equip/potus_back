package com.potus.app.potus.controller;

import com.potus.app.potus.model.Potus;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/api/potus")
public class PotusActionsController {

    @Autowired
    PotusService potusService;

    @GetMapping("")
    public Potus getPotus(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return user.getPotus();
    }
}
