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
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.garden.payload.request.GardenCreationRequest;
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
                .post("/api/admin/tokens")
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
                .post("/api/admin/tokens")
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

    @Test
    public void renameTokenTest() throws Exception {

        Long token = 2L;

        APIToken apiToken = new APIToken("test", "test");


        Mockito.when(adminService.findByToken(any())).thenReturn(apiToken);
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());


        CreateAPITokenRequest createAPITokenRequest = new CreateAPITokenRequest("XDD");
        APIToken mockToken = new APIToken(createAPITokenRequest.getName(), createAPITokenRequest.getName());

        Mockito.when(adminService.renameToken(any(), any())).thenReturn(mockToken);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockToken);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/admin/tokens/"+token)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAPITokenRequest));

        MvcResult result = this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void renameTokenTestNameAlreadyExists() throws Exception {

        Long token = 2L;

        APIToken apiToken = new APIToken("test", "test");


        Mockito.when(adminService.findByToken(any())).thenReturn(apiToken);

        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());

        CreateAPITokenRequest createAPITokenRequest = new CreateAPITokenRequest("XDD");

        Mockito.when(adminService.renameToken(any(),any())).thenThrow(new ResourceAlreadyExistsException());

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/admin/tokens/" + token)
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

    @Test
    public void renameTokenTestNotFound() throws Exception {

        Long token = 2L;

        APIToken apiToken = new APIToken("test", "test");


        Mockito.when(adminService.findByToken(any())).thenThrow(new ResourceNotFoundException());



        CreateAPITokenRequest createAPITokenRequest = new CreateAPITokenRequest("XDD");

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/admin/tokens/" + token)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAPITokenRequest));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    // REFRESH

    @Test
    public void refreshTokenTest() throws Exception {

        Long token = 2L;

        APIToken apiToken = new APIToken("test", "test");


        Mockito.when(adminService.findByToken(any())).thenReturn(apiToken);
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());

        APIToken mockToken = new APIToken(apiToken.getName() ,"test2");

        Mockito.when(adminService.refreshToken(any())).thenReturn(mockToken);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockToken);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/admin/tokens/"+token)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }


    @Test
    public void refreshTokenTestNotFound() throws Exception {

        Long token = 2L;

        APIToken apiToken = new APIToken("test", "test");


        Mockito.when(adminService.findByToken(any())).thenThrow(new ResourceNotFoundException());



        CreateAPITokenRequest createAPITokenRequest = new CreateAPITokenRequest("XDD");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/admin/tokens/" + token)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAPITokenRequest));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void deleteTokenTests() throws Exception {

        Long token = 2L;

        APIToken apiToken = new APIToken("test", "test");


        Mockito.when(adminService.findByName(any())).thenReturn(apiToken);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/admin/tokens/"+token)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                    .andReturn();
    }

    @Test
    public void deleteTokenNotExistsTokenExceptionTests() throws Exception {

        Long token = 2L;


        Mockito.when(adminService.findByToken(any())).thenThrow(new ResourceNotFoundException());

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/admin/tokens/"+token)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }


    @Test
    public void deleteAdminTests() throws Exception {

        String user = "test";
        User mockedUser = TestUtils.getMockUser();

        Mockito.when(userService.findByUsername(any())).thenReturn(mockedUser);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/admin/"+user)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void deleteAdminAlreadyIsNotAdminExceptionTests() throws Exception {

        String user = "test";
        User mockedUser = TestUtils.getMockUser();

        Mockito.when(userService.findByUsername(any())).thenReturn(mockedUser);

        Mockito.when(adminService.deleteAdmin(any())).thenThrow(new ResourceAlreadyExistsException());

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/admin/"+user)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }


    @Test
    public void addAdminTests() throws Exception {

        String user = "test";
        User mockedUser = TestUtils.getMockUser();

        Mockito.when(userService.findByUsername(any())).thenReturn(mockedUser);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/admin/"+user)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void addAdminAlreadyIsAdminExceptionTests() throws Exception {

        String user = "test";
        User mockedUser = TestUtils.getMockUser();

        Mockito.when(userService.findByUsername(any())).thenReturn(mockedUser);

        Mockito.when(adminService.addAdmin(any())).thenThrow(new ResourceAlreadyExistsException());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/admin/"+user)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }
}
