package com.studioengine.tutor.jobs;

import com.studioengine.tutor.dataaccess.entities.Appointment;
import com.studioengine.tutor.dataaccess.entities.NotificationLog;
import com.studioengine.tutor.dataaccess.enums.NotificationType;
import com.studioengine.tutor.dataaccess.repositories.AppointmentRepository;
import com.studioengine.tutor.dataaccess.repositories.NotificationLogRepository;
import com.studioengine.tutor.email.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyReminderHandlerTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private NotificationLogRepository notificationLogRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private DailyReminderHandler dailyReminderHandler;

    @Test
    void shouldSendReminder() {
        // given
        var appointmentId = UUID.randomUUID();
        var appointment = mock(Appointment.class);
        when(appointment.getId()).thenReturn(appointmentId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(notificationLogRepository.existsByAppointmentIdAndNotificationType(
                appointmentId, NotificationType.REMINDER)).thenReturn(false);

        // when
        dailyReminderHandler.handle(appointmentId);

        // then
        verify(appointmentRepository).findById(appointmentId);
        verify(emailService).sendReminder(appointment);
        var captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(captor.capture());
        assertThat(captor.getValue().getNotificationType()).isEqualTo(NotificationType.REMINDER);
    }

    @Test
    void shouldNotSendReminderWhenAppointmentNotFound() {
        // given
        var appointmentId = UUID.randomUUID();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> dailyReminderHandler.handle(appointmentId)).isInstanceOf(IllegalStateException.class);

        // then
        verify(appointmentRepository).findById(appointmentId);
        verify(emailService, never()).sendReminder(any());
        verify(notificationLogRepository, never()).save(any());
    }

    @Test
    void shouldSkipWhenReminderAlreadySent() {
        // given
        var appointmentId = UUID.randomUUID();
        var appointment = mock(Appointment.class);
        when(appointment.getId()).thenReturn(appointmentId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(notificationLogRepository.existsByAppointmentIdAndNotificationType(
                appointmentId, NotificationType.REMINDER)).thenReturn(true);

        // when
        dailyReminderHandler.handle(appointmentId);

        // then
        verify(appointmentRepository).findById(appointmentId);
        verify(emailService, never()).sendReminder(appointment);
        verify(notificationLogRepository, never()).save(any());
    }
}
