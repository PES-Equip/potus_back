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

    public Actions getAction() throws BadRequestException{
        try {
            return Actions.valueOf(action.toUpperCase());
        } catch (Exception e){
            throw new BadRequestException(ACTION_DOES_NOT_EXISTS);
        }
    }

    public void setAction(String action) {
        this.action = action;
    }
}
