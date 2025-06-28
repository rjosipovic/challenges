package com.playground.analytics_manager.outbound.api;

import com.playground.analytics_manager.outbound.auth.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @GetMapping("/user")
    public ResponseEntity<Void> get(Authentication authentication) {
        var principal = (JwtUserPrincipal) authentication.getPrincipal();
        var userId = principal.getClaims().get("userId").toString();
        return ResponseEntity.ok().build();
    }
}
