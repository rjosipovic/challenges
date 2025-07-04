package com.playground.analytics_manager.outbound.api.dto;

import lombok.Value;

@Value
public class StatisticsUpdate {

    String userId;
    String game;
    String difficulty;
    boolean success;
}
