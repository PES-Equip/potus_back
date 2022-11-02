package com.potus.app.garden.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.ForbiddenException;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.garden.model.Garden;
import com.potus.app.garden.model.GardenMember;
import com.potus.app.garden.model.GardenRole;
import com.potus.app.garden.payload.request.GardenCreationRequest;
import com.potus.app.garden.payload.request.GardenDescriptionRequest;
import com.potus.app.garden.service.GardenService;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.payload.request.PotusCreationRequest;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.potus.app.exception.GeneralExceptionMessages.*;
import static com.potus.app.garden.utils.GardenExceptionMessages.*;
import static com.potus.app.potus.utils.PotusExceptionMessages.*;
import static com.potus.app.user.utils.UserUtils.getUser;
import static java.net.HttpURLConnection.*;
import static java.net.HttpURLConnection.HTTP_CONFLICT;

@RestController
@RequestMapping(value="/api/gardens")
@Api(tags= "Gardens",value = "Gardens endpoints")
public class GardenController {

    @Autowired
    GardenService gardenService;

    @Autowired
    UserService userService;

    @ApiOperation(value = "GET ALL GARDENS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Garden list"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping()
    public List<Garden> getGardens() {
        return gardenService.getAll();
    }

    @ApiOperation(value = "CREATE GARDEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Garden"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = BAD_REQUEST),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_CONFLICT, message = CONFLICT),
    })
    @PostMapping()
    public Garden createGarden(@RequestBody @Valid GardenCreationRequest body, Errors errors){

        if (errors.hasErrors())
            throw new BadRequestException(errors.getAllErrors().get(0).getDefaultMessage());

        User user = getUser();

        if(user.getGarden() != null)
            throw new ResourceAlreadyExistsException(USER_HAS_GARDEN);

        if(gardenService.existsByName(body.getName()))
            throw new ResourceAlreadyExistsException(GARDEN_NAME_ALREADY_EXISTS);

        GardenMember gardenMember = gardenService.createGarden(user, body.getName());
        user.setGarden(gardenMember);
        userService.saveUser(user);
        return gardenMember.getGarden();
    }

    @ApiOperation(value = "GET GARDEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Garden"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping("/{garden}")
    public Garden getGarden(@PathVariable String garden){
        return gardenService.findByName(garden);
    }

    @ApiOperation(value = "REMOVES GARDEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Garden removed"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_FORBIDDEN, message = FORBIDDEN),
    })
    @DeleteMapping("/{garden}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGarden(@PathVariable String garden){

        Garden selectedGarden = gardenService.findByName(garden);

        User user = getUser();

        GardenMember member = gardenService.findByUser(user);

        if(member.getGarden() != selectedGarden || member.getRole() != GardenRole.OWNER)
            throw new ForbiddenException();

        gardenService.deleteGarden(selectedGarden);
    }

    @ApiOperation(value = "GET USER GARDEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Garden"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping("/profile")
    public GardenMember getUserGarden(){
        User user = getUser();
        return gardenService.findByUser(user);
    }


    @ApiOperation(value = "MODIFY GARDEN DESCRIPTION")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Garden"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = BAD_REQUEST),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_FORBIDDEN, message = FORBIDDEN),
    })
    @PostMapping("/profile")
    public Garden editGardenDescription(@RequestBody @Valid GardenDescriptionRequest body, Errors errors){

        if (errors.hasErrors())
            throw new BadRequestException("Error");

        GardenMember member =  gardenService.findByUser(getUser());

        if(member.getRole().compareTo(GardenRole.ADMIN) < 0)
            throw new ForbiddenException();

        return gardenService.editDescription(member.getGarden(), body.getDescription());
    }

    @ApiOperation(value = "EXIT GARDEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Exit correctly"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = BAD_REQUEST),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = CONFLICT),
    })
    @DeleteMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exitGarden(){
        User user = getUser();
        GardenMember member =  gardenService.findByUser(user);
        gardenService.removeUser(member);
    }



}
