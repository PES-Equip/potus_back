package com.potus.app.user.payload.request;



import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class UsernameRequest {

    @NotNull()
    @NotEmpty()
    private String username;

    public UsernameRequest() {
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
