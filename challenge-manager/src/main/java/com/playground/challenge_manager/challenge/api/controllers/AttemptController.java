package com.playground.challenge_manager.challenge.api.controllers;

import com.playground.challenge_manager.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.challenge_manager.challenge.api.dto.ChallengeResultDTO;
import com.playground.challenge_manager.challenge.services.interfaces.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attempts")
@RequiredArgsConstructor
public class AttemptController {

    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<ChallengeResultDTO> verify(@RequestBody @Valid ChallengeAttemptDTO attempt) {
        return ResponseEntity.ok(challengeService.verifyAttempt(attempt));
    }

    @GetMapping
    public ResponseEntity<List<ChallengeResultDTO>> findLast10ResultsForUser(@RequestParam("userId") @UUID String userId) {
        return ResponseEntity.ok(challengeService.findLast10ResultsForUser(java.util.UUID.fromString(userId)));
    }
}
