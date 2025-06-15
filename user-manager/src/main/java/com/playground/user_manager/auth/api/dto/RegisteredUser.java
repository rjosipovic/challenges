package com.playground.user_manager.auth.api.dto;

import lombok.Value;

@Value
public class RegisteredUser {

    String userId;
    String email;
    String alias;
}
