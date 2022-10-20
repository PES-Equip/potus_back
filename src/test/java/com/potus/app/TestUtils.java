package com.potus.app;

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
}
