package com.playground.user_manager.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<User> userJacksonTester;

    @Autowired
    private JacksonTester<CreateUserDTO> createUserJacksonTester;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetUserByAlias_success() throws Exception {

        var user = new User("1", "test-user");

        when(userService.getUserByAlias("test-user")).thenReturn(Optional.of(user));

        var res = mockMvc.perform(get("/users/alias/test-user"))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(200, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals("UTF-8", res.getCharacterEncoding()),
                () -> assertEquals(user, userJacksonTester.parseObject(res.getContentAsString()))
        );
    }

    @Test
    void testGetUserByAlias_failure() throws Exception {

        when(userService.getUserByAlias("test-user")).thenReturn(Optional.empty());

        var res = mockMvc.perform(get("/users/alias/test-user"))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, res.getStatus())
        );
    }

    @Test
    void testCreateUser() throws Exception {
        var alias = "test-user";
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
                () -> assertEquals(new User("1", alias), userJacksonTester.parseObject(res.getContentAsString()))   
        );
    }
}