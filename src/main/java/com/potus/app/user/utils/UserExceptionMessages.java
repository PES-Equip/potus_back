package com.potus.app.user.utils;

public final class UserExceptionMessages {


    public static final String USER_PROFILE_ALREADY_EXISTS = "User profile already exists";
    public static final String USERNAME_CANT_BE_NULL = "Username can't be null";

    public static final String USER_NOT_FOUND = "User not found";

    public static final String USERNAME_IS_SAME = "Username is the same";

    public static final String USERNAME_ALREADY_TAKEN = "Username already taken";

    public static final String USER_MUST_CONFIRM_FIRST = "User must confirm first";

    public static final String USER_HAS_NOT_ENOUGH_CURRENCY = "User has not enough currency";
    public static String userNotFound(Object o){
        return "User (" + o + ") not found";
    }

    public static final String USER_MUST_BE_ADMIN = "User must be admin to access admin endpoints";
    public static final String USER_ALREADY_HAS_ADDED_MEETING = "The meeting is already added";
    public static final String USER_DOES_NOT_HAVE_MEETING = "The user does not have that meeting added";






    private UserExceptionMessages(){
    }
}
