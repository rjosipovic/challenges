package com.playground.user_manager.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testSaveUser() {
        //given
        var alias = "test-user";
        var uuid = UUID.randomUUID();
        var createUserRequest = new CreateUserDTO(alias);
        when(userRepository.findByAlias(alias)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity(uuid, alias));
        //when
        var savedUser = userService.createUser(createUserRequest);
        //then
        assertAll(
                () -> assertNotNull(savedUser),
                () -> assertEquals(uuid.toString(), savedUser.getId()),
                () -> assertEquals(alias, savedUser.getAlias())
        );
    }

}