package com.potus.app.admin;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.admin.controller.AdminController;
import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.payload.request.CreateAPITokenRequest;
import com.potus.app.admin.service.AdminService;
import com.potus.app.airquality.controller.AirQualityController;
import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.garden.payload.request.GardenCreationRequest;
import com.potus.app.user.model.User;
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

import java.util.List;
import java.util.stream.Stream;

import static com.potus.app.admin.utils.AdminExceptionMessages.TOKEN_NAME_ALREADY_EXISTS;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {AdminController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class AdminControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private Authentication auth;

    private SecurityContext securityContext;

    @MockBean
    private AdminService adminService;

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
    public void getTokensTest() throws Exception {
        List<APIToken> mockTokens  = Stream.of(TestUtils.getMockTokens()).toList();
        Mockito.when(adminService.getAllTokens()).thenReturn(mockTokens);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockTokens);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/admin/tokens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }


    @Test
    public void createTokenTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());


        CreateAPITokenRequest createAPITokenRequest = new CreateAPITokenRequest("XDD");
        APIToken mockToken = new APIToken("XD", createAPITokenRequest.getName());

        Mockito.when(adminService.createToken(any())).thenReturn(mockToken);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockToken);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/admin/token")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAPITokenRequest));

        MvcResult result = this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void createTokenTestNameAlreadyExists() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());

        CreateAPITokenRequest createAPITokenRequest = new CreateAPITokenRequest("XDD");

        Mockito.when(adminService.createToken(any())).thenThrow(new ResourceAlreadyExistsException());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/admin/token")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAPITokenRequest));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }


}
