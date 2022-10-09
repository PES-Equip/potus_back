package com.potus.app.user.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Validated
public class OauthCheckController {

    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello Oauth OK");
    }

    @GetMapping("profile")
    public Object userDetails(Authentication authentication) {
        return authentication.getPrincipal();
    }

}
