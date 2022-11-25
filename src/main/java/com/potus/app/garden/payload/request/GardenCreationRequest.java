package com.potus.app.garden.payload.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class GardenCreationRequest {

    @NotNull(message = "Garden name must be defined")
    @NotEmpty(message = "Garden name must be defined")
    @Size(min=3, max=20, message = "Garden name must be between 3 and 20 characters")
    private String name;

    public GardenCreationRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
