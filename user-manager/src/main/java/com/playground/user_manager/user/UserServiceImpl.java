package com.playground.user_manager.user;

import com.playground.user_manager.errors.ResourceAlreadyExistsException;
import com.playground.user_manager.errors.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserByAlias(String alias) {
        return userRepository
                .findByAlias(alias)
                .map(userEntity -> new User(userEntity.getId().toString(), userEntity.getAlias()))
                .orElseThrow(() -> new ResourceNotFoundException("User with alias " + alias + " not found", "user"));
    }


    @Override
    public User createUser(CreateUserDTO createUserDTO) {
        var alias = createUserDTO.getAlias();
        if (userRepository.findByAlias(alias).isPresent()) {
            throw new ResourceAlreadyExistsException("User with alias " + alias + " already exists", "user");
        }
        var userEntity = new UserEntity(UUID.randomUUID(), alias);
        var savedUserEntity = userRepository.save(userEntity);
        return new User(savedUserEntity.getId().toString(), savedUserEntity.getAlias());
    }
}
