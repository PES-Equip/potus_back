package com.potus.app.garden.controller;

import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.ForbiddenException;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.garden.model.*;
import com.potus.app.garden.payload.request.GardenCreationRequest;
import com.potus.app.garden.payload.request.GardenDescriptionRequest;
import com.potus.app.garden.payload.request.GardenSetRoleRequest;
import com.potus.app.garden.payload.response.GardenMemberResponse;
import com.potus.app.garden.service.GardenRequestService;
import com.potus.app.garden.service.GardenService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.potus.app.exception.GeneralExceptionMessages.*;
import static com.potus.app.garden.model.GardenRequestType.USER_INVITATION_REQUEST;
import static com.potus.app.garden.model.GardenRequestType.GROUP_JOIN_REQUEST;
import static com.potus.app.garden.utils.GardenExceptionMessages.*;
import static com.potus.app.garden.utils.GardenUtils.getGardenRoleFormatted;
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
    GardenRequestService gardenRequestService;

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

        if(gardenService.existsByName(body.getName()) || body.getName().equals("profile"))
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
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
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
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
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

    @ApiOperation(value = "REMOVE GARDEN MEMBER")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Garden member removed"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_FORBIDDEN, message = FORBIDDEN),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
    })
    @DeleteMapping("/{garden}/{user}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable String garden, @PathVariable String user){

        Garden selectedGarden = gardenService.findByName(garden);

        GardenMember member = gardenService.findByUser(getUser());

        if(member.getGarden() != selectedGarden || member.getRole().compareTo(GardenRole.NORMAL) == 0)
            throw new ForbiddenException();

        User userRequest = userService.findByUsername(user);
        GardenMember memberRequest = gardenService.findByUser(userRequest);

        if(memberRequest.getGarden() != selectedGarden)
            throw new ResourceNotFoundException(USER_NOT_MEMBER);

        if(member.getRole().compareTo(memberRequest.getRole()) < 1)
            throw new ForbiddenException();



        gardenService.removeUser(memberRequest);
    }

    @ApiOperation(value = "SET MEMBER ROLE")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Set member role"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_FORBIDDEN, message = FORBIDDEN),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
    })
    @PutMapping("/{garden}/{user}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setMemberRole(@PathVariable String garden, @PathVariable String user, @RequestBody @Valid GardenSetRoleRequest body, Errors errors) {

        if (errors.hasErrors())
            throw new BadRequestException(ROLE_IS_NOT_DEFINED);

        GardenRole role = getGardenRoleFormatted(body.getRole());
        Garden selectedGarden = gardenService.findByName(garden);
        GardenMember member = gardenService.findByUser(getUser());

        if (member.getGarden() != selectedGarden || member.getRole().compareTo(GardenRole.NORMAL) == 0)
            throw new ForbiddenException();

        User userRequest = userService.findByUsername(user);
        GardenMember memberRequest = gardenService.findByUser(userRequest);

        if(memberRequest.getGarden() != selectedGarden)
            throw new ResourceNotFoundException(USER_NOT_MEMBER);

        //NO HACE FALTA REALMENTE PERO BUENO
        if (member.equals(memberRequest))
            throw new BadRequestException(USER_CAN_NOT_EDIT_ITSELF);

        if (member.getRole().compareTo(memberRequest.getRole()) < 1 || member.getRole().compareTo(role) < 0)
            throw new ForbiddenException();

        if (role.equals(GardenRole.OWNER)) {
            gardenService.changeOwner(member, memberRequest);
        } else {
            gardenService.changeRole(memberRequest, role);
        }
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
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
    })
    @PutMapping("/profile")
    public Garden editGardenDescription(@RequestBody @Valid GardenDescriptionRequest body, Errors errors){

        if (errors.hasErrors())
            throw new BadRequestException("Error on description");

        GardenMember member =  gardenService.findByUser(getUser());

        if(member.getRole().compareTo(GardenRole.ADMIN) < 0)
            throw new ForbiddenException();

        return gardenService.editDescription(member.getGarden(), body.getDescription());
    }

    @ApiOperation(value = "EXIT GARDEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Exit correctly"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
    })
    @DeleteMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exitGarden(){
        User user = getUser();
        GardenMember member =  gardenService.findByUser(user);
        gardenService.removeUser(member);
    }

    @ApiOperation(value = "GET GARDEN MEMBERS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Garden"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping("/profile/members")
    public List<GardenMemberResponse> getGardenMembers(){
        User user = getUser();
        GardenMember member = gardenService.findByUser(user);
        Garden garden = member.getGarden();
        List<GardenMember> members = gardenService.getMembers(garden);
        List<GardenMemberResponse> response = new ArrayList<>();

        members.forEach(m -> {
            response.add(new GardenMemberResponse(m.getUser().getUsername(),m.getRole()));
        });

        return response;
    }

    // REQUESTS

    @ApiOperation(value = "GET ALL USER GARDEN INVITATION REQUESTS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User garden invitation list"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping("/profile/requests")
    public List<Garden> getUserGardenRequests() {

        User user = getUser();

        gardenRequestService.validateUserRequests(user);
        List<GardenRequest> gardenRequests = gardenRequestService.findUserRequests(user);

        return gardenRequests.stream().map(GardenRequest::getGarden).collect(Collectors.toList());
    }

    @ApiOperation(value = "CREATE A GROUP JOIN REQUEST")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_CREATED, message = "Join request created"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
            @ApiResponse(code = HTTP_CONFLICT, message = CONFLICT),
    })
    @PostMapping("/profile/requests/{garden}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createInvitation(@PathVariable String garden) {

        Garden selectedGarden = gardenService.findByName(garden);
        User user = getUser();

        if (user.getGarden() != null)
            throw new BadRequestException(USER_HAS_GARDEN);

        if(gardenRequestService.existsRequest(selectedGarden, user))
            throw new ResourceAlreadyExistsException(REQUEST_ALREADY_EXISTS);

        gardenRequestService.createRequest(user,selectedGarden, GROUP_JOIN_REQUEST);
    }

    @ApiOperation(value = "ACCEPT A GROUP INVITATION")
    @ApiResponses(value = {
    @ApiResponse(code = HTTP_CREATED, message = "Garden member"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
            @ApiResponse(code = HTTP_CONFLICT, message = CONFLICT),
    })
    @PutMapping("/profile/requests/{garden}")
    @ResponseStatus(HttpStatus.CREATED)
    public GardenMember acceptInvitation(@PathVariable String garden) {

        Garden selectedGarden = gardenService.findByName(garden);
        User user = getUser();

        GardenRequest gardenRequest = gardenRequestService.findRequest(user,selectedGarden);

        if(gardenRequest.getType().equals(GROUP_JOIN_REQUEST))
            throw new ResourceNotFoundException(REQUEST_NOT_FOUND);


        GardenMember gardenMember = gardenService.addUser(selectedGarden, user);

        gardenRequestService.deleteUserRequests(user);
        return gardenMember;
    }


    @ApiOperation(value = "DENY A GROUP INVITATION")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Denied correctly"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
    })
    @DeleteMapping("/profile/requests/{garden}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void denyInvitation(@PathVariable String garden) {

        Garden selectedGarden = gardenService.findByName(garden);
        User user = getUser();

        GardenRequest gardenRequest = gardenRequestService.findRequest(user,selectedGarden);

        if(gardenRequest.getType().equals(GROUP_JOIN_REQUEST))
            throw new ResourceNotFoundException(REQUEST_NOT_FOUND);


        gardenRequestService.deleteRequest(gardenRequest);
    }

    @ApiOperation(value = "GET ALL GARDEN JOIN REQUESTS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Garden join requests"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
    })
    @GetMapping("/{garden}/requests")
    public List<User> getGardenRequests(@PathVariable String garden) {

        Garden selectedGarden = gardenService.findByName(garden);

        GardenMember member = gardenService.findByUser(getUser());
        if(member.getGarden() != selectedGarden || member.getRole().compareTo(GardenRole.NORMAL) == 0)
            throw new ForbiddenException();

        gardenRequestService.validateGardenRequests(selectedGarden);
        List<GardenRequest> gardenRequests = gardenRequestService.findGardenRequests(selectedGarden);

        return gardenRequests.stream().map(GardenRequest::getUser).collect(Collectors.toList());
    }

    @ApiOperation(value = "CREATE A  USER INVITATION REQUEST")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_CREATED, message = "User invitation created correctly"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
            @ApiResponse(code = HTTP_CONFLICT, message = CONFLICT),
    })
    @PostMapping("/{garden}/requests/{user}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createJoin(@PathVariable String garden, @PathVariable String user) {


        Garden selectedGarden = gardenService.findByName(garden);
        GardenMember member = gardenService.findByUser(getUser());

        if(member.getGarden() != selectedGarden || member.getRole().compareTo(GardenRole.NORMAL) == 0)
            throw new ForbiddenException();

        User requestUser = userService.findByUsername(user);

        if(gardenRequestService.existsRequest(selectedGarden, requestUser))
            throw new ResourceAlreadyExistsException(REQUEST_ALREADY_EXISTS);

        if(requestUser.getGarden() != null)
            throw new BadRequestException(USER_HAS_GARDEN);


        gardenRequestService.createRequest(requestUser,selectedGarden, USER_INVITATION_REQUEST);
    }

    @ApiOperation(value = "ACCEPT A GROUP JOIN REQUEST")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Garden member"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
            @ApiResponse(code = HTTP_CONFLICT, message = CONFLICT),
    })
    @PutMapping("/{garden}/requests/{user}")
    @ResponseStatus(HttpStatus.CREATED)
    public GardenMember acceptJoin(@PathVariable String garden,@PathVariable String user) {


        Garden selectedGarden = gardenService.findByName(garden);
        GardenMember member = gardenService.findByUser(getUser());

        if(member.getGarden() != selectedGarden || member.getRole().compareTo(GardenRole.NORMAL) == 0)
            throw new ForbiddenException();

        User requestUser = userService.findByUsername(user);
        GardenRequest gardenRequest = gardenRequestService.findRequest(requestUser,selectedGarden);

        if(gardenRequest.getType().equals(USER_INVITATION_REQUEST))
            throw new BadRequestException(REQUEST_NOT_FOUND);


        GardenMember gardenMember = gardenService.addUser(selectedGarden, requestUser);
        gardenRequestService.deleteUserRequests(requestUser);
        return gardenMember;
    }

    @ApiOperation(value = "DENY A GROUP JOIN REQUEST")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Denied correctly"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
    })
    @DeleteMapping("/{garden}/requests/{user}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void denyJoin(@PathVariable String garden,@PathVariable String user) {


        Garden selectedGarden = gardenService.findByName(garden);
        GardenMember member = gardenService.findByUser(getUser());

        if(member.getGarden() != selectedGarden || member.getRole().compareTo(GardenRole.NORMAL) == 0)
            throw new ForbiddenException();

        User requestUser = userService.findByUsername(user);
        GardenRequest gardenRequest = gardenRequestService.findRequest(requestUser,selectedGarden);

        if(gardenRequest.getType().equals(USER_INVITATION_REQUEST))
            throw new ResourceNotFoundException(REQUEST_NOT_FOUND);


        gardenRequestService.deleteRequest(gardenRequest);
    }



    @ApiOperation(value = "GET ALL GARDEN JOIN REQUESTS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Garden join requests"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = NOT_FOUND),
    })
    @GetMapping("/{garden}/chats")
    public List<ChatMessage> getChatMessages(@PathVariable String garden, @RequestParam(value = "page", required = false) Integer page) {

        Garden selectedGarden = gardenService.findByName(garden);

        GardenMember member = gardenService.findByUser(getUser());
        if(member.getGarden() != selectedGarden || member.getRole().compareTo(GardenRole.NORMAL) == 0)
            throw new ForbiddenException();


        int chatPage = 0;

        if (page != null && page > 0)
            chatPage = page;

        return gardenService.findMessagesByGarden(selectedGarden, chatPage);
    }


}
