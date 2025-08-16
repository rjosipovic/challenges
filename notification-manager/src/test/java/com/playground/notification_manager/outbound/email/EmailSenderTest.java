package com.playground.notification_manager.outbound.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailSender emailSender;

    @Test
    void sendEmail_sendsCorrectMail() {
        // given
        var defaultFrom = "no-reply@example.com";
        var to = "recipient@example.com";
        var subject = "Test Subject";
        var body = "Test Body";
        var emailMessage = new EmailMessage(defaultFrom, to, subject, body);

        var captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        //when
        emailSender.sendEmail(emailMessage);

        //then
        verify(javaMailSender, times(1)).send(captor.capture());
        var sentMessage = captor.getValue();

        assertAll(
                () -> assertThat(sentMessage.getFrom()).isEqualTo(defaultFrom),
                () -> assertThat(sentMessage.getTo()).containsExactly(to),
                () -> assertThat(sentMessage.getSubject()).isEqualTo(subject),
                () -> assertThat(sentMessage.getText()).isEqualTo(body)
        );
    }

    @Test
    void sendEmail_throwsException() {
        // given
        var emailMessage = new EmailMessage(null, null, null, null);

        //when
        var exception = assertThrows(IllegalArgumentException.class, () -> emailSender.sendEmail(emailMessage));

        //then
        assertThat(exception.getMessage()).isEqualTo("EmailNotification cannot be null");
    }
}