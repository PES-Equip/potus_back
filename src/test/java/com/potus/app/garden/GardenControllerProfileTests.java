package com.potus.app.garden;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.exception.*;
import com.potus.app.garden.controller.GardenController;
import com.potus.app.garden.model.*;
import com.potus.app.garden.payload.request.GardenCreationRequest;
import com.potus.app.garden.payload.request.GardenDescriptionRequest;
import com.potus.app.garden.payload.request.GardenSetRoleRequest;
import com.potus.app.garden.payload.response.GardenMemberResponse;
import com.potus.app.garden.service.GardenRequestService;
import com.potus.app.garden.service.GardenService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.potus.app.garden.utils.GardenExceptionMessages.GARDEN_DOES_NOT_EXISTS;
import static com.potus.app.garden.utils.GardenExceptionMessages.USER_HAS_NOT_GARDEN;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {GardenController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class GardenControllerProfileTests {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;


    @MockBean
    private Authentication auth;

    private SecurityContext securityContext;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private GardenRequestService gardenRequestService;

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
    public void getUserGardenTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        User user = TestUtils.getMockUserWithGardenOwner();



        Mockito.when(gardenService.findByUser(any())).thenReturn(user.getGarden());

        final String expectedResponseContent = objectMapper.writeValueAsString(user.getGarden());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/profile")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getUserGardenTestUserHasNotGardenException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        User user = TestUtils.getMockUserWithGardenOwner();



        Mockito.when(gardenService.findByUser(any())).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/profile")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    // /profile/members

    @Test
    public void getMembersGardenTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        User user = TestUtils.getMockUserWithGardenOwner();

        List<GardenMember> members = Collections.singletonList(user.getGarden());

        Mockito.when(gardenService.findByUser(any())).thenReturn(user.getGarden());
        Mockito.when(gardenService.getMembers(any())).thenReturn(members);

        List<GardenMemberResponse> response = Collections.singletonList(new GardenMemberResponse(user.getUsername(),user.getGarden().getRole()));

        final String expectedResponseContent = objectMapper.writeValueAsString(response);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/profile/members")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getMembersGardenTestUserHasNotGardenException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        User user = TestUtils.getMockUserWithGardenOwner();



        Mockito.when(gardenService.findByUser(any())).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/profile/members")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    // PUT /profile editGardenDescription

    @Test
    public void editGardenDescriptionTest() throws Exception {



        User user = TestUtils.getMockUserWithGardenOwner();

        Mockito.when(auth.getPrincipal()).thenReturn(user);

        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByUser(any())).thenReturn(user.getGarden());
        Mockito.when(gardenService.editDescription(any(),any())).thenReturn(garden);

        final String expectedResponseContent = objectMapper.writeValueAsString(garden);

        GardenDescriptionRequest gardenDescriptionRequest = new GardenDescriptionRequest();
        gardenDescriptionRequest.setDescription("");

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/profile")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gardenDescriptionRequest));


        MvcResult result = this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void editGardenDescriptionTestHasNotGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();


        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByUser(user)).thenThrow(new ResourceNotFoundException());

        GardenDescriptionRequest gardenDescriptionRequest = new GardenDescriptionRequest();
        gardenDescriptionRequest.setDescription("");

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/profile")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gardenDescriptionRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void editGardenDescriptionTestUserHasNotPermissionsFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();
        user.getGarden().setRole(GardenRole.NORMAL);


        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());

        GardenDescriptionRequest gardenDescriptionRequest = new GardenDescriptionRequest();
        gardenDescriptionRequest.setDescription("");

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/profile")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gardenDescriptionRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }


    //  DELETE /profile Exit Garden

    @Test
    public void exitGardenTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        User user = TestUtils.getMockUserWithGardenOwner();



        Mockito.when(gardenService.findByUser(any())).thenReturn(user.getGarden());

        final String expectedResponseContent = objectMapper.writeValueAsString(user.getGarden());

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/profile")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void exitGardenTestUserHasNotGardenException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        User user = TestUtils.getMockUserWithGardenOwner();

        Mockito.when(gardenService.findByUser(any())).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/profile")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }


    // REQUESTS

    // GET /profile/requests

    @Test
    public void getUserGardenRequestsTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        List<GardenRequest> list = new ArrayList<>();

        Mockito.when(gardenRequestService.findUserRequests(any())).thenReturn(list);

        final String expectedResponseContent = objectMapper.writeValueAsString(list.stream().map(GardenRequest::getGarden).toList());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/profile/requests")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }


    // POST /profile/requests/{garden}
    @Test
    public void createInvitationTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.existsRequest(any(),any())).thenReturn(false);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void createInvitationTestGardenNotExistsException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void createInvitationTestAlreadyExistsRequestException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.existsRequest(any(),any())).thenReturn(true);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }

    @Test
    public void createInvitationTestUserHasGardenException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUserWithGardenOwner());


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.existsRequest(any(),any())).thenReturn(false);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();
    }

    // PUT /profile/requests/{garden}

    @Test
    public void acceptInvitationTest() throws Exception {

        User requestUser = TestUtils.getMockUser();

        Mockito.when(auth.getPrincipal()).thenReturn( requestUser);


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        GardenRequest gardenRequest = new GardenRequest(garden,requestUser,new Date(), GardenRequestType.USER_INVITATION_REQUEST);
        GardenMember gardenMember = new GardenMember(garden,requestUser,GardenRole.NORMAL);

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenReturn(gardenRequest);
        Mockito.when(gardenService.addUser(any(),any())).thenReturn(gardenMember);

        final String expectedResponseContent = objectMapper.writeValueAsString(gardenMember);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void acceptInvitationTestGardenNotExistsException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void acceptInvitationTestNotExistsRequestException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void acceptInvitationTestConflictException() throws Exception {

        User requestUser = TestUtils.getMockUser();

        Mockito.when(auth.getPrincipal()).thenReturn( requestUser);


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        GardenRequest gardenRequest = new GardenRequest(garden,requestUser,new Date(), GardenRequestType.USER_INVITATION_REQUEST);
        GardenMember gardenMember = new GardenMember(garden,requestUser,GardenRole.NORMAL);

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenReturn(gardenRequest);
        Mockito.when(gardenService.addUser(any(),any())).thenThrow(new ConflictException());


        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ConflictException))
                .andReturn();
    }

    @Test
    public void acceptInvitationTestRequestNotFoundException() throws Exception {

        User requestUser = TestUtils.getMockUser();

        Mockito.when(auth.getPrincipal()).thenReturn( requestUser);


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        GardenRequest gardenRequest = new GardenRequest(garden,requestUser,new Date(), GardenRequestType.GROUP_JOIN_REQUEST);
        GardenMember gardenMember = new GardenMember(garden,requestUser,GardenRole.NORMAL);

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenReturn(gardenRequest);


        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    // Delete /profile/requests/{garden} DENY


    @Test
    public void denyInvitationTest() throws Exception {

        User requestUser = TestUtils.getMockUser();

        Mockito.when(auth.getPrincipal()).thenReturn( requestUser);


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        GardenRequest gardenRequest = new GardenRequest(garden,requestUser,new Date(), GardenRequestType.USER_INVITATION_REQUEST);
        GardenMember gardenMember = new GardenMember(garden,requestUser,GardenRole.NORMAL);

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenReturn(gardenRequest);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void denyInvitationTestGardenNotExistsException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void denyInvitationTestNotExistsRequestException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void denyInvitationTestRequestNotFoundException() throws Exception {

        User requestUser = TestUtils.getMockUser();

        Mockito.when(auth.getPrincipal()).thenReturn( requestUser);


        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        GardenRequest gardenRequest = new GardenRequest(garden,requestUser,new Date(), GardenRequestType.GROUP_JOIN_REQUEST);
        GardenMember gardenMember = new GardenMember(garden,requestUser,GardenRole.NORMAL);

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenReturn(gardenRequest);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/profile/requests/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }




}
