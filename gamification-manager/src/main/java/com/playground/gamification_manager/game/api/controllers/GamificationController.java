package com.playground.gamification_manager.game.api.controllers;

import com.playground.gamification_manager.game.api.dto.ChallengeSolvedDTO;
import com.playground.gamification_manager.game.service.interfaces.GameService;
import com.playground.gamification_manager.game.service.model.GameResult;
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
public class GamificationController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResult> processChallenge(@RequestBody @Valid ChallengeSolvedDTO challengeSolved) {
        return ResponseEntity.ok(gameService.process(challengeSolved));
    }
}
