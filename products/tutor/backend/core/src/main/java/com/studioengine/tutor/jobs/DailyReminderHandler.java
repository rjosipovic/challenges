package com.studioengine.tutor.jobs;

import com.studioengine.tutor.dataaccess.entities.Appointment;
import com.studioengine.tutor.dataaccess.entities.NotificationLog;
import com.studioengine.tutor.dataaccess.enums.NotificationType;
import com.studioengine.tutor.dataaccess.repositories.AppointmentRepository;
import com.studioengine.tutor.dataaccess.repositories.NotificationLogRepository;
import com.studioengine.tutor.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyReminderHandler {

    private final AppointmentRepository appointmentRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final EmailService emailService;

    @Transactional
    public void handle(UUID appointmentId) {
        var appointment = findAppointment(appointmentId);
        var alreadySent = notificationLogRepository
                .existsByAppointmentIdAndNotificationType(
                        appointment.getId(),
                        NotificationType.REMINDER);

        if (alreadySent) {
            log.debug("Skipping appointment {} - reminder already sent", appointmentId);
            return;
        }

        emailService.sendReminder(appointment);
        var notificationLog = NotificationLog.create(appointment, NotificationType.REMINDER);
        notificationLogRepository.save(notificationLog);
    }

    private Appointment findAppointment(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalStateException("Appointment not found: " + appointmentId));
    }
}
