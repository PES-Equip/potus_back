package com.potus.app.potus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.potus.controller.PotusActionsController;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.service.PotusService;
import com.potus.app.security.filter.PotusStatesFilter;
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

import java.util.Date;

import static com.potus.app.potus.utils.PotusExceptionMessages.POTUS_IS_DEAD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PotusStatesFilter.class, PotusActionsController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class PotusStatesFilterTests {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;

    private SecurityContext securityContext;

    @MockBean
    private PotusService potusService;

    @MockBean
    private UserService userService;

    private Authentication auth;
    @Autowired
    public PotusStatesFilter potusStatesFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(potusStatesFilter).build();

        auth = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    public void potusDeadTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUserWithDeadPotus());


        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/potus")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(status().reason(POTUS_IS_DEAD))
                .andReturn();
    }

    @Test
    public void potusUpdateTest() throws Exception {

        User mockUser = TestUtils.getMockUser();
        Potus potus = mockUser.getPotus();
        Date mockDate = new Date(potus.getLastModified().getTime() - (86400000));
        potus.setLastModified(mockDate);

        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        doNothing().when(potusService).updatePotusStats(isA(Potus.class));
        potus.setWaterLevel(95);

        final String expectedResponseContent = objectMapper.writeValueAsString(potus);

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


