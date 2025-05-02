package com.playground.challenge_manager.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_ATTR = "requestId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var requestId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ID_ATTR, requestId);
        log.info("Request: {}, Method: {}, URI: {}", requestId, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        var requestId = (String) request.getAttribute(REQUEST_ID_ATTR);
        log.info("Response : {}, Status Code: {}", requestId, response.getStatus());
    }
}
