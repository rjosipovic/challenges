package com.playground.user_manager.errors;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebErrorConfiguration {

    private final String currentApiVersion = "1.0";
    private final String sendReportUri = "http://localhost:8080/api/v1/errors";

    @Bean
    public ErrorAttributes errorAttributes() {
        return new UserManagerErrorAttributes(currentApiVersion, sendReportUri);
    }
}
