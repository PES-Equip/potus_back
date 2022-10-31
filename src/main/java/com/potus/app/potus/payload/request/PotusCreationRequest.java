package com.potus.app.potus.payload.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class PotusCreationRequest {

    @NotNull()
    @NotEmpty()
    private String name;

    public PotusCreationRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
