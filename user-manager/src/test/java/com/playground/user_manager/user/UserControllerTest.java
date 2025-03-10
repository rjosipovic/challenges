package com.playground.user_manager.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
    private JacksonTester<List<User>> usersJacksonTester;
    private JacksonTester<CreateUserDTO> createUserJacksonTester;

    @BeforeEach
    void setup() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);
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
    void testGetUserByAlias_userNotFound() throws Exception {

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
    void testCreateUser_withMandatoryFields() throws Exception {
        var alias = "test-user";
        var email = "someemail@gmail.com";
        var birthday = LocalDate.of(1981, 12, 24);
        var userId = UUID.randomUUID().toString();
        var user = new User(userId, alias);
        var createUser = new CreateUserDTO(alias, email, null, null);

        when(userService.createUser(createUser)).thenReturn(new User(userId, alias));

        var res = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserJacksonTester.write(new CreateUserDTO(alias, email, null, null)).getJson()))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(200, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals("UTF-8", res.getCharacterEncoding()),
                () -> assertEquals(userJacksonTester.write(user).getJson(), res.getContentAsString())
        );

    }

    @Test
    void testCreateUser_failure_missingAllMandatoryFields() throws Exception {
        var alias = "test-user";
        var email = "someemail@gmail.com";
        var userId = UUID.randomUUID().toString();
        var user = new User(userId, alias);
        var createUser = new CreateUserDTO(alias, email, null, null);

        when(userService.createUser(createUser)).thenReturn(new User(userId, alias));

        var res = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserJacksonTester.write(new CreateUserDTO(alias, email, null, null)).getJson()))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(200, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals("UTF-8", res.getCharacterEncoding()),
                () -> assertEquals(userJacksonTester.write(user).getJson(), res.getContentAsString())
        );
    }

    @Test
    void testCreateUser_failure_missingAlias() throws Exception {
        //given
        var email = "someemail@gmail.com";
        //when
        var res = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJacksonTester.write(new CreateUserDTO(null, email, null, null)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus())
        );
    }

    @Test
    void testCreateUser_failure_missingEmail() throws Exception {
        //given
        var alias = "test-user";
        //when
        var res = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJacksonTester.write(new CreateUserDTO(alias, null, null, null)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus())
        );
    }

    @Test
    void testCreateUser_failure_invalidEmail() throws Exception {
        //given
        var alias = "test-user";
        var email = "someemail";
        //when
        var res = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJacksonTester.write(new CreateUserDTO(alias, email, null, null)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus())
        );
    }

    @Test
    void testCreateUser_failure_invalidBirthday() throws Exception {
        //given
        var alias = "test-user";
        var email = "someemail@gmail.com";
        var birthday = LocalDate.now().plusDays(1);
        //when
        var res = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJacksonTester.write(new CreateUserDTO(alias, email, birthday, null)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus())
        );
    }

    @Test
    void testCreateUser_failure_invalidGender() throws Exception {
        //given
        var alias = "test-user";
        var email = "someemail@gmail.com";
        var gender = "invalid";
        //when
        var res = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJacksonTester.write(new CreateUserDTO(alias, email, null, gender)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus())
        );
    }

    @Test
    void testGetAllUsers() throws Exception {
        //given
        var user1 = new User("1", "test-user1");
        var user2 = new User("2", "test-user2");
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
}