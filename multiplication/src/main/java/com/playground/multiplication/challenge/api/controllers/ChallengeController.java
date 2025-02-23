package com.playground.multiplication.challenge.api.controllers;

import com.playground.multiplication.challenge.services.model.Challenge;
import com.playground.multiplication.challenge.services.interfaces.ChallengeGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeGeneratorService challengeGeneratorService;

    @GetMapping("/random")
    public ResponseEntity<Challenge> get() {
        return ResponseEntity.ok(challengeGeneratorService.randomChallenge());
    }
}
