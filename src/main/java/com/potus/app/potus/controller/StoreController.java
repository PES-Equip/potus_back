package com.potus.app.potus.controller;

import com.potus.app.exception.BadRequestException;
import com.potus.app.potus.model.Actions;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.PotusModifier;
import com.potus.app.potus.payload.request.PotusActionRequest;
import com.potus.app.potus.payload.request.PotusEventRequest;
import com.potus.app.potus.payload.response.PotusModifierStoreResponse;
import com.potus.app.potus.service.PotusEventsService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.potus.app.exception.GeneralExceptionMessages.BAD_REQUEST;
import static com.potus.app.exception.GeneralExceptionMessages.UNAUTHENTICATED;
import static com.potus.app.potus.utils.PotusExceptionMessages.ACTION_IS_NULL;
import static com.potus.app.potus.utils.PotusExceptionMessages.COORDINATES_ARE_NULL;
import static com.potus.app.user.utils.UserUtils.getActionFormatted;
import static com.potus.app.user.utils.UserUtils.getUser;
import static java.net.HttpURLConnection.*;

@RestController
@RequestMapping(value="/api/potus/store")
@Api(tags= "Potus",value = "Potus endpoints")
public class StoreController {

    @Autowired
    private PotusService potusService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "GET POTUS STORE")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User potus"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @GetMapping()
    public List<PotusModifierStoreResponse> getPotusStore() {
        Potus potus = getUser().getPotus();
        Set<PotusModifier> buffs = potus.getBuffs();

        List<PotusModifierStoreResponse> response = new ArrayList<>();
        buffs.forEach(buff ->{
            response.add(new PotusModifierStoreResponse(buff));
            });
        return response;
    }

    @ApiOperation(value = "GET POTUS")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "User potus"),
            @ApiResponse(code = HTTP_UNAUTHORIZED, message = UNAUTHENTICATED),
    })
    @PostMapping("/buy/{modifier}")
    public void purchaseModifier(@PathVariable String modifier) {
        User user = getUser();
        Potus potus = user.getPotus();
        PotusModifier selectedModifier = potus.getBuff(modifier);
        userService.upgradeModifier(user,selectedModifier);
    }


}
