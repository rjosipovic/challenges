package com.playground.challenge_manager.challenge.clients.gamification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.challenge_manager.challenge.clients.dto.ChallengeSolvedDTO;
import com.playground.challenge_manager.challenge.clients.dto.GameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamificationClientTest {

    @Mock
    private GamificationClientConfig config;
    @Mock
    private ExchangeFunction exchangeFunction;
    private GamificationClient client;

    private JacksonTester<ChallengeSolvedDTO> jsonChallengeSolved;
    private JacksonTester<GameResult> jsonGameResult;

    @BeforeEach
    void init() {
        JacksonTester.initFields(this, new ObjectMapper());
        when(config.getProtocol()).thenReturn("http");
        when(config.getHost()).thenReturn("localhost");
        when(config.getPort()).thenReturn("8080");
        var builder = WebClient.builder().exchangeFunction(exchangeFunction);
        client = new GamificationClient(config, builder);
    }

    @Test
    void shouldPublishChallengeSolved() throws IOException {
        //given
        var userId = UUID.randomUUID().toString();
        var challengeAttemptId = UUID.randomUUID().toString();
        var firstNumber = 12;
        var secondNumber = 23;
        var correct = Boolean.TRUE;
        var game = "multiplication";
        var score = 20;
        var badges = Set.of("FIRST_WON");
        var gameResult = new GameResult(userId, score, badges);
        var mockResponse = ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(String.valueOf(jsonGameResult.write(gameResult).getJson()))
                .build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(mockResponse));
        when(config.getPublishAttemptPath()).thenReturn("/gamification");

        //when
        var result = client.publishChallengeSolved(new ChallengeSolvedDTO(userId, challengeAttemptId, firstNumber, secondNumber, correct, game));

        //then
        assertAll(
                () -> assertEquals(gameResult, result),
                () -> assertEquals(score, result.getScore()),
                () -> assertEquals(badges, result.getBadges())
        );
    }

    @Test
    void shouldPublishChallengeSolved_onBadRequest() {
        //given
        var userId = UUID.randomUUID().toString();
        var challengeAttemptId = UUID.randomUUID().toString();
        var firstNumber = 12;
        var secondNumber = 23;
        var correct = Boolean.TRUE;
        var game = "multiplication";

        var response = ClientResponse.create(HttpStatus.BAD_REQUEST).body("bad request").build();
        when(config.getPublishAttemptPath()).thenReturn("/gamification");
        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(response));

        //when
        assertThrows(WebClientResponseException.class, () -> client.publishChallengeSolved(new ChallengeSolvedDTO(userId, challengeAttemptId, firstNumber, secondNumber, correct, game)));
    }

    @Test
    void shouldPublishChallengeSolved_onNotFound() {
        //given
        var userId = UUID.randomUUID().toString();
        var challengeAttemptId = UUID.randomUUID().toString();
        var firstNumber = 12;
        var secondNumber = 23;
        var correct = Boolean.TRUE;
        var game = "multiplication";

        var response = ClientResponse.create(HttpStatus.NOT_FOUND).body("not found").build();
        when(config.getPublishAttemptPath()).thenReturn("/gamification");
        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(response));

        //when
        assertThrows(WebClientResponseException.class, () -> client.publishChallengeSolved(new ChallengeSolvedDTO(userId, challengeAttemptId, firstNumber, secondNumber, correct, game)));
    }

    @Test
    void shouldPublishChallengeSolved_onInternalServerError() {
        //given
        var userId = UUID.randomUUID().toString();
        var challengeAttemptId = UUID.randomUUID().toString();
        var firstNumber = 12;
        var secondNumber = 23;
        var correct = Boolean.TRUE;
        var game = "multiplication";

        var response = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).body("internal server error").build();
        when(config.getPublishAttemptPath()).thenReturn("/gamification");
        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(response));

        //when
        assertThrows(WebClientResponseException.class, () -> client.publishChallengeSolved(new ChallengeSolvedDTO(userId, challengeAttemptId, firstNumber, secondNumber, correct, game)));
    }
}