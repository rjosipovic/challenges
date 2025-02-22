package com.playground.user_manager.errors;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(ErrorController.ERROR_PATH)
public class ErrorController extends AbstractErrorController {

    static final String ERROR_PATH = "/error";

    public ErrorController(final ErrorAttributes errorAttributes) {
        super(errorAttributes, Collections.emptyList());
    }

    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        var body = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        var status = this.getStatus(request);
        return ResponseEntity.status(status).body(body);
    }
}
