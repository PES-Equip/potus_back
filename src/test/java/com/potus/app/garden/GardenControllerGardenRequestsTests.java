package com.potus.app.garden;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.exception.*;
import com.potus.app.garden.controller.GardenController;
import com.potus.app.garden.model.*;
import com.potus.app.garden.payload.request.GardenDescriptionRequest;
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
import java.util.Date;
import java.util.List;

import static com.potus.app.garden.utils.GardenExceptionMessages.GARDEN_DOES_NOT_EXISTS;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {GardenController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class GardenControllerGardenRequestsTests {

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


    // /{garden}/requests

    @Test
    public void getGardenRequestsTest() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();
        Mockito.when(auth.getPrincipal()).thenReturn(user);

        List<GardenRequest> list = new ArrayList<>();

        Mockito.when(gardenRequestService.findGardenRequests(any())).thenReturn(list);
        Mockito.when(gardenService.findByName(any())).thenReturn(user.getGarden().getGarden());
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());


        final String expectedResponseContent = objectMapper.writeValueAsString(list.stream().map(GardenRequest::getUser).toList());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getGardenRequestsTestNotFoundGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException(GARDEN_DOES_NOT_EXISTS));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void getGardenRequestsTestUserHasNotGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenThrow(new ResourceNotFoundException());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void getGardenRequestsTestUserIsNotFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();


        Garden mockGarden = new Garden("prove");

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(mockGarden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());


        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void getGardenRequestsTestUserHasNotPermissionsFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    // POST /{garden}/requests/{user}

    @Test
    public void createJoinGardenRequestsTest() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();
        Mockito.when(auth.getPrincipal()).thenReturn(user);


        User mockUser = TestUtils.getMockUser();

        Mockito.when(gardenService.findByName(any())).thenReturn(user.getGarden().getGarden());
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenRequestService.existsRequest(any(),any())).thenReturn(false);
        Mockito.when(userService.findByUsername(any())).thenReturn(mockUser);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void createJoinGardenRequestsTestNotFoundGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();


        User mockUser = TestUtils.getMockUser();

        Garden garden = user.getGarden().getGarden();

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException(GARDEN_DOES_NOT_EXISTS));

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void createJoinGardenRequestsTestUserHasNotGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenThrow(new ResourceNotFoundException());

        User mockUser = TestUtils.getMockUser();

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void createJoinGardenRequestsTestUserIsNotFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();


        Garden mockGarden = new Garden("prove");

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(mockGarden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());

        User mockUser = TestUtils.getMockUser();

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void createJoinGardenRequestsTestUserHasNotPermissionsFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = TestUtils.getMockUser();
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);



        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void createJoinGardenRequestsTestExistsRequestException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = TestUtils.getMockUser();
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.ADMIN);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);
        Mockito.when(gardenRequestService.existsRequest(any(),any())).thenReturn(true);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }

    @Test
    public void createJoinGardenRequestsTestRequestUserHasGarden() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = TestUtils.getMockUser();
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.ADMIN);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(userService.findByUsername(mockUser.getUsername())).thenReturn(user);
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();
    }

    // PUT /{garden}/requests/{user}


    @Test
    public void acceptJoinGardenRequestsTest() throws Exception {


        User requestUser = TestUtils.getMockUser();
        User user = TestUtils.getMockUserWithGardenOwner();
        Mockito.when(auth.getPrincipal()).thenReturn( user);



        Garden garden = user.getGarden().getGarden();

        GardenRequest gardenRequest = new GardenRequest(garden,requestUser,new Date(), GardenRequestType.GROUP_JOIN_REQUEST);
        GardenMember gardenMember = new GardenMember(garden,requestUser,GardenRole.NORMAL);


        Mockito.when(gardenService.findByName(any())).thenReturn(user.getGarden().getGarden());
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenReturn(gardenRequest);
        Mockito.when(userService.findByUsername(any())).thenReturn(requestUser);
        Mockito.when(gardenService.addUser(any(),any())).thenReturn(gardenMember);

        final String expectedResponseContent = objectMapper.writeValueAsString(gardenMember);


        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ requestUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void acceptJoinGardenRequestsTestNotFoundGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();


        User mockUser = TestUtils.getMockUser();

        Garden garden = user.getGarden().getGarden();

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException(GARDEN_DOES_NOT_EXISTS));

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }


    @Test
    public void acceptJoinGardenRequestsTestUserHasNotGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenThrow(new ResourceNotFoundException());

        User mockUser = TestUtils.getMockUser();

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void acceptJoinGardenRequestsTestUserIsNotFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();


        Garden mockGarden = new Garden("prove");

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(mockGarden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());

        User mockUser = TestUtils.getMockUser();

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void acceptJoinGardenRequestsTestUserHasNotPermissionsFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = TestUtils.getMockUser();
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);


        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void acceptJoinGardenRequestsTestNotExistsRequestException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = TestUtils.getMockUser();
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.ADMIN);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenThrow(new ResourceNotFoundException());

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void acceptJoinGardenRequestsTestRequestConflict() throws Exception {

        User requestUser = TestUtils.getMockUser();
        User user = TestUtils.getMockUserWithGardenOwner();
        Mockito.when(auth.getPrincipal()).thenReturn( user);



        Garden garden = user.getGarden().getGarden();

        GardenRequest gardenRequest = new GardenRequest(garden,requestUser,new Date(), GardenRequestType.GROUP_JOIN_REQUEST);
        GardenMember gardenMember = new GardenMember(garden,requestUser,GardenRole.NORMAL);


        Mockito.when(gardenService.findByName(any())).thenReturn(user.getGarden().getGarden());
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenReturn(gardenRequest);
        Mockito.when(userService.findByUsername(any())).thenReturn(requestUser);
        Mockito.when(gardenService.addUser(any(),any())).thenThrow(new ConflictException());


        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ requestUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ConflictException))
                .andReturn();
    }

    // DELETE /{garden}/requests/{user}

    @Test
    public void denyJoinGardenRequestsTest() throws Exception {


        User requestUser = TestUtils.getMockUser();
        User user = TestUtils.getMockUserWithGardenOwner();
        Mockito.when(auth.getPrincipal()).thenReturn( user);



        Garden garden = user.getGarden().getGarden();

        GardenRequest gardenRequest = new GardenRequest(garden,requestUser,new Date(), GardenRequestType.GROUP_JOIN_REQUEST);


        Mockito.when(gardenService.findByName(any())).thenReturn(user.getGarden().getGarden());
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenReturn(gardenRequest);
        Mockito.when(userService.findByUsername(any())).thenReturn(requestUser);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ requestUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void denyJoinGardenRequestsTestNotFoundGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();


        User mockUser = TestUtils.getMockUser();

        Garden garden = user.getGarden().getGarden();

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException(GARDEN_DOES_NOT_EXISTS));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }


    @Test
    public void denytJoinGardenRequestsTestUserHasNotGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenThrow(new ResourceNotFoundException());

        User mockUser = TestUtils.getMockUser();

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void denyJoinGardenRequestsTestUserIsNotFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();


        Garden mockGarden = new Garden("prove");

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(mockGarden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());

        User mockUser = TestUtils.getMockUser();

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void denyJoinGardenRequestsTestUserHasNotPermissionsFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = TestUtils.getMockUser();
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void denyJoinGardenRequestsTestNotExistsRequestException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = TestUtils.getMockUser();
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.ADMIN);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);
        Mockito.when(gardenRequestService.findRequest(any(),any())).thenThrow(new ResourceNotFoundException());

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+user.getGarden().getGarden().getName() + "/requests/"+ mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }
}
