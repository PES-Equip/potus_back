package com.potus.app.user.controller;


import com.nimbusds.jose.shaded.json.JSONObject;
import com.potus.app.user.exception.ResourceAlreadyExistsException;
import com.potus.app.user.exception.ResourceNotFoundException;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("")
    public List<User> getUsers() {
        return userService.getAll();
    }


    @GetMapping("/profile")
    public User getUser(Authentication auth){
        return (User) auth.getPrincipal();
    }

    @PostMapping("/profile")
    public User setUsername(Authentication auth, @RequestBody User body){
        String username = body.getUsername();
        User user = (User) auth.getPrincipal();
        try{
            User userExist = userService.findByUsername(username);
            throw new ResourceAlreadyExistsException("User with username", username);
        } catch (ResourceNotFoundException ignored) {}

        user.setUsername(username);
        return userService.saveUser(user);
    }

    @GetMapping("/status")
    public JSONObject getStatus(Authentication auth){
        User user = (User) auth.getPrincipal();

        JSONObject response = new JSONObject();
        response.put("status",user.getStatus());
        return response;
    }



}
