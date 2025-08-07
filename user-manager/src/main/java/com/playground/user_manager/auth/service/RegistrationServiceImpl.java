package com.playground.user_manager.auth.service;

import com.playground.user_manager.auth.api.dto.RegisterUserDTO;
import com.playground.user_manager.auth.api.dto.RegisteredUser;
import com.playground.user_manager.errors.exceptions.UserAlreadyExistsException;
import com.playground.user_manager.user.dataaccess.UserEntity;
import com.playground.user_manager.user.dataaccess.UserRepository;
import com.playground.user_manager.user.messaging.producers.UserMessageProducer;
import com.playground.user_manager.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final UserMessageProducer userMessageProducer;

    @Override
    @Transactional
    public void register(RegisterUserDTO registerUserDTO) {
        var alias = registerUserDTO.getAlias();
        var email = registerUserDTO.getEmail();
        verifyUniqueness(alias, email);
        var birthdate = registerUserDTO.getBirthdate();
        var gender = registerUserDTO.getGender();
        var userEntity = UserEntity.builder()
                .alias(alias)
                .email(email)
                .birthdate(birthdate)
                .gender(gender)
                .build();
        var savedUserEntity = userRepository.save(userEntity);
        var user = new User(savedUserEntity.getId().toString(), savedUserEntity.getAlias());
        publishUserCreatedMessage(user);
    }

    private void verifyUniqueness(String alias, String email) {
        if (userRepository.findByAlias(alias).isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with alias: %s already exists", alias));
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with email: %s already exists", email));
        }
    }

    @Override
    public boolean isRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public Optional<RegisteredUser> getRegisteredUser(String email) {
        return userRepository.findByEmail(email)
                .map(userEntity -> new RegisteredUser(
                        userEntity.getId().toString(),
                        userEntity.getEmail(),
                        userEntity.getAlias())
                );
    }

    private void publishUserCreatedMessage(User user) {
        userMessageProducer.sendUserCreatedMessage(user);
    }
}
