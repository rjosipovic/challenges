package com.playground.user_manager.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.playground.user_manager.errors.advice.ControllerAdvice;
import com.playground.user_manager.errors.controller.ErrorController;
import com.playground.user_manager.errors.custom.UserManagerError;
import com.playground.user_manager.errors.custom.UserManagerErrorAttributes;
import com.playground.user_manager.errors.exceptions.ResourceNotFoundException;
import com.playground.user_manager.user.api.controllers.UserController;
import com.playground.user_manager.user.api.dto.CreateUserDTO;
import com.playground.user_manager.user.model.User;
import com.playground.user_manager.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
@AutoConfigureJsonTesters
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private JacksonTester<User> userJacksonTester;
    private JacksonTester<List<User>> usersJacksonTester;
    private JacksonTester<CreateUserDTO> createUserJacksonTester;
    private JacksonTester<UserManagerError> errorJacksonTester;

    @BeforeEach
    void setup() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController, new ErrorController(new UserManagerErrorAttributes("1.0", "")))
                .setControllerAdvice(new ControllerAdvice())
                .build();
    }

    @Test
    void testGetUserByAlias_success() throws Exception {

        //given
        var userId = UUID.randomUUID().toString();
        var alias = "test-user";
        var user = new User(userId, alias);

        when(userService.getUserByAlias("test-user")).thenReturn(user);

        //when
        var res = mockMvc.perform(get(String.format("/users/alias/%s", alias)))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(200, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals("UTF-8", res.getCharacterEncoding()),
                () -> assertEquals(userJacksonTester.write(user).getJson(), res.getContentAsString())
        );
    }

    @Test
    void testGetUserByAlias_userNotFound() throws Exception {

        //given
        var alias = "test-user";
        var apiVersion = "1.0";
        var code = "NE-001";
        var message = "Resource not found";
        var domain = "user";
        var reason = "not found";
        var errorMessage = "User with alias " + alias + " not found";
        var errorReportUri = "";
        var ex = new ResourceNotFoundException(errorMessage, domain);
        when(userService.getUserByAlias(alias)).thenThrow(ex);

        var error = new UserManagerError(apiVersion, code, message, domain, reason, errorMessage, errorReportUri);

        //when
        var res = mockMvc.perform(get(String.format("/users/alias/%s", alias)))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(404, res.getStatus()),
                () -> assertFalse(res.getContentAsString().isEmpty()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals(errorJacksonTester.write(error).getJson(), res.getContentAsString())
        );
    }

    @Test
    void testGetAllUsers() throws Exception {
        //given
        var userId1 = UUID.randomUUID().toString();
        var userId2 = UUID.randomUUID().toString();
        var alias1 = "test-user1";
        var alias2 = "test-user2";
        var user1 = new User(userId1, alias1);
        var user2 = new User(userId2, alias2);
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));
        //when
        var res = mockMvc.perform(get("/users")).andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(200, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals("UTF-8", res.getCharacterEncoding()),
                () -> assertEquals(usersJacksonTester.write(List.of(user1, user2)).getJson(), res.getContentAsString())
        );
    }

    @Test
    void testGetUsersByIds() throws Exception {
        //given
        var userId1 = UUID.randomUUID().toString();
        var userId2 = UUID.randomUUID().toString();
        var alias1 = "test-user1";
        var alias2 = "test-user2";
        var user1 = new User(userId1, alias1);
        var user2 = new User(userId2, alias2);
        when(userService.getUsersByIds(List.of(userId1, userId2))).thenReturn(List.of(user1, user2));
        //when
        var res = mockMvc.perform(get("/users").param("ids", userId1, userId2)).andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(200, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals("UTF-8", res.getCharacterEncoding()),
                () -> assertEquals(usersJacksonTester.write(List.of(user1, user2)).getJson(), res.getContentAsString())

        );
    }
}