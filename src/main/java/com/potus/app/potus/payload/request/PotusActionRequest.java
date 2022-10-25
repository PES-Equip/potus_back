package com.potus.app.potus.payload.request;

import com.potus.app.exception.BadRequestException;
import com.potus.app.potus.model.Actions;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.potus.app.potus.utils.PotusExceptionMessages.ACTION_DOES_NOT_EXISTS;


public class PotusActionRequest {

    @NotNull()
    @NotEmpty()
    private String action;

    public PotusActionRequest() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
