package com.potus.app.garden.payload.response;

import com.potus.app.garden.model.GardenRole;

public class GardenMemberResponse {

    private String username;
    private GardenRole role;

    public GardenMemberResponse() {
    }

    public GardenMemberResponse(String username, GardenRole role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GardenRole getRole() {
        return role;
    }

    public void setRole(GardenRole role) {
        this.role = role;
    }
}
