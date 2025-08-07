package com.playground.user_manager.auth.api.controllers;

import com.nimbusds.jose.JOSEException;
import com.playground.user_manager.auth.api.dto.AuthCodeGenerationRequest;
import com.playground.user_manager.auth.api.dto.AuthCodeVerificationRequest;
import com.playground.user_manager.auth.api.dto.AuthTokenResponse;
import com.playground.user_manager.auth.api.dto.RegisterUserDTO;
import com.playground.user_manager.auth.api.dto.RegisteredUser;
import com.playground.user_manager.auth.service.AuthService;
import com.playground.user_manager.auth.service.JwtGenerator;
import com.playground.user_manager.auth.service.RegistrationService;
import com.playground.user_manager.errors.exceptions.InvalidVerificationCodeException;
import com.playground.user_manager.errors.exceptions.TokenGenerationException;
import com.playground.user_manager.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;
    private final JwtGenerator jwtGenerator;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        log.info("Received register request: {}", registerUserDTO);
        registrationService.register(registerUserDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-code")
    public ResponseEntity<?> requestCode(@RequestBody @Valid AuthCodeGenerationRequest generationRequest) {
        log.info("Received code request: {}", generationRequest);
        authService.generateAuthCode(generationRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<AuthTokenResponse> verifyCode(@RequestBody @Valid AuthCodeVerificationRequest verificationRequest) {
        log.info("Received code verification request: {}", verificationRequest);

        var email = verificationRequest.getEmail();
        var code = verificationRequest.getCode();

        verifyCode(email, code);
        var registeredUser = getRegisteredUser(email);
        var token = generateToken(registeredUser);
        return ResponseEntity.ok(new AuthTokenResponse(token));
    }

    private void verifyCode(String email, String code) {
        var isValid = authService.verifyCode(email, code);

        if (!isValid) {
            var msg = String.format("Code verification failed for email: %s and code: %s", email, code);
            log.info(msg);
            throw new InvalidVerificationCodeException(msg);
        }
    }

    private RegisteredUser getRegisteredUser(String email) {
        return registrationService.getRegisteredUser(email)
                .orElseThrow(() -> new IllegalStateException("Unable to find user with email: " + email));
    }

    private String generateToken(RegisteredUser registeredUser) {
        try {
            return jwtGenerator.generate(registeredUser);
        } catch (JOSEException e) {
            log.error("Unable to generate token", e);
            throw new TokenGenerationException("Unable to generate token", e);
        }
    }
}