package com.playground.gamification_manager.game.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.gamification_manager.game.api.dto.ChallengeSolvedDTO;
import com.playground.gamification_manager.game.dataaccess.domain.BadgeType;
import com.playground.gamification_manager.game.service.interfaces.GameService;
import com.playground.gamification_manager.game.service.model.GameResult;
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

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class GamificationControllerTest {

    private MockMvc mvc;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GamificationController controller;

    private JacksonTester<ChallengeSolvedDTO> jsonChallengeSolved;

    private JacksonTester<GameResult> jsonGameResult;

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldSendChallengeSolved() throws Exception {
        //given
        var userId = UUID.randomUUID().toString();
        var challengeAttemptId = UUID.randomUUID().toString();
        var firstNumber = 1;
        var secondNumber = 2;
        var correct = true;
        var game = "multiplication";
        var challengeSolvedDTO = new ChallengeSolvedDTO(userId, challengeAttemptId, firstNumber, secondNumber, correct, game);

        var gameResult = new GameResult(userId, 20, Set.of(BadgeType.FIRST_WON));
        when(gameService.process(challengeSolvedDTO)).thenReturn(gameResult);

        //when
        var response = mvc.perform(post("/attempts")
                .contentType("application/json")
                .content(jsonChallengeSolved.write(challengeSolvedDTO).getJson()))
                .andReturn().getResponse();

        //then
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType()),
                () -> assertEquals(jsonGameResult.write(gameResult).getJson(), response.getContentAsString())
        );
    }
}