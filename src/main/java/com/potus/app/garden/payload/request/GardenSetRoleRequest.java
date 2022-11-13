package com.potus.app.garden.payload.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class GardenSetRoleRequest {

    @NotNull()
    @NotEmpty()
    private String role;

    public GardenSetRoleRequest() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
