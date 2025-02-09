package com.playground.multiplication.challenge;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attempts")
@RequiredArgsConstructor
public class AttemptController {

    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<ChallengeResultDTO> verify(@RequestBody @Valid ChallengeAttemptDTO attempt) {
        return ResponseEntity.ok(challengeService.verifyAttempt(attempt));
    }
}
