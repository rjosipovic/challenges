package com.playground.notification_manager.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String MDC_REQUEST_ID_KEY = "requestId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        var requestId = MDC.get(MDC_REQUEST_ID_KEY);
        var startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        var duration = (startTime != null) ? System.currentTimeMillis() - startTime : -1;

        log.info("Request End. ID: {}, Status: {}, Duration: {}ms", requestId, response.getStatus(), duration);
        if (Objects.nonNull(ex)) {
            log.error("Request ID: {} resulted in an exception", requestId, ex);
        }
    }
}
