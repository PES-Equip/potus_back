package com.potus.app.garden;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestConfig;
import com.potus.app.TestUtils;
import com.potus.app.exception.*;
import com.potus.app.garden.controller.GardenController;
import com.potus.app.garden.model.Garden;
import com.potus.app.garden.model.GardenMember;
import com.potus.app.garden.model.GardenRole;
import com.potus.app.garden.payload.request.GardenCreationRequest;
import com.potus.app.garden.service.GardenRequestService;
import com.potus.app.garden.service.GardenService;
import com.potus.app.potus.controller.PotusController;
import com.potus.app.potus.service.PotusEventsService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.user.model.User;
import com.potus.app.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

import java.util.Collections;
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
public class GardenControllerTests {

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
    public void getGardensTest() throws Exception {


        User user = TestUtils.getMockUserWithGardenOwner();
        Mockito.when(auth.getPrincipal()).thenReturn(user);


        List<Garden> gardens =  Collections.singletonList(user.getGarden().getGarden());
        Mockito.when(gardenService.getAll()).thenReturn(gardens);

        final String expectedResponseContent = objectMapper.writeValueAsString(gardens);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void createGardensTest() throws Exception {


        User user = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());


        GardenCreationRequest gardenCreationRequest = new GardenCreationRequest();

        gardenCreationRequest.setName("test");

        user.setGarden(TestUtils.getGarden(user, gardenCreationRequest.getName()));

        Mockito.when(gardenService.existsByName(any())).thenReturn(false);
        Mockito.when(gardenService.createGarden(any(),any())).thenReturn(user.getGarden());
        Mockito.when(userService.saveUser(any())).thenReturn(user);

        final String expectedResponseContent = objectMapper.writeValueAsString(user.getGarden().getGarden());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gardenCreationRequest));

        MvcResult result = this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void createGardensTestEmptyRequestException() throws Exception {


        User user = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());


        GardenCreationRequest gardenCreationRequest = new GardenCreationRequest();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gardenCreationRequest));

                this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();
    }

    @Test
    public void createGardensTestRequestSizeException() throws Exception {


        User user = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());


        GardenCreationRequest gardenCreationRequest = new GardenCreationRequest();

        gardenCreationRequest.setName("t");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gardenCreationRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();
    }

    @Test
    public void createGardensTestInvalidNameException() throws Exception {



        User user = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());


        GardenCreationRequest gardenCreationRequest = new GardenCreationRequest();

        gardenCreationRequest.setName("profile");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gardenCreationRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }

    @Test
    public void createGardensTestNameAlreadyTakenException() throws Exception {



        User user = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());


        GardenCreationRequest gardenCreationRequest = new GardenCreationRequest();

        gardenCreationRequest.setName("test");

        Mockito.when(gardenService.existsByName(any())).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gardenCreationRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }

    @Test
    public void createGardensTestUserAlreadyHasGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();
        Mockito.when(auth.getPrincipal()).thenReturn(user);


        GardenCreationRequest gardenCreationRequest = new GardenCreationRequest();

        gardenCreationRequest.setName("test");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/gardens")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gardenCreationRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException))
                .andReturn();
    }

    // GET /{garden}

    @Test
    public void getGardenTest() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);

        final String expectedResponseContent = objectMapper.writeValueAsString(garden);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getGardenTestNotFoundException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException(GARDEN_DOES_NOT_EXISTS));

        final String expectedResponseContent = objectMapper.writeValueAsString(garden);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/gardens/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    // DELETE /{garden}

    @Test
    public void deleteGardenTest() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Mockito.when(auth.getPrincipal()).thenReturn( user );


        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(any())).thenReturn(user.getGarden());


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void deleteGardenTestMemberNotOwnerException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(any())).thenReturn(mockedMember);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void deleteGardenTestNotMemberException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        Garden mockGarden = new Garden("garden");
        GardenMember mockedMember = new GardenMember(mockGarden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(any())).thenReturn(mockedMember);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void deleteGardenTesUserWithoutGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Mockito.when(auth.getPrincipal()).thenReturn( user );


        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(any())).thenThrow(new ResourceNotFoundException(USER_HAS_NOT_GARDEN));


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void deleteGardenTestNotFoundException() throws Exception {

        Mockito.when(auth.getPrincipal()).thenReturn( TestUtils.getMockUser());

        User user = TestUtils.getMockUserWithGardenOwner();
        Garden garden = user.getGarden().getGarden();

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException(GARDEN_DOES_NOT_EXISTS));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    // DELETE /{garden}/{user}

    @Test
    public void removeUserTest() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);
        Mockito.when(userService.findByUsername(mockUser.getUsername())).thenReturn(mockUser);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName()+"/"+mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
    }


    @Test
    public void removeUserTestNotFoundGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenThrow(new ResourceNotFoundException(GARDEN_DOES_NOT_EXISTS));


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName()+"/"+mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void removeUserTestNotFoundUserException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(userService.findByUsername(mockUser.getUsername())).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName()+"/"+mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void removeUserTestUserHasNotGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenThrow(new ResourceNotFoundException());


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName()+"/"+mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void removeUserTestUserIsNotFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Garden mockGarden = new Garden("prove");

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(mockGarden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+mockGarden.getName()+"/"+mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }

    @Test
    public void removeUserTestUserHasNotPermissionsFromGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( mockUser );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);
        Mockito.when(userService.findByUsername(mockUser.getUsername())).thenReturn(mockUser);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName()+"/"+user.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }


    @Test
    public void removeUserTestTargetUserHasNotGardenException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenThrow(new ResourceNotFoundException());
        Mockito.when(userService.findByUsername(mockUser.getUsername())).thenReturn(mockUser);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName()+"/"+mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void removeUserTestTargetUserIsNotGardenMemberException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();

        User mockUser = new User("a@a.com", "YEPA");
        Garden mockGarden = new Garden("prove");
        GardenMember mockedMember = new GardenMember(mockGarden, mockUser, GardenRole.NORMAL);

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);
        Mockito.when(userService.findByUsername(mockUser.getUsername())).thenReturn(mockUser);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName()+"/"+mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void removeUserTestTargetHasEqualOrHigherRoleException() throws Exception {

        User user = TestUtils.getMockUserWithGardenOwner();

        Garden garden = user.getGarden().getGarden();
        user.getGarden().setRole(GardenRole.ADMIN);

        User mockUser = new User("a@a.com", "YEPA");
        GardenMember mockedMember = new GardenMember(garden, mockUser, GardenRole.ADMIN);

        Mockito.when(auth.getPrincipal()).thenReturn( user );

        Mockito.when(gardenService.findByName(any())).thenReturn(garden);
        Mockito.when(gardenService.findByUser(user)).thenReturn(user.getGarden());
        Mockito.when(gardenService.findByUser(mockUser)).thenReturn(mockedMember);
        Mockito.when(userService.findByUsername(mockUser.getUsername())).thenReturn(mockUser);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/gardens/"+garden.getName()+"/"+mockUser.getUsername())
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ForbiddenException))
                .andReturn();
    }
}
