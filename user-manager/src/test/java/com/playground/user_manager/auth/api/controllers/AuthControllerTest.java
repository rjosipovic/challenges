package com.playground.user_manager.auth.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.user_manager.auth.api.dto.AuthCodeGenerationRequest;
import com.playground.user_manager.auth.api.dto.AuthCodeVerificationRequest;
import com.playground.user_manager.auth.service.AuthService;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    private JacksonTester<AuthCodeGenerationRequest> authCodeGenerationRequestJacksonTester;
    private JacksonTester<AuthCodeVerificationRequest> authCodeVerificationRequestJacksonTester;

    @BeforeEach
    void setUp() {
        var objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ValidationExceptionHandler())
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
        var code = "123456";
        var request = new AuthCodeVerificationRequest(email, code);
        when(authService.verifyCode(email, code)).thenReturn(true);

        //when
        var result = mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authCodeVerificationRequestJacksonTester.write(request).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(200, result.getStatus())
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

    // Simple global exception handler for validation errors
    @RestControllerAdvice
    static class ValidationExceptionHandler {
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public org.springframework.http.ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
            return org.springframework.http.ResponseEntity.badRequest().body("Validation failed");
        }
    }
}