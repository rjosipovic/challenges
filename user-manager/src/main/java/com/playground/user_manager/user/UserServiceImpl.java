package com.playground.user_manager.user;

import com.playground.user_manager.errors.exceptions.ResourceAlreadyExistsException;
import com.playground.user_manager.errors.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return new User(savedUserEntity.getId().toString(), savedUserEntity.getAlias());
    }
}
