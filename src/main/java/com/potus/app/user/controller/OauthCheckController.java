package com.potus.app.user.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
@Validated
public class OauthCheckController {

    @RequestMapping("/hello")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello Oauth OK");
    }

    @GetMapping("/user/me")
    public Principal userDetails(Principal principal) {

        return principal;
    }

}
