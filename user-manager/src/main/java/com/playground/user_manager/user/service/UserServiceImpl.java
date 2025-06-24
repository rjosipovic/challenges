package com.playground.user_manager.user.service;

import com.playground.user_manager.errors.exceptions.ResourceNotFoundException;
import com.playground.user_manager.user.dataaccess.UserRepository;
import com.playground.user_manager.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
}
