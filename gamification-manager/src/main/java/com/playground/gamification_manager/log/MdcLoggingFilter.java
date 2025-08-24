package com.playground.gamification_manager.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final String MDC_REQUEST_ID_KEY = "requestId";
    private static final String REQUEST_PREFIX = "Request: ";
    private static final String RESPONSE_PREFIX = "Response: ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // We need to wrap the request and response to cache their content
        var requestWrapper = new ContentCachingRequestWrapper(request);
        var responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            final String requestId = UUID.randomUUID().toString();
            MDC.put(MDC_REQUEST_ID_KEY, requestId);

            // CRITICAL: The filter chain must be processed first.
            // This allows the framework to read the request body and populate the cache.
            filterChain.doFilter(requestWrapper, responseWrapper);

            // Now that the chain has been processed, the cached content is available for logging.
            logRequestDetails(requestWrapper);
            logResponseDetails(responseWrapper);

        } finally {
            // This is critical to ensure the response is sent to the client
            responseWrapper.copyBodyToResponse();
            MDC.remove(MDC_REQUEST_ID_KEY);
        }
    }

    private void logRequestDetails(ContentCachingRequestWrapper request) {
        var msg = new StringBuilder();
        msg.append(REQUEST_PREFIX);
        msg.append("method=").append(request.getMethod()).append("; ");
        msg.append("uri=").append(request.getRequestURI());

        var queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

        var requestBody = getBody(request.getContentAsByteArray(), request.getCharacterEncoding());
        if (StringUtils.hasText(requestBody)) {
            msg.append("; body=").append(requestBody);
        }

        logger.info(msg.toString());
    }

    private void logResponseDetails(ContentCachingResponseWrapper response) {
        int status = response.getStatus();
        var responseBody = getBody(response.getContentAsByteArray(), response.getCharacterEncoding());
        logger.info(RESPONSE_PREFIX + "status=" + status + "; body=" + responseBody);
    }

    private String getBody(byte[] contentAsByteArray, String characterEncoding) {
        if (contentAsByteArray == null || contentAsByteArray.length == 0) {
            return "";
        }
        try {
            return new String(contentAsByteArray, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to read request/response body", e);
            return "[[FAILED TO READ BODY]]";
        }
    }
}