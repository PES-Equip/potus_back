package com.potus.app.meeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.admin.controller.AdminController;
import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.service.AdminService;
import com.potus.app.meetings.controller.MeetingsController;
import com.potus.app.meetings.model.Meeting;
import com.potus.app.meetings.service.MeetingsService;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {MeetingsController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class MeetingControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private Authentication auth;

    private SecurityContext securityContext;

    @MockBean
    private MeetingsService meetingsService;

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
    public void getMeetingsTestOk() throws Exception {

        List<Meeting> mockMeetings  = new ArrayList<>();
        Mockito.when(meetingsService.findAll()).thenReturn(mockMeetings);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockMeetings);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/meetings")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getMeetingsByLatitudeAndLengthTestOk() throws Exception {

        List<Meeting> mockMeetings  = new ArrayList<>();
        Mockito.when(meetingsService.getMeetingsLatLen(any(),any())).thenReturn(mockMeetings);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockMeetings);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/meetings?length=5&latitude=4")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getMeetingsByLatitudeAndLengthTestWrongFormat() throws Exception {

        List<Meeting> mockMeetings  = new ArrayList<>();
        Mockito.when(meetingsService.getMeetingsLatLen(any(),any())).thenReturn(mockMeetings);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockMeetings);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/meetings?length=a&latitude=4")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    @Test
    public void getMeetingsByDateOk() throws Exception {

        List<Meeting> mockMeetings  = new ArrayList<>();
        Mockito.when(meetingsService.getMeetingDateInterval(any(),any())).thenReturn(mockMeetings);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockMeetings);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/meetings?start_date=05-04-2022&end_date=05-04-2023")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getMeetingsByDateWrongFormat() throws Exception {

        List<Meeting> mockMeetings  = new ArrayList<>();
        Mockito.when(meetingsService.getMeetingDateInterval(any(),any())).thenReturn(mockMeetings);

        final String expectedResponseContent = objectMapper.writeValueAsString(mockMeetings);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/meetings?start_date=05-04:2022&end_date=05-04-2023")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
