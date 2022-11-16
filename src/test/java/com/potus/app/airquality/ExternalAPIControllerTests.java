package com.potus.app.airquality;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.airquality.controller.AirQualityController;
import com.potus.app.airquality.controller.ExternalAPIController;
import com.potus.app.airquality.service.AirQualityService;
import com.potus.app.garden.service.GardenRequestService;
import com.potus.app.garden.service.GardenService;
import com.potus.app.potus.controller.PotusController;
import com.potus.app.user.service.UserService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import com.potus.app.airquality.model.Region;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ExternalAPIController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class ExternalAPIControllerTests {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private Authentication auth;

    private SecurityContext securityContext;

    @MockBean
    private AirQualityService airQualityService;

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
    public void getRegionsTest() throws Exception {
        List<Region> mockRegions = Stream.of(TestUtils.getMockRegion()).toList();
        Mockito.when(airQualityService.findAll()).thenReturn(mockRegions);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockRegions);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/external/airquality/regions")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getRegionTest() throws Exception {
        Region mockRegion = TestUtils.getMockRegion();
        Mockito.when(airQualityService.getRegion(any(),any())).thenReturn(mockRegion);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockRegion);
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("latitude", "0.0");
        params.add("length", "0.0");

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/external/airquality/region")
                .queryParams(params)
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }


}
