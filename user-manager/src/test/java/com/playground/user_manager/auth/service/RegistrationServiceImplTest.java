package com.playground.user_manager.auth.service;

import com.playground.user_manager.auth.api.dto.RegisterUserDTO;
import com.playground.user_manager.user.dataaccess.UserEntity;
import com.playground.user_manager.user.dataaccess.UserRepository;
import com.playground.user_manager.user.messaging.producers.UserMessageProducer;
import com.playground.user_manager.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMessageProducer userMessageProducer;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    void testRegisterUser() {
        //given
        var alias = "test-user";
        var email = "someemail@gmail.com";
        var uuid = UUID.randomUUID();
        var registerUserRequest = new RegisterUserDTO(alias, email, null, null);
        when(userRepository.findByAlias(alias)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(UserEntity.builder().alias(alias).id(uuid).email(email).build());
        //when
        registrationService.register(registerUserRequest);
        //then
        var userEntityCapture = ArgumentCaptor.forClass(UserEntity.class);
        var userCapture = ArgumentCaptor.forClass(User.class);

        verify(userRepository).findByAlias(alias);
        verify(userRepository).save(userEntityCapture.capture());
        verify(userMessageProducer).sendUserCreatedMessage(userCapture.capture());

        assertAll(
                () -> assertEquals(alias, userEntityCapture.getValue().getAlias()),
                () -> assertEquals(email, userEntityCapture.getValue().getEmail()),
                () -> assertEquals(alias, userCapture.getValue().getAlias()),
                () -> assertEquals(uuid.toString(), userCapture.getValue().getId())
        );
    }
}