package com.playground.challenge_manager.challenge.clients.gamification;

import com.playground.challenge_manager.challenge.clients.dto.ChallengeSolvedDTO;
import com.playground.challenge_manager.challenge.clients.dto.GameResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GamificationClient {

    private final GamificationClientConfig config;
    private final WebClient webClient;

    public GamificationClient(GamificationClientConfig config, WebClient.Builder builder) {
        this.config = config;
        var protocol = config.getProtocol();
        var host = config.getHost();
        var port = config.getPort();
        var baseURL = String.format("%s://%s:%s", protocol, host, port);
        webClient = builder
                .baseUrl(baseURL)
                .filter(logRequest())
                .filter(logResponse())
                .build();

    }

    public GameResult publishChallengeSolved(ChallengeSolvedDTO challengeSolved) {
        var endpoint = config.getPublishAttemptPath();
        return post(challengeSolved, GameResult.class, endpoint).block();
    }

    private <T, R> Mono<R> post(T requestBody, Class<R> responseType, String endpoint) {
        return webClient.post()
                .uri(endpoint)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType);
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            var clientRequestMethod = clientRequest.method();
            var clientRequestURI = clientRequest.url();
            log.info("Request: {} {}", clientRequestMethod, clientRequestURI);
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            var responseStatusCode = clientResponse.statusCode();
            log.info("Response Status Code: {}", responseStatusCode);
            return Mono.just(clientResponse);
        });
    }
}
