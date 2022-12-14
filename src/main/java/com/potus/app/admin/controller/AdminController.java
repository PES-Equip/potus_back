package com.potus.app.admin.controller;

import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.payload.request.AddAdminRequest;
import com.potus.app.admin.payload.request.CreateAPITokenRequest;
import com.potus.app.admin.service.AdminService;
import com.potus.app.airquality.model.Region;
import com.potus.app.exception.BadRequestException;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostRemove;
import javax.validation.Valid;
import java.util.List;

import static com.potus.app.exception.GeneralExceptionMessages.UNAUTHENTICATED;
import static java.net.HttpURLConnection.*;

@RestController
@RequestMapping(value="/api/admin")
@Api(tags= "Admin",value = "Admin endpoints")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @ApiOperation(value = "GET ALL THE TOKENS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Tokens list"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping(value = "/tokens")
    public List<APIToken> getTokens() {
        return adminService.getAllTokens();
    }

    @ApiOperation(value = "CREATE A TOKEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_CREATED, message = "Token created"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_CONFLICT, message = "A token with the given name already exists"),
    })
    @PostMapping(value = "/tokens")
    @ResponseStatus(HttpStatus.CREATED)
    public APIToken createToken(@RequestBody @Valid CreateAPITokenRequest body, Errors errors) {

        if(errors.hasErrors())
            throw new BadRequestException(errors.getAllErrors().get(0).getDefaultMessage());

        return adminService.createToken(body.getName());
    }

    @ApiOperation(value = "DELETE A TOKEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Token deleted"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = "Token not found"),
    })
    @DeleteMapping(value = "/tokens/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteToken(@PathVariable String token) {

        APIToken selectedToken = adminService.findByName(token);
        adminService.deleteToken(selectedToken);
    }

    @ApiOperation(value = "ADD AN ADMIN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_CREATED, message = "Admin added"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = "User not found"),
            @ApiResponse(code = HTTP_CONFLICT, message = "User is already admin"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{user}")
    public void addAdmin(@PathVariable String user) {

        User selectedUser = userService.findByUsername(user);

        adminService.addAdmin(selectedUser);
    }

    @ApiOperation(value = "DELETE AN ADMIN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Admin deleted"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = "User not found"),
            @ApiResponse(code = HTTP_CONFLICT, message = "User is not admin"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{user}")
    public void deleteAdmin(@PathVariable String user) {

        User selectedUser = userService.findByUsername(user);
        adminService.deleteAdmin(selectedUser);
    }


}
