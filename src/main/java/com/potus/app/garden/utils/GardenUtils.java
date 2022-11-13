package com.potus.app.garden.utils;

import com.potus.app.exception.BadRequestException;
import com.potus.app.garden.model.GardenRole;
import com.potus.app.potus.model.Actions;

import static com.potus.app.garden.utils.GardenExceptionMessages.GARDEN_ROLE_NOT_EXISTS;
import static com.potus.app.potus.utils.PotusExceptionMessages.ACTION_DOES_NOT_EXISTS;

public class GardenUtils {

    public static final int REQUEST_TIME_LIMIT = 7;

    public static final int GARDEN_MAX_SIZE = 30;


    private GardenUtils(){}

    public static GardenRole getGardenRoleFormatted(String role) throws BadRequestException {
        try {
            return GardenRole.valueOf(role.toUpperCase());
        } catch (Exception e){
            throw new BadRequestException(GARDEN_ROLE_NOT_EXISTS);
        }
    }
}
