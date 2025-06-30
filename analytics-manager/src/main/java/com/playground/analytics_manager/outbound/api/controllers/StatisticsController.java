package com.playground.analytics_manager.outbound.api.controllers;

import com.playground.analytics_manager.outbound.api.dto.UserSuccessRate;
import com.playground.analytics_manager.outbound.auth.JwtUserPrincipal;
import com.playground.analytics_manager.outbound.services.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final UserStatisticsService userStatisticsService;

    @GetMapping("/user")
    public ResponseEntity<UserSuccessRate> get(Authentication authentication) {
        var principal = (JwtUserPrincipal) authentication.getPrincipal();
        var userId = principal.getClaims().get("userId").toString();
        var userStatistics = userStatisticsService.getUserStatistics(userId);
        return ResponseEntity.ok(userStatistics);
    }
}
