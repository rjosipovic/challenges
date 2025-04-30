package com.playground.challenge_manager.challenge.clients.dto;

import lombok.Value;

import java.util.Set;

@Value
public class GameResult {

    String userId;
    int score;
    Set<String> badges;
}
