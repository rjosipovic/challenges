package com.playground.challenge_manager.challenge.clients.gamification;

import com.playground.challenge_manager.challenge.clients.dto.ChallengeSolvedDTO;
import com.playground.challenge_manager.challenge.clients.dto.GameResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GamificationClient {

    private static final String GAMIFICATION_PROTOCOL = "http";
    private static final String GAMIFICATION_HOST = "localhost";
    private static final String GAMIFICATION_PORT = "8082";
    private static final String ATTEMPTS_ENDPOINT = "/attempts";

    private WebClient webClient;

    @PostConstruct
    private void init() {
        var baseURL = String.format("%s://%s:%s", GAMIFICATION_PROTOCOL, GAMIFICATION_HOST, GAMIFICATION_PORT);
        webClient = WebClient.builder().baseUrl(baseURL).build();
    }

    public void publishChallengeSolved(ChallengeSolvedDTO challengeSolved) {
        post(challengeSolved, GameResult.class).block();
    }

    private <T, R> Mono<R> post(T requestBody, Class<R> responseType) {
        return webClient.post()
                .uri(ATTEMPTS_ENDPOINT)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType);
    }
}
