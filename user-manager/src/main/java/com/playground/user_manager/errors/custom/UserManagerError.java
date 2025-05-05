package com.playground.user_manager.errors.custom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class UserManagerError {

    private final String apiVersion;
    private final ErrorBlock error;

    public UserManagerError(
            final String apiVersion,
            final String code,
            final String message,
            final String domain,
            final String reason,
            final String errorMessage,
            final String errorReportUri
    ) {
        this.apiVersion = apiVersion;
        this.error = new ErrorBlock(code, message, domain, reason, errorMessage, errorReportUri);
    }

    public static UserManagerError fromDefaultAttributeMap(final String apiVersion,
                                                           final Map<String, Object> defaultErrorAttributes,
                                                           final String sendReportBaseUri
    ) {
        // original attribute values are documented in org.springframework.boot.web.servlet.error.DefaultErrorAttributes
        return new UserManagerError(
                apiVersion,
                ((Integer) defaultErrorAttributes.get("status")).toString(),
                (String) defaultErrorAttributes.getOrDefault("message", "no message available"),
                (String) defaultErrorAttributes.getOrDefault("path", "no domain available"),
                (String) defaultErrorAttributes.getOrDefault("error", "no reason available"),
                (String) defaultErrorAttributes.get("message"),
                sendReportBaseUri
        );
    }

    // utility method to return a map of serialized root attributes
    public Map<String, Object> toAttributeMap() {
        return Map.of(
                "apiVersion", apiVersion,
                "error", error
        );
    }

    @Getter
    private static final class ErrorBlock {

        @JsonIgnore
        private final String code;
        private final String message;
        private final List<Error> errors;

        public ErrorBlock(final String code, final String message, final String domain,
                          final String reason, final String errorMessage, final String errorReportUri) {
            this.code = code;
            this.message = message;
            this.errors = List.of(
                    new Error(domain, reason, errorMessage, errorReportUri)
            );
        }

        private ErrorBlock(final String code, final String message, final List<Error> errors) {
            this.code = code;
            this.message = message;
            this.errors = errors;
        }

        public static ErrorBlock copyWithMessage(final ErrorBlock s, final String message) {
            return new ErrorBlock(s.code, message, s.errors);
        }

    }

    @Getter
    private static final class Error {
        private final String domain;
        private final String reason;
        private final String message;
        private final String sendReport;

        public Error(final String domain, final String reason, final String message, final String sendReport) {
            this.domain = domain;
            this.reason = reason;
            this.message = message;
            this.sendReport = sendReport;
        }
    }
}