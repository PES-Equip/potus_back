package com.potus.app.admin.controller;

import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.model.BanRequest;
import com.potus.app.admin.model.BannedAccount;
import com.potus.app.admin.payload.request.AddAdminRequest;
import com.potus.app.admin.payload.request.BanAccountRequest;
import com.potus.app.admin.payload.request.CreateAPITokenRequest;
import com.potus.app.admin.service.AdminService;
import com.potus.app.airquality.model.Region;
import com.potus.app.exception.BadRequestException;
import com.potus.app.garden.model.ChatMessage;
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

@CrossOrigin(origins = "*", maxAge = 3600)
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
    @DeleteMapping(value = "/tokens/{tokenId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteToken(@PathVariable Long tokenId) {

        APIToken selectedToken = adminService.findByToken(tokenId);
        adminService.deleteToken(selectedToken);
    }

    @ApiOperation(value = "REFRESH A TOKEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Token refreshed"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = "Token not found"),
    })
    @PostMapping(value = "/tokens/{tokenId}")
    public APIToken refreshToken(@PathVariable Long tokenId) {

        APIToken selectedToken = adminService.findByToken(tokenId);

        return adminService.refreshToken(selectedToken);
    }

    @ApiOperation(value = "RENAME A TOKEN")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "Token deleted"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = "Token not found"),
    })
    @PutMapping(value = "/tokens/{tokenId}")
    public APIToken renameToken(@PathVariable Long tokenId,@RequestBody @Valid CreateAPITokenRequest body, Errors errors) {

        if(errors.hasErrors())
            throw new BadRequestException(errors.getAllErrors().get(0).getDefaultMessage());

        APIToken selectedToken = adminService.findByToken(tokenId);

        return adminService.renameToken(selectedToken, body.getName());
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

    @ApiOperation(value = "EMAILS BANNED")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Banned emails"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping(value = "/banned")
    public List<BannedAccount> getBannedAccounts() {
        return adminService.findAllBannedAccounts();
    }

    @ApiOperation(value = "BAN AN USER")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User banned"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = "User not found"),
    })
    @PostMapping(value = "/ban/{userId}")
    public BannedAccount banAccount(@PathVariable Long userId, @RequestBody @Valid BanAccountRequest body, Errors errors) {

        if(errors.hasErrors())
            throw new BadRequestException(errors.getAllErrors().get(0).getDefaultMessage());

        User selectedUser = userService.findById(userId);

        return adminService.banAccount(selectedUser, body.getReason());
    }

    @ApiOperation(value = "NOT BAN AN USER")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_NO_CONTENT, message = "User not banned"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = "User not found"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/ban/{userId}")
    public void notBanAccount(@PathVariable Long userId) {
        User selectedUser = userService.findById(userId);

        adminService.deleteBanRequest(selectedUser);
    }

    @ApiOperation(value = "GET BAN REQUESTS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User banned"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
            @ApiResponse(code = HTTP_NOT_FOUND, message = "User not found"),
    })
    @GetMapping(value = "/ban")
    public List<BanRequest> getBanRequests() {
        return adminService.getBanRequests().stream().filter(request -> request.getReports().size() >= 3).toList();
    }


    @ApiOperation(value = "GET REPORT CHAT HISTORY")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "CHAT HISTORY"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping(value = "/chat/{chatId}/history")
    public List<ChatMessage> getChatHistory(@PathVariable String chatId) {

        ChatMessage chatMessage = adminService.findChatMessageById(chatId);
        return adminService.getPreviousMessages(chatMessage, 0);
    }

    // post ban
    // delete ban


}
