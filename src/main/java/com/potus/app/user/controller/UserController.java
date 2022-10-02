package com.potus.app.user.controller;


import com.potus.app.user.exception.ResourceAlreadyExistsException;
import com.potus.app.user.exception.ResourceNotFoundException;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import com.sun.tools.jconsole.JConsoleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<User> getUsers() {
        return userService.getAll();
    }

    @PostMapping("")
    public User createUser(Principal principal, @RequestBody User user) {
        String username = user.getUsername();
        String id = principal.getName();

        if(userService.exists(id)){
            throw new ResourceAlreadyExistsException("User with id", id);
        }
        try{
            User userExist = userService.findByUsername(username);
            throw new ResourceAlreadyExistsException("User with username", username);
        } catch (ResourceNotFoundException ignored) {}

        String email = "test";
        User newUser = new User(id,email,username);
        return userService.saveUser(newUser);
    }

    @GetMapping("/profile")
    public User getUser(Principal principal){

        return userService.findById(principal.getName());
    }



}
