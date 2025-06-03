package com.playground.user_manager.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class AuthCodeGenerationRequest {

    @NotNull @Email
    String email;
}
