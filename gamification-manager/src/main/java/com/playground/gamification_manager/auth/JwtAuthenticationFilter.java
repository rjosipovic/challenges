package com.playground.gamification_manager.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String secret;

    public JwtAuthenticationFilter(String secret) {
        this.secret = secret;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            var token = header.substring(7);
            try {
                var signedJwt = SignedJWT.parse(token);
                var valid = signedJwt.verify(new MACVerifier(secret));
                if (valid && signedJwt.getJWTClaimsSet().getExpirationTime().after(new Date())) {
                    var email = signedJwt.getJWTClaimsSet().getSubject();
                    var authentication = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    var securityContext = SecurityContextHolder.getContext();
                    securityContext.setAuthentication(authentication);
                }
            } catch (ParseException | JOSEException e) {
                throw new ServletException(e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
