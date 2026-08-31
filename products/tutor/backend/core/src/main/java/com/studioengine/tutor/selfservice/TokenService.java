package com.studioengine.tutor.selfservice;

import com.studioengine.tutor.config.InstanceProperties;
import com.studioengine.tutor.dataaccess.entities.Appointment;
import com.studioengine.tutor.dataaccess.entities.CancellationToken;
import com.studioengine.tutor.dataaccess.enums.TokenType;
import com.studioengine.tutor.dataaccess.repositories.CancellationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final CancellationTokenRepository cancellationTokenRepository;
    private final InstanceProperties instanceProperties;

    public String generateManageLink(Appointment appointment) {
        var slot = appointment.getTimeSlot();
        var expiresAt = slot.getSlotDate()
                .atTime(slot.getEndTime())
                .atOffset(OffsetDateTime.now().getOffset());

        var token = CancellationToken.create(appointment, TokenType.APPOINTMENT_MANAGEMENT, expiresAt);
        cancellationTokenRepository.save(token);
        return "%s/manage.html?token=%s".formatted(instanceProperties.getBaseUrl(), token.getToken());
    }
}
