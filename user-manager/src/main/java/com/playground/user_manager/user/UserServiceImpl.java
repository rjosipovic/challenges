package com.playground.user_manager.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserByAlias(String alias) {
        var optionalUserEntity = userRepository.findByAlias(alias);
        if (optionalUserEntity.isPresent()) {
            var userEntity = optionalUserEntity.get();
            return Optional.of(new User(userEntity.getId().toString(), userEntity.getAlias()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public User createUser(CreateUserDTO createUserDTO) {
        var alias = createUserDTO.getAlias();
        if (userRepository.findByAlias(alias).isPresent()) {
            throw new IllegalArgumentException("User with alias " + alias + " already exists");
        }
        var userEntity = new UserEntity(UUID.randomUUID(), alias);
        var savedUserEntity = userRepository.save(userEntity);
        return new User(savedUserEntity.getId().toString(), savedUserEntity.getAlias());
    }
}
