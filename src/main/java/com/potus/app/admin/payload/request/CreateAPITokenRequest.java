package com.potus.app.admin.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateAPITokenRequest {

    @NotBlank(message = "API Token name must be defined")
    @Size(min = 3, max = 50, message = "API Token name must be between 3 and 50")
    private String name;

    public CreateAPITokenRequest() {
    }

    public CreateAPITokenRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
