package com.potus.app;

import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.garden.model.Garden;
import com.potus.app.garden.model.GardenMember;
import com.potus.app.garden.model.GardenRole;
import com.potus.app.potus.model.Potus;
import com.potus.app.user.model.User;

import java.util.Collections;

public class TestUtils {

    private TestUtils(){}

    private static Potus potus = new Potus("potus");

    public static User getMockUser(){
        User user = new User("test@test.com", "test");
        user.setPotus(potus);

        return user;
    }

    public static User getMockUserWithDeadPotus(){
        User user = new User("test@test.com", "test");
        Potus p = new Potus();
        p.setAlive(false);
        user.setPotus(p);

        return user;
    }

    public static User getMockNewUser(){
        User user = new User("test@test.com", null);
        return user;
    }

    public static User getMockUserWithGardenOwner(){
        User user = new User("test@test.com", "test");
        user.setPotus(potus);
        user.setGarden(getGarden(user, "testgarden"));

        return user;
    }

    public static GardenMember getGarden(User user, String name){
        Garden garden = new Garden(name);
        GardenMember gm = new GardenMember(garden, user, GardenRole.OWNER);
        garden.setMembers(Collections.singleton(gm));
        return gm;
    }


}
