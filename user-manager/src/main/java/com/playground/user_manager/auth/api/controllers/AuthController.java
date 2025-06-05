package com.playground.user_manager.auth.api.controllers;

import com.nimbusds.jose.JOSEException;
import com.playground.user_manager.auth.api.dto.AuthCodeGenerationRequest;
import com.playground.user_manager.auth.api.dto.AuthCodeVerificationRequest;
import com.playground.user_manager.auth.api.dto.AuthTokenResponse;
import com.playground.user_manager.auth.service.AuthService;
import com.playground.user_manager.auth.service.JwtGenerator;
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
    private final JwtGenerator jwtGenerator;

    @PostMapping("/request-code")
    public ResponseEntity<?> requestCode(@RequestBody @Valid AuthCodeGenerationRequest generationRequest) {
        log.info("Received auth request: {}", generationRequest);
        authService.generateAuthCode(generationRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<AuthTokenResponse> verifyCode(@RequestBody @Valid AuthCodeVerificationRequest verificationRequest) throws JOSEException {
        log.info("Received auth request: {}", verificationRequest);
        var email = verificationRequest.getEmail();
        var code = verificationRequest.getCode();
        var isValid = authService.verifyCode(email, code);
        if (isValid) {
            var token = jwtGenerator.generate(email);
            return ResponseEntity.ok(new AuthTokenResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}