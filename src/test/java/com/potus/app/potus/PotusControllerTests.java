package com.potus.app.potus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.potus.app.TestUtils;
import com.potus.app.exception.BadRequestException;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.potus.controller.PotusController;
import com.potus.app.potus.model.*;
import com.potus.app.potus.payload.request.PotusActionRequest;
import com.potus.app.potus.payload.request.PotusEventRequest;
import com.potus.app.potus.payload.response.PotusModifierStoreResponse;
import com.potus.app.potus.service.PotusEventsService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.TestConfig;
import com.potus.app.user.model.User;
import com.potus.app.user.service.TrophyService;
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

import java.util.*;

import static com.potus.app.potus.utils.PotusUtils.PRUNING_CURRENCY_BONUS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {PotusController.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class PotusControllerTests {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;


    @MockBean
    private Authentication auth;

    private SecurityContext securityContext;

    @MockBean
    private PotusService potusService;

    @MockBean
    private PotusEventsService potusEventsService;

    @MockBean
    private TrophyService trophyService;

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
    public void getPotusTest() throws Exception {
        Mockito.when(auth.getPrincipal()).thenReturn(TestUtils.getMockUser());
        final String expectedResponseContent = objectMapper.writeValueAsString(TestUtils.getMockUser().getPotus());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/potus")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void doActionOKTest() throws Exception {
        User mockedUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        PotusActionRequest potusActionRequest = new PotusActionRequest();
        potusActionRequest.setAction("prune");

        User user = new User(mockedUser.getEmail(),mockedUser.getEmail());
        user.setPotus(mockedUser.getPotus());

        user.setCurrency(mockedUser.getCurrency() + PRUNING_CURRENCY_BONUS);
        Mockito.when(potusService.doFilterAction(any(),any())).thenReturn(PRUNING_CURRENCY_BONUS);
        Mockito.when(userService.addCurrency(any(),any())).thenReturn(user);

        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("user", user);
        responseMap.put("trophies", List.of());

        final String expectedResponseContent = objectMapper.writeValueAsString(responseMap);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/action")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusActionRequest));

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();

    }

    @Test
    public void doActionIsNullErrorTest() throws Exception {
        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        PotusActionRequest potusActionRequest = new PotusActionRequest();


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/action")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusActionRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();


    }

    @Test
    public void doActionNotExistsErrorTest() throws Exception {
        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        PotusActionRequest potusActionRequest = new PotusActionRequest();
        potusActionRequest.setAction("YEPAAA");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/action")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusActionRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();


    }

    @Test
    public void doActionAlreadyDidItErrorTest() throws Exception {
        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        PotusActionRequest potusActionRequest = new PotusActionRequest();
        potusActionRequest.setAction("prune");


        Mockito.when(potusService.doFilterAction(any(),any())).thenThrow(new BadRequestException());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/action")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusActionRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();

    }

    @Test
    public void potusEventOK() throws Exception {

        User mockUser = TestUtils.getMockNewUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        PotusEventRequest potusEventRequest = new PotusEventRequest();
        potusEventRequest.setValues(11.0, 11.0);

        Potus potus = new Potus();

        Mockito.when(potusEventsService.doEvent(any(),any(),any())).thenReturn(potus);

        final String expectedResponseContent = objectMapper.writeValueAsString(potus);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/events")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusEventRequest));

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void potusEventError() throws Exception {

        User mockUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockUser);

        PotusEventRequest potusEventRequest = new PotusEventRequest();


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/events")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(potusEventRequest));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andReturn();
    }


    @Test
    public void getPotusBuffsTest() throws Exception {

        User mockedUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        Modifier modifier = new Modifier("TEST", ModifierEffectType.WATERING_MODIFIER, 1., 1., ModifierType.PERMANENT_BUFF);
        Set<PotusModifier> modifiers = Collections.singleton(new PotusModifier(mockedUser.getPotus(),modifier,1));

        mockedUser.getPotus().setBuffs(modifiers);

        final String expectedResponseContent = objectMapper.writeValueAsString(modifiers.stream().toList());


        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/potus/buffs")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getPotusDebuffsTest() throws Exception {

        User mockedUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        Modifier modifier = new Modifier("TEST", ModifierEffectType.WATERING_MODIFIER, 1., 1., ModifierType.TEMPORAL_DEBUFF);
        Set<PotusModifier> modifiers = Collections.singleton(new PotusModifier(mockedUser.getPotus(),modifier,1));

        mockedUser.getPotus().setDebuffs(modifiers);

        final String expectedResponseContent = objectMapper.writeValueAsString(modifiers.stream().toList());


        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/potus/debuffs")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void getPotusStoreTest() throws Exception {

        User mockedUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        Modifier modifier = new Modifier("TEST", ModifierEffectType.WATERING_MODIFIER, 1., 1., ModifierType.PERMANENT_BUFF);
        Set<PotusModifier> modifiers = Collections.singleton(new PotusModifier(mockedUser.getPotus(),modifier,1));

        mockedUser.getPotus().setBuffs(modifiers);

        List<PotusModifierStoreResponse> potusModifierStoreResponses = new ArrayList<>();
        modifiers.forEach(buff ->{
            potusModifierStoreResponses.add(new PotusModifierStoreResponse(buff));
        });

        final String expectedResponseContent = objectMapper.writeValueAsString(potusModifierStoreResponses);


        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/potus/store")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();
    }

    @Test
    public void buyUpgradePotusModifierTest() throws Exception {

        User mockedUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        Modifier modifier = new Modifier("TEST", ModifierEffectType.WATERING_MODIFIER, 1., 1., ModifierType.PERMANENT_BUFF);
        Set<PotusModifier> modifiers = Collections.singleton(new PotusModifier(mockedUser.getPotus(),modifier,1));

        mockedUser.getPotus().setBuffs(modifiers);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/store/buy/TEST")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
    }


    @Test
    public void buyUpgradePotusModifierNotExistsTest() throws Exception {

        User mockedUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        Modifier modifier = new Modifier("TEST", ModifierEffectType.WATERING_MODIFIER, 1., 1., ModifierType.PERMANENT_BUFF);
        Set<PotusModifier> modifiers = Collections.singleton(new PotusModifier(mockedUser.getPotus(),modifier,1));

        mockedUser.getPotus().setBuffs(modifiers);


        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/store/buy/ADEU")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }

    @Test
    public void buyUpgradePotusModifierNotMoneyTest() throws Exception {

        User mockedUser = TestUtils.getMockUser();
        Mockito.when(auth.getPrincipal()).thenReturn(mockedUser);

        Modifier modifier = new Modifier("TEST", ModifierEffectType.WATERING_MODIFIER, 1., 1., ModifierType.PERMANENT_BUFF);
        Set<PotusModifier> modifiers = Collections.singleton(new PotusModifier(mockedUser.getPotus(),modifier,1));

        mockedUser.getPotus().setBuffs(modifiers);

        doThrow(new ResourceNotFoundException()).when(userService).upgradeModifier(any(),any());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/potus/store/buy/ADEU")
                .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andReturn();
    }
 // no money
    // not found
    // ok


}