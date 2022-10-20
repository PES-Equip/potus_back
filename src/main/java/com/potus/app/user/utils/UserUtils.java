package com.potus.app.user.utils;

import com.potus.app.user.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    private UserUtils(){}

    public static User getUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
