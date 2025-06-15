package com.playground.user_manager.auth.service.code_generation.handlers;

import com.playground.user_manager.auth.service.RegistrationService;
import com.playground.user_manager.auth.service.code_generation.CodeGenerationContext;
import com.playground.user_manager.auth.service.code_generation.CodeGenerationHandler;
import com.playground.user_manager.errors.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRegisteredHandler implements CodeGenerationHandler {

    private final RegistrationService registrationService;

    @Override
    public void handle(CodeGenerationContext ctx) {
        log.info("About to check if user is registered");
        var email = ctx.getEmail();
        if (notRegistered(email)) {
            log.info("User {} is not registered", email);
            throw new ResourceNotFoundException("User with email " + email + " is not registered", "user");
        }
    }

    private boolean notRegistered(String email) {
        return !registrationService.isRegistered(email);
    }
}
