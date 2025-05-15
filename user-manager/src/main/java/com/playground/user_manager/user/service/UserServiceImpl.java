package com.playground.user_manager.user.service;

import com.playground.user_manager.errors.exceptions.ResourceAlreadyExistsException;
import com.playground.user_manager.errors.exceptions.ResourceNotFoundException;
import com.playground.user_manager.user.api.dto.CreateUserDTO;
import com.playground.user_manager.user.dataaccess.UserEntity;
import com.playground.user_manager.user.dataaccess.UserRepository;
import com.playground.user_manager.user.model.User;
import com.playground.user_manager.user.messaging.producers.UserMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMessageProducer userMessageProducer;

    public List<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(userEntity -> new User(userEntity.getId().toString(), userEntity.getAlias()))
                .toList();
    };

    public List<User> getUsersByIds(List<String> ids) {
        var uuids = ids.stream().map(UUID::fromString).toList();
        return userRepository.findByIdIn(uuids)
                .stream()
                .map(userEntity -> new User(userEntity.getId().toString(), userEntity.getAlias()))
                .toList();
    };

    @Override
    public User getUserByAlias(String alias) {
        return userRepository
                .findByAlias(alias)
                .map(userEntity -> new User(userEntity.getId().toString(), userEntity.getAlias()))
                .orElseThrow(() -> new ResourceNotFoundException("User with alias " + alias + " not found", "user"));
    }


    @Override
    @Transactional
    public User createUser(CreateUserDTO createUserDTO) {
        var alias = createUserDTO.getAlias();
        if (userRepository.findByAlias(alias).isPresent()) {
            throw new ResourceAlreadyExistsException("User with alias " + alias + " already exists", "user");
        }
        var email = createUserDTO.getEmail();
        var birthdate = createUserDTO.getBirthdate();
        var gender = createUserDTO.getGender();
        var userEntity = UserEntity.builder()
                .alias(alias)
                .email(email)
                .birthdate(birthdate)
                .gender(gender)
                .build();
        var savedUserEntity = userRepository.save(userEntity);
        var user = new User(savedUserEntity.getId().toString(), savedUserEntity.getAlias());
        publishUserCreatedMessage(user);
        return user;
    }

    private void publishUserCreatedMessage(User user) {
        userMessageProducer.sendUserCreatedMessage(user);
    }
}
