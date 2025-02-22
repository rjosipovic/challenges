package com.playground.user_manager.errors;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException implements ErrorCode {

    private static final String ERROR_CODE = "NE-001";

    private final String resourceType;
    private final String errorCode;

    public ResourceNotFoundException(String message, String resourceType) {
        super(message);
        this.resourceType = resourceType;
        this.errorCode = ERROR_CODE;
    }
}
