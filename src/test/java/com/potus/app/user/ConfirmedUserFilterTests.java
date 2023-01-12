package com.potus.app.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestUtils;
import com.potus.app.potus.controller.PotusController;
import com.potus.app.potus.service.PotusEventsService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.TestConfig;
import com.potus.app.security.filter.ConfirmedUserFilter;
import com.potus.app.user.service.TrophyService;
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

import static com.potus.app.user.utils.UserExceptionMessages.USER_MUST_CONFIRM_FIRST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ConfirmedUserFilter.class, PotusController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class ConfirmedUserFilterTests {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;



    private SecurityContext securityContext;

    @MockBean
    private PotusService potusService;

    @MockBean
    private PotusEventsService potusEventsService;

    @MockBean
    private UserService userService;

    @MockBean
    private TrophyService trophyService;

    private Authentication auth;
    @Autowired
    public ConfirmedUserFilter confirmedUserFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(confirmedUserFilter).build();

        auth = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    public void newUserTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockNewUser());

        final String expectedResponseContent = objectMapper.writeValueAsString(TestUtils.getMockNewUser().getPotus());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/potus")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(status().reason(USER_MUST_CONFIRM_FIRST))
                .andReturn();
    }

    @Test
    public void confirmedUserTest() throws Exception {

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


}