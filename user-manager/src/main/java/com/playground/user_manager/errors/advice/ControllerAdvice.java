package com.playground.user_manager.errors.advice;

import com.nimbusds.jose.JOSEException;
import com.playground.user_manager.errors.custom.UserManagerError;
import com.playground.user_manager.errors.exceptions.ResourceAlreadyExistsException;
import com.playground.user_manager.errors.exceptions.ResourceNotFoundException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        response.getHeaders().add("apiVersion", "1.0");
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<UserManagerError> handleValidation(MethodArgumentNotValidException ex) {
        var apiVersion = "1.0";
        var message = "Validation failed";
        var errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        var error = new UserManagerError(
                apiVersion,
                null,
                message,
                null,
                "invalid",
                errorMessage,
                ""
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JOSEException.class)
    public ResponseEntity<UserManagerError> handleJwtException(Exception ex) {
        var apiVersion = "1.0";
        var message = "Token generation failed";
        var errorMessage = ex.getMessage();
        var errorReportUri = "";
        var error = new UserManagerError(
                apiVersion,
                null,
                message,
                null,
                null,
                errorMessage,
                errorReportUri
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<UserManagerError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        var apiVersion = "1.0";
        var code = ex.getErrorCode();
        var message = "Resource not found";
        var domain = ex.getResourceType();
        var reason = "not found";
        var errorMessage = ex.getMessage();
        var errorReportUri = "";
        var error = new UserManagerError(
                apiVersion,
                code,
                message,
                domain,
                reason,
                errorMessage,
                errorReportUri
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<UserManagerError> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        var apiVersion = "1.0";
        var code = ex.getErrorCode();
        var message = "Resource already exists";
        var domain = ex.getResourceType();
        var reason = "exists";
        var errorMessage = ex.getMessage();
        var errorReportUri = "";
        var error = new UserManagerError(
                apiVersion,
                code,
                message,
                domain,
                reason,
                errorMessage,
                errorReportUri
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
