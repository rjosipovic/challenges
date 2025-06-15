package com.playground.user_manager.auth.service;

import com.playground.user_manager.auth.api.dto.RegisterUserDTO;
import com.playground.user_manager.auth.api.dto.RegisteredUser;

import java.util.Optional;

public interface RegistrationService {

    void register(RegisterUserDTO registerUserDTO);
    boolean isRegistered(String email);
    Optional<RegisteredUser> getRegisteredUser(String email);
}
