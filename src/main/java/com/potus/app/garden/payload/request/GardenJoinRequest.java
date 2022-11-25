package com.potus.app.garden.payload.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class GardenJoinRequest {

    @NotNull(message = "Username must be defined")
    @NotEmpty(message = "Username name must be defined")
    private String username;

    public GardenJoinRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
