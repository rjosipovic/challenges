package com.playground.user_manager.errors.custom;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

public class UserManagerErrorAttributes extends DefaultErrorAttributes {

    private final String currentApiVersion;
    private final String sendReportUri;

    public UserManagerErrorAttributes(final String currentApiVersion, final String sendReportUri) {
        this.currentApiVersion = currentApiVersion;
        this.sendReportUri = sendReportUri;
    }

    @Override
    public Map<String, Object> getErrorAttributes(final WebRequest webRequest, final ErrorAttributeOptions options) {
        final Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, options);
        final UserManagerError userManagerError = UserManagerError.fromDefaultAttributeMap(currentApiVersion, defaultErrorAttributes, sendReportUri);
        return userManagerError.toAttributeMap();
    }
}
