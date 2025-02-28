package com.playground.user_manager.user;

public interface UserService {

    User getUserByAlias(String alias);
    User createUser(CreateUserDTO createUserDTO);
}
