package com.playground.user_manager.auth.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.playground.user_manager.auth.api.dto.AuthCodeGenerationRequest;
import com.playground.user_manager.auth.api.dto.AuthCodeVerificationRequest;
import com.playground.user_manager.auth.api.dto.RegisterUserDTO;
import com.playground.user_manager.auth.api.dto.RegisteredUser;
import com.playground.user_manager.auth.service.AuthService;
import com.playground.user_manager.auth.service.JwtGenerator;
import com.playground.user_manager.auth.service.RegistrationService;
import com.playground.user_manager.errors.advice.ControllerAdvice;
import com.playground.user_manager.errors.custom.UserManagerError;
import com.playground.user_manager.errors.exceptions.ResourceAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    @Mock
    private AuthService authService;
    @Mock
    private JwtGenerator jwtGenerator;
    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private AuthController controller;

    private JacksonTester<AuthCodeGenerationRequest> authCodeGenerationRequestJacksonTester;
    private JacksonTester<AuthCodeVerificationRequest> authCodeVerificationRequestJacksonTester;
    private JacksonTester<RegisterUserDTO> registerUserDTOTester;
    private JacksonTester<UserManagerError> errorJacksonTester;

    @BeforeEach
    void setUp() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ValidationExceptionHandler())
                .setControllerAdvice(new ControllerAdvice())
                .build();
    }

    @Test
    void requestCode_happyPath_returnsOk() throws Exception {
        //given
        var email = "user@example.com";
        var request = new AuthCodeGenerationRequest(email);
        doNothing().when(authService).generateAuthCode(email);

        //when
        var result = mockMvc.perform(post("/auth/request-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authCodeGenerationRequestJacksonTester.write(request).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(200, result.getStatus())
        );
    }

    @Test
    void requestCode_invalidEmail_returnsBadRequest() throws Exception {
        //given
        var email = "not-an-email";
        var request = new AuthCodeGenerationRequest(email);

        //when
        var result = mockMvc.perform(post("/auth/request-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authCodeGenerationRequestJacksonTester.write(request).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(400, result.getStatus())
        );
    }

    @Test
    void verifyCode_happyPath_returnsOk() throws Exception {
        //given
        var email = "user@example.com";
        var alias = "test-alias";
        var userId = UUID.randomUUID().toString();
        var registeredUser = new RegisteredUser(userId, email, alias);
        var code = "123456";
        var token = "token";
        var request = new AuthCodeVerificationRequest(email, code);
        when(authService.verifyCode(email, code)).thenReturn(true);
        when(jwtGenerator.generate(registeredUser)).thenReturn(token);
        when(registrationService.getRegisteredUser(email)).thenReturn(Optional.of(registeredUser));

        //when
        var result = mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authCodeVerificationRequestJacksonTester.write(request).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(200, result.getStatus()),
                () -> assertEquals("application/json", result.getContentType()),
                () -> assertEquals("UTF-8", result.getCharacterEncoding()),
                () -> assertEquals("{\"token\":\"token\"}", result.getContentAsString())
        );
    }

    @Test
    void verifyCde_unauthorized_returnsUnauthorized() throws Exception {
        //given
        var email = "user@example.com";
        var code = "123456";
        var request = new AuthCodeVerificationRequest(email, code);
        when(authService.verifyCode(email, code)).thenReturn(false);

        //when
        var result = mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authCodeVerificationRequestJacksonTester.write(request).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(401, result.getStatus())
        );
    }

    @Test
    void verifyCode_missingFields_returnsBadRequest() throws Exception {
        //given
        var email = "user@example.com";
        var request = new AuthCodeVerificationRequest(email, null);

        //when
        var result = mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authCodeVerificationRequestJacksonTester.write(request).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(400, result.getStatus())
        );
    }

    @Test
    void testRegisterUser_withMandatoryFields() throws Exception {
        //given
        var alias = "test-user";
        var email = "someemail@gmail.com";

        //when
        var res = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerUserDTOTester.write(new RegisterUserDTO(alias, email, null, null)).getJson()))
                .andReturn().getResponse();

        //then
        assertEquals(200, res.getStatus());
    }

    @Test
    void testRegisterUser_failure_alreadyExists() throws Exception {
        //given
        var alias = "test-user";
        var email = "someemail@gmail.com";
        var apiVersion = "1.0";
        var code = "NE-001";
        var message = "Resource already exists";
        var registerUser = new RegisterUserDTO(alias, email, null, null);
        var errorMessage = "User with alias " + alias + " already exists";
        var domain = "user";
        var reason = "exists";
        var errorReportUri = "";
        var ex = new ResourceAlreadyExistsException(errorMessage, domain);

        doThrow(ex).when(registrationService).register(registerUser);

        var error = new UserManagerError(apiVersion, code, message, domain, reason, errorMessage, errorReportUri);

        //when
        var res = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerUserDTOTester.write(registerUser).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(400, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertEquals("UTF-8", res.getCharacterEncoding()),
                () -> assertEquals(errorJacksonTester.write(error).getJson(), res.getContentAsString())
        );
    }

    @Test
    void testRegisterUser_failure_missingAlias() throws Exception {
        //given
        var email = "someemail@gmail.com";
        var message = "Validation failed";
        //when
        var res = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerUserDTOTester.write(new RegisterUserDTO(null, email, null, null)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertTrue(res.getContentAsString().contains(message))
        );
    }

    @Test
    void testCreateUser_failure_missingEmail() throws Exception {
        //given
        var alias = "test-user";
        var message = "Validation failed";
        //when
        var res = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerUserDTOTester.write(new RegisterUserDTO(alias, null, null, null)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertTrue(res.getContentAsString().contains(message))
        );
    }

    @Test
    void testCreateUser_failure_invalidEmail() throws Exception {
        //given
        var alias = "test-user";
        var email = "someemail";
        var message = "Validation failed";
        //when
        var res = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerUserDTOTester.write(new RegisterUserDTO(alias, email, null, null)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertTrue(res.getContentAsString().contains(message))
        );
    }

    @Test
    void testCreateUser_failure_invalidBirthday() throws Exception {
        //given
        var alias = "test-user";
        var email = "someemail@gmail.com";
        var message = "Validation failed";
        var birthday = LocalDate.now().plusDays(1);
        //when
        var res = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerUserDTOTester.write(new RegisterUserDTO(alias, email, birthday, null)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertTrue(res.getContentAsString().contains(message))
        );
    }

    @Test
    void testCreateUser_failure_invalidGender() throws Exception {
        //given
        var alias = "test-user";
        var email = "someemail@gmail.com";
        var gender = "invalid";
        var message = "Validation failed";
        //when
        var res = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerUserDTOTester.write(new RegisterUserDTO(alias, email, null, gender)).getJson()))
                .andReturn().getResponse();
        //then
        assertAll(
                () -> assertEquals(400, res.getStatus()),
                () -> assertEquals("application/json", res.getContentType()),
                () -> assertTrue(res.getContentAsString().contains(message))
        );
    }


    // Simple global exception handler for validation errors
    @RestControllerAdvice
    static class ValidationExceptionHandler {
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public org.springframework.http.ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
            return org.springframework.http.ResponseEntity.badRequest().body("Validation failed");
        }
    }
}