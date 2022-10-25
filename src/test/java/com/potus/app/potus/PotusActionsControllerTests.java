package com.potus.app.potus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestUtils;
import com.potus.app.exception.BadRequestException;
import com.potus.app.potus.controller.PotusActionsController;
import com.potus.app.potus.model.Actions;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.payload.request.PotusActionRequest;
import com.potus.app.potus.payload.request.PotusEventRequest;
import com.potus.app.potus.service.PotusService;
import com.potus.app.TestConfig;
import com.potus.app.user.model.User;
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

import java.util.Date;

import static com.potus.app.potus.utils.PotusUtils.PRUNNING_CURRENCY_BONUS;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {PotusActionsController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class PotusActionsControllerTests {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;


    @MockBean
    private Authentication auth;

    private SecurityContext securityContext;

    @MockBean
    private PotusService potusService;

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
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());
    }

    @Test
    public void getPotusTest() throws Exception {
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());
        final String expectedResponseContent = objectMapper.writeValueAsString(TestUtils.getMockUser().getPotus());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/potus")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void doActionOKTest() throws Exception {
        User mockedUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        PotusActionRequest potusActionRequest = new PotusActionRequest();
        potusActionRequest.setAction("prune");

        User user = new User(mockedUser.getEmail(),mockedUser.getEmail());
        user.setPotus(mockedUser.getPotus());

        user.setCurrency(mockedUser.getCurrency() + PRUNNING_CURRENCY_BONUS);

        Mockito.when(potusService.doFilterAction(any(),any())).thenReturn(PRUNNING_CURRENCY_BONUS);
        Mockito.when(userService.addCurrency(any(),any())).thenReturn(user);

        final String expectedResponseContent = objectMapper.writeValueAsString(user);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/action")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusActionRequest));

        MvcResult result = this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();

    }

    @Test
    public void doActionIsNullErrorTest() throws Exception {
        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        PotusActionRequest potusActionRequest = new PotusActionRequest();


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/action")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusActionRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();


    }

    @Test
    public void doActionNotExistsErrorTest() throws Exception {
        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        PotusActionRequest potusActionRequest = new PotusActionRequest();
        potusActionRequest.setAction("YEPAAA");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/action")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusActionRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();


    }

    @Test
    public void doActionAlreadyDidItErrorTest() throws Exception {
        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        PotusActionRequest potusActionRequest = new PotusActionRequest();
        potusActionRequest.setAction("prune");


        Mockito.when(potusService.doFilterAction(any(),any())).thenThrow(new BadRequestException());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/action")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusActionRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();

    }


}