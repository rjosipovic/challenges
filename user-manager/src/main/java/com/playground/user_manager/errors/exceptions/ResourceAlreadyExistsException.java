package com.playground.user_manager.errors.exceptions;

import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends IllegalArgumentException implements ErrorCode {

    private static final String ERROR_CODE = "NE-002";

    private final String resourceType;
    private final String errorCode;

    public ResourceAlreadyExistsException(String message, String resourceType) {
        super(message);
        this.resourceType = resourceType;
        this.errorCode = ERROR_CODE;

    }
}
