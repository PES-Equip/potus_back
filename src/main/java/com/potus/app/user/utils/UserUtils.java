package com.potus.app.user.utils;

import com.potus.app.exception.BadRequestException;
import com.potus.app.potus.model.Actions;
import com.potus.app.user.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.potus.app.potus.utils.PotusExceptionMessages.ACTION_DOES_NOT_EXISTS;

public class UserUtils {

    private UserUtils(){}

    public static User getUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Actions getActionFormatted(String action) throws BadRequestException {
        try {
            return Actions.valueOf(action.toUpperCase());
        } catch (Exception e){
            throw new BadRequestException(ACTION_DOES_NOT_EXISTS);
        }
    }
}
