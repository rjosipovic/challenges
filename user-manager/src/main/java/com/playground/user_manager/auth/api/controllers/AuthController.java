package com.playground.user_manager.auth.api.controllers;

import com.playground.user_manager.auth.api.dto.AuthCodeGenerationRequest;
import com.playground.user_manager.auth.api.dto.AuthCodeVerificationRequest;
import com.playground.user_manager.auth.service.AuthService;
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

    @PostMapping("/request-code")
    public ResponseEntity<?> requestCode(@RequestBody @Valid AuthCodeGenerationRequest generationRequest) {
        log.info("Received auth request: {}", generationRequest);
        authService.generateAuthCode(generationRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Boolean> verifyCode(@RequestBody @Valid AuthCodeVerificationRequest verificationRequest) {
        log.info("Received auth request: {}", verificationRequest);
        var isValid = authService.verifyCode(verificationRequest.getEmail(), verificationRequest.getCode());
        return ResponseEntity.ok(isValid);
    }
}