package com.potus.app.user.controller;


import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.PotusRegistry;
import com.potus.app.potus.payload.request.PotusCreationRequest;
import com.potus.app.potus.service.PotusRegistryService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserStatus;
import com.potus.app.user.payload.request.UsernameRequest;
import com.potus.app.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

import static com.potus.app.exception.GeneralExceptionMessages.*;
import static com.potus.app.potus.utils.PotusExceptionMessages.*;
import static com.potus.app.user.utils.UserExceptionMessages.*;
import static com.potus.app.user.utils.UserUtils.getUser;
import static java.net.HttpURLConnection.*;

@RestController
@RequestMapping(value="/api/user")
@Api(tags= "User",value = "User endpoints")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PotusRegistryService potusRegistryService;

    @Autowired
    private PotusService potusService;

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
        return userService.createPotus(user, "potus");
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

    @ApiOperation(value = "CHANGE USERNAME")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = BAD_REQUEST),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_CONFLICT, message = CONFLICT),
    })
    @PostMapping("/profile")
    public User changeUsername(@RequestBody @Valid UsernameRequest body, Errors errors){

        if (errors.hasErrors())
            throw new BadRequestException(USERNAME_CANT_BE_NULL);

        User user = getUser();
        String username = body.getUsername();
        if(user.getUsername().equals(username))
            throw new BadRequestException(USERNAME_IS_SAME);

        return  userService.setUsername(user, username);
    }

    @ApiOperation(value = "DELETES ACCOUNT")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "User deleted correctly"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = BAD_REQUEST),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @DeleteMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteAccount(){
        User user = getUser();
        potusRegistryService.deleteRegistries(user);
        userService.deleteUser(user);
    }

    @ApiOperation(value = "CREATE POTUS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Potus"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = BAD_REQUEST),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_CONFLICT, message = CONFLICT),
    })
    @PostMapping("/profile/potus")
    public Potus createPotus(@RequestBody @Valid PotusCreationRequest body, Errors errors){

        if (errors.hasErrors())
            throw new BadRequestException(POTUS_NAME_IS_NULL);

        User user = getUser();


        if(user.getPotus().isAlive())
            throw new ResourceAlreadyExistsException(POTUS_ALREADY_ALIVE);

        if(potusRegistryService.existsByUserAndName(user, body.getName()))
            throw new ResourceAlreadyExistsException(POTUS_NAME_ALREADY_EXISTS);

        return potusService.restartPotus(user.getPotus(), body.getName());
    }

    @ApiOperation(value = "GET POTUS REGISTRIES")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Potus registries"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping("/profile/history")
    public List<PotusRegistry> getPotusRegistry(){
        return potusRegistryService.findByUser(getUser());
    }


}
