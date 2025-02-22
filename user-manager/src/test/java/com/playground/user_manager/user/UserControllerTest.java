package com.playground.user_manager.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.user_manager.errors.ControllerAdvice;
import com.playground.user_manager.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
@AutoConfigureJsonTesters
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private JacksonTester<User> userJacksonTester;
    private JacksonTester<CreateUserDTO> createUserJacksonTester;

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ControllerAdvice())
                .build();
    }

    @Test
    void testGetUserByAlias_success() throws Exception {

        var user = new User("1", "test-user");

        when(userService.getUserByAlias("test-user")).thenReturn(user);

        var res = mockMvc.perform(get("/users/alias/test-user"))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(200, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals("UTF-8", res.getCharacterEncoding()),
                () -> assertEquals(userJacksonTester.write(user).getJson(), res.getContentAsString())
        );
    }

    @Test
    void testGetUserByAlias_failure() throws Exception {

        when(userService.getUserByAlias("test-user")).thenThrow(ResourceNotFoundException.class);

        var res = mockMvc.perform(get("/users/alias/test-user"))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, res.getStatus()),
                () -> assertFalse(res.getContentAsString().isEmpty()),
                () -> assertEquals("application/json", res.getContentType())

        );
    }

    @Test
    void testCreateUser() throws Exception {
        var alias = "test-user";
        var userId = "1";
        var user = new User(userId, alias);
        var createUser = new CreateUserDTO(alias);

        when(userService.createUser(createUser)).thenReturn(new User("1", alias));

        var res = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJacksonTester.write(new CreateUserDTO(alias)).getJson()))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(200, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals("UTF-8", res.getCharacterEncoding()),
                () -> assertEquals(userJacksonTester.write(user).getJson(), res.getContentAsString())
        );
    }
}