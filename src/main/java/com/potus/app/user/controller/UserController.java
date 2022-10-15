package com.potus.app.user.controller;


import com.nimbusds.jose.shaded.json.JSONObject;
import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
import com.potus.app.user.payload.request.UsernameRequest;
import com.potus.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.potus.app.user.utils.UserExceptionMessages.*;

@RestController
@RequestMapping(value="/api/user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("")
    public List<User> getUsers() {
        return userService.getAll();
    }

    @PostMapping("")
    public User createProfile(Authentication auth, @RequestBody @Valid UsernameRequest body, Errors errors){

        if (errors.hasErrors())
            throw new BadRequestException(USERNAME_CANT_BE_NULL);


        User user = (User) auth.getPrincipal();
        String username = body.getUsername();

        if(user.getStatus() != UserStatus.NEW)
            throw new ResourceAlreadyExistsException(USER_PROFILE_ALREADY_EXISTS);

        userService.setUsername(user, username);
        return userService.createPotus(user);
    }

    @GetMapping("/profile")
    public User getUser(Authentication auth){
        return (User) auth.getPrincipal();
    }


    @PostMapping("/profile")
    public User setUsername(Authentication auth, @RequestBody @Valid UsernameRequest body, Errors errors){

        if (errors.hasErrors())
            throw new BadRequestException(USERNAME_CANT_BE_NULL);

        String username = body.getUsername();
        User user = (User) auth.getPrincipal();

        return userService.setUsername(user,username);
    }

    @GetMapping("/status")
    public JSONObject getStatus(Authentication auth){
        User user = (User) auth.getPrincipal();

        JSONObject response = new JSONObject();
        response.put("status",user.getStatus());
        return response;
    }



}
