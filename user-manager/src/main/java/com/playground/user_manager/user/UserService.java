package com.playground.user_manager.user;

import java.util.Optional;

public interface UserService {

    User getUserByAlias(String alias);
    User createUser(CreateUserDTO createUserDTO);
}
