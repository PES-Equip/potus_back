package com.potus.app.airquality;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.admin.service.AdminService;
import com.potus.app.airquality.controller.ExternalAPIController;
import com.potus.app.airquality.model.Region;
import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.exception.BadRequestException;
import com.potus.app.potus.controller.PotusController;
import com.potus.app.potus.service.PotusEventsService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.security.filter.AdminTokenFilter;
import com.potus.app.security.filter.ConfirmedUserFilter;
import com.potus.app.security.filter.ExternalTokenFilter;
import com.potus.app.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
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

import java.util.List;
import java.util.stream.Stream;

import static com.potus.app.admin.utils.AdminUtils.APITOKEN_TYPE;
import static com.potus.app.user.utils.UserExceptionMessages.USER_MUST_CONFIRM_FIRST;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {ExternalTokenFilter.class, ExternalAPIController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class TokenFilterTests {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;



    private SecurityContext securityContext;

    @MockBean
    private AirQualityService airQualityService;

    @MockBean
    private AdminService adminService;
    @Autowired
    public ExternalTokenFilter externalTokenFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(externalTokenFilter).build();

        securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void tokenValidTest() throws Exception {

        Mockito.when( adminService.existsToken(any())).thenReturn(true);

        List<Region> mockRegions = Stream.of(TestUtils.getMockRegion()).toList();
        Mockito.when(airQualityService.findAll()).thenReturn(mockRegions);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockRegions);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/external/airquality/regions")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, APITOKEN_TYPE + " yepa");

        MvcResult result = this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void tokenInValidTest() throws Exception {

        Mockito.when( adminService.existsToken(any())).thenReturn(false);

        List<Region> mockRegions = Stream.of(TestUtils.getMockRegion()).toList();
        Mockito.when(airQualityService.findAll()).thenReturn(mockRegions);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockRegions);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/external/airquality/regions")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, APITOKEN_TYPE + " yepa");

        this.mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void tokenInValidTest2() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/external/airquality/regions")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,  "BEARER yepa");

        this.mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

}
