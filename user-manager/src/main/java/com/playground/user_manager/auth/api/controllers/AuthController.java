package com.playground.user_manager.auth.api.controllers;

import com.nimbusds.jose.JOSEException;
import com.playground.user_manager.auth.api.dto.AuthCodeGenerationRequest;
import com.playground.user_manager.auth.api.dto.AuthCodeVerificationRequest;
import com.playground.user_manager.auth.api.dto.AuthTokenResponse;
import com.playground.user_manager.auth.api.dto.RegisterUserDTO;
import com.playground.user_manager.auth.service.AuthService;
import com.playground.user_manager.auth.service.JwtGenerator;
import com.playground.user_manager.auth.service.RegistrationService;
import com.playground.user_manager.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/request-code")
    public ResponseEntity<?> requestCode(@RequestBody @Valid AuthCodeGenerationRequest generationRequest) {
        log.info("Received code request: {}", generationRequest);
        authService.generateAuthCode(generationRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<AuthTokenResponse> verifyCode(@RequestBody @Valid AuthCodeVerificationRequest verificationRequest) throws JOSEException {
        log.info("Received code verification request: {}", verificationRequest);

        var email = verificationRequest.getEmail();
        var code = verificationRequest.getCode();
        var isValid = authService.verifyCode(email, code);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var registeredUser = registrationService.getRegisteredUser(email);

        if (registeredUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        var token = jwtGenerator.generate(registeredUser.get());
        return ResponseEntity.ok(new AuthTokenResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid RegisterUserDTO registerUserDTO) {
        log.info("Received register request: {}", registerUserDTO);
        registrationService.register(registerUserDTO);
        return ResponseEntity.ok().build();
    }
}