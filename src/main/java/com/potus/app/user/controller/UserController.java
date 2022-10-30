package com.potus.app.user.controller;


import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
import com.potus.app.user.payload.request.UsernameRequest;
import com.potus.app.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.potus.app.exception.GeneralExceptionMessages.*;
import static com.potus.app.user.utils.UserExceptionMessages.USERNAME_CANT_BE_NULL;
import static com.potus.app.user.utils.UserExceptionMessages.USER_PROFILE_ALREADY_EXISTS;
import static com.potus.app.user.utils.UserUtils.getUser;
import static java.net.HttpURLConnection.*;

@RestController
@RequestMapping(value="/api/user")
@Api(tags= "User",value = "User endpoints")
public class UserController {

    @Autowired
    private UserService userService;


    @ApiOperation(value = "GET USERS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping()
    public List<User> getUsers() {
        return userService.getAll();
    }

    @ApiOperation(value = "CREATE PROFILE")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = BAD_REQUEST),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_CONFLICT, message = CONFLICT),
    })
    @PostMapping()
    public User createProfile(@RequestBody @Valid UsernameRequest body, Errors errors){

        if (errors.hasErrors())
            throw new BadRequestException(USERNAME_CANT_BE_NULL);


        User user = getUser();
        String username = body.getUsername();

        if(user.getStatus() != UserStatus.NEW)
            throw new ResourceAlreadyExistsException(USER_PROFILE_ALREADY_EXISTS);

        userService.setUsername(user, username);
        return userService.createPotus(user);
    }

    @ApiOperation(value = "GET PROFILE")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping("/profile")
    public User getProfile(){
        return getUser();
    }

}
