package com.potus.app.user.utils;

public final class UserExceptionMessages {


    public static final String USER_PROFILE_ALREADY_EXISTS = "User profile already exists";
    public static final String USERNAME_CANT_BE_NULL = "Username can't be null";

    public static final String USER_NOT_FOUND = "User not found";

    public static final String USERNAME_ALREADY_TAKEN = "Username already taken";

    public static final String USER_MUST_CONFIRM_FIRST = "User must confirm first";
    public static String userNotFound(Object o){
        return "User (" + o + ") not found";
    }



    private UserExceptionMessages(){
    }
}
