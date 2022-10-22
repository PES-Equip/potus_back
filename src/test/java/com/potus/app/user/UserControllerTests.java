package com.potus.app.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.potus.controller.PotusActionsController;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.controller.UserController;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.potus.app.TestUtils.getMockUserWithDeadPotus;
import static com.potus.app.user.utils.UserExceptionMessages.USER_MUST_CONFIRM_FIRST;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {UserController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class UserControllerTests {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;


    @MockBean
    private Authentication auth;

    private SecurityContext securityContext;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        auth = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    public void getProfileTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());

        final String expectedResponseContent = objectMapper.writeValueAsString(TestUtils.getMockUser());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/profile")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getUsersTest() throws Exception {

        List<User> users = new ArrayList<>();
        users.add(getMockUserWithDeadPotus());

        Mockito.when(userService.getAll()).thenReturn(users);

        final String expectedResponseContent = objectMapper.writeValueAsString(users);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/user")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();

    }

    @Test
    public void createProfileOKTest() throws Exception {

    }

    @Test
    public void createProfileAlreadyExistsTest() throws Exception {

    }

    @Test
    public void createProfileUsernameCantBeNullTest() throws Exception {

    }

    @Test
    public void createProfileUsernameAlreadyTakenTest() throws Exception {

    }


}
