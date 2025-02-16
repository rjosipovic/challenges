package com.playground.user_manager.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class CreateUserDTO {

    @NotBlank
    String alias;
}
