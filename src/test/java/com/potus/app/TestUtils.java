package com.potus.app;

import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.potus.model.Potus;
import com.potus.app.user.model.User;

public class TestUtils {

    private TestUtils(){}

    private static Potus potus = new Potus();

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

}
