package com.potus.app.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.potus.model.Potus;
import com.potus.app.user.controller.UserController;
import com.potus.app.user.model.User;
import com.potus.app.user.payload.request.UsernameRequest;
import com.potus.app.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static com.potus.app.user.utils.UserExceptionMessages.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

        User mockUser = TestUtils.getMockNewUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        UsernameRequest usernameRequest = new UsernameRequest();
        usernameRequest.setUsername("Test");

        User user = new User(mockUser.getEmail(), usernameRequest.getUsername());
        user.setPotus(new Potus());

        Mockito.when(userService.setUsername(any(),any())).thenReturn(user);
        Mockito.when(userService.createPotus(any())).thenReturn(user);

        final String expectedResponseContent = objectMapper.writeValueAsString(user);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/user")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usernameRequest));

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void createProfileAlreadyExistsTest() throws Exception {
        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        UsernameRequest usernameRequest = new UsernameRequest();
        usernameRequest.setUsername("Test");

        User user = new User(mockUser.getEmail(), usernameRequest.getUsername());
        user.setPotus(new Potus());

        Mockito.when(userService.setUsername(any(),any())).thenReturn(user);
        Mockito.when(userService.createPotus(any())).thenReturn(user);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/user")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usernameRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }

    @Test
    public void createProfileUsernameCantBeNullTest() throws Exception {
        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        UsernameRequest usernameRequest = new UsernameRequest();

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/user")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usernameRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();
    }

    @Test
    public void createProfileUsernameAlreadyTakenTest() throws Exception {
        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        UsernameRequest usernameRequest = new UsernameRequest();
        usernameRequest.setUsername("Test");


        Mockito.when(userService.setUsername(any(),any())).thenThrow(new ResourceAlreadyExistsException(USERNAME_ALREADY_TAKEN));


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/user")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usernameRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }


}
