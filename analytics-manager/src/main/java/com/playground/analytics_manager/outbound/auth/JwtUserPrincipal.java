package com.playground.analytics_manager.outbound.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class JwtUserPrincipal implements Serializable {

    private final String email;
    private final Map<String, Object> claims;
}
