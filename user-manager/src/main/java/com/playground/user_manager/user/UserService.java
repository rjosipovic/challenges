package com.playground.user_manager.user;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User getUserByAlias(String alias);
    User createUser(CreateUserDTO createUserDTO);
}
