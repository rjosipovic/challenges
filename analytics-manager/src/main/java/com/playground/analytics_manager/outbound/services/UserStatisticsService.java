package com.playground.analytics_manager.outbound.services;

import com.playground.analytics_manager.outbound.api.dto.UserSuccessRate;

public interface UserStatisticsService {

    UserSuccessRate getUserStatistics(String userId);
}
