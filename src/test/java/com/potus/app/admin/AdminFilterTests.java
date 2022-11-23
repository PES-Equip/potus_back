package com.potus.app.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.admin.controller.AdminController;
import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.service.AdminService;
import com.potus.app.airquality.controller.ExternalAPIController;
import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.potus.service.PotusEventsService;
import com.potus.app.potus.service.PotusRegistryService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.security.filter.AdminTokenFilter;
import com.potus.app.security.filter.ExternalTokenFilter;
import com.potus.app.security.filter.PotusIsDeadFilter;
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
import org.springframework.http.HttpHeaders;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.potus.app.admin.utils.AdminUtils.APITOKEN_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {AdminTokenFilter.class, AdminController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class AdminFilterTests {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;

    private SecurityContext securityContext;


    @MockBean
    private UserService userService;

    @MockBean
    private AdminService adminService;

    private Authentication auth;
    @Autowired
    public AdminTokenFilter adminTokenFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(adminTokenFilter).build();

        auth = Mockito.mock(Authentication.class);
        securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    public void AdminValidTest() throws Exception {

        User mockedUser = TestUtils.getMockUser();

        mockedUser.setAdmin(true);

        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        List<APIToken> tokens = Collections.singletonList(new APIToken("test","test"));
        Mockito.when(adminService.getAllTokens()).thenReturn(tokens);


        final String expectedResponseContent = objectMapper.writeValueAsString(tokens);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/admin/tokens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void tokenInValidTest() throws Exception {

        User mockedUser = TestUtils.getMockUser();


        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/admin/tokens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

}
