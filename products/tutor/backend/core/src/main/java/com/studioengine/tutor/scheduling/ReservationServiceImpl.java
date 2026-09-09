package com.studioengine.tutor.scheduling;

import com.studioengine.tutor.config.BrandProperties;
import com.studioengine.tutor.config.SchedulingProperties;
import com.studioengine.tutor.dataaccess.entities.TimeSlot;
import com.studioengine.tutor.dataaccess.enums.TimeSlotState;
import com.studioengine.tutor.dataaccess.repositories.TimeSlotRepository;
import com.studioengine.tutor.errors.exceptions.PastSlotException;
import com.studioengine.tutor.errors.exceptions.ResourceNotFoundException;
import com.studioengine.tutor.errors.exceptions.SlotConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotStateMachine stateMachine;
    private final SchedulingProperties schedulingProperties;
    private final BrandProperties brandProperties;

    @Override
    @Transactional
    public Reservation reserve(ReserveSlotCommand command) {
        var slotId = command.getSlotId();
        var slot = find(slotId);
        verifyReservationPossible(slot);
        verifyNotInPast(slot);
        stateMachine.transition(slot, TimeSlotState.RESERVED, "GUEST");
        timeSlotRepository.save(slot);

        var expiresAt = OffsetDateTime.now().plus(schedulingProperties.getReservationTimeout());

        return Reservation.builder()
                .timeSlotId(slotId)
                .expiresAt(expiresAt)
                .build();
    }

    private TimeSlot find(UUID slotId) {
        return timeSlotRepository.findByIdForUpdate(slotId).orElseThrow(() -> new ResourceNotFoundException("TimeSlot not found: " + slotId));
    }

    private void verifyReservationPossible(TimeSlot slot) {
        var id = slot.getId();
        var state = slot.getState();
        if (state != TimeSlotState.AVAILABLE) {
            throw new SlotConflictException("Slot %s is in state %s, expected AVAILABLE".formatted(id, state));
        }
    }

    private void verifyNotInPast(TimeSlot slot) {
        var timezone = ZoneId.of(brandProperties.getTimezone());
        var now = LocalDateTime.now(timezone);
        var date = slot.getSlotDate();
        var startTime = slot.getStartTime();
        var slotStart = date.atTime(startTime);
        if (!slotStart.isAfter(now)) {   // start <= now → reject
            throw new PastSlotException("Cannot book a slot in the past: %s %s".formatted(date, startTime));
        }
    }
}
