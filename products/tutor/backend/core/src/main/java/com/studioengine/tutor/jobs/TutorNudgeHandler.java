package com.studioengine.tutor.jobs;

import com.studioengine.tutor.config.SchedulingProperties;
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

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TutorNudgeHandler {

    private final AppointmentRepository appointmentRepository;
    private final SchedulingProperties schedulingProperties;
    private final NotificationLogRepository notificationLogRepository;
    private final EmailService emailService;

    @Transactional
    public void handle(UUID appointmentId) {
        var appointment = findAppointment(appointmentId);
        var coolDown = OffsetDateTime.now().minus(schedulingProperties.getNudgeCooldown());

        var alreadyNudged = notificationLogRepository
                .existsByAppointmentIdAndNotificationTypeAndSentAtAfter(
                        appointment.getId(),
                        NotificationType.NUDGE,
                        coolDown
                );

        if (alreadyNudged) {
            log.debug("Skipping appointment {} - nudge already sent within cooldown", appointmentId);
            return;
        }

        emailService.sendNudge(appointment);
        var notificationLog = NotificationLog.create(appointment, NotificationType.NUDGE);
        notificationLogRepository.save(notificationLog);
        log.info("Sent nudge for appointment {}", appointmentId);
    }

    private Appointment findAppointment(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalStateException("Appointment not found: " + appointmentId));
    }
}