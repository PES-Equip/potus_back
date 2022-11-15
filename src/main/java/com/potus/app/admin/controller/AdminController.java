package com.potus.app.admin.controller;

import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.payload.request.CreateAPITokenRequest;
import com.potus.app.admin.service.AdminService;
import com.potus.app.airquality.model.Region;
import com.potus.app.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value="/api/admin")
public class AdminController {

    @Autowired
    AdminService adminService;


    @GetMapping(value = "/tokens")
    public List<APIToken> getTokens() {
        return adminService.getAllTokens();
    }

    @PostMapping(value = "/tokens")
    @ResponseStatus(HttpStatus.CREATED)
    public APIToken createToken(@RequestBody @Valid CreateAPITokenRequest body, Errors errors) {

        if(errors.hasErrors())
            throw new BadRequestException(errors.getAllErrors().get(0).getDefaultMessage());

        return adminService.createToken(body.getName());
    }
}
