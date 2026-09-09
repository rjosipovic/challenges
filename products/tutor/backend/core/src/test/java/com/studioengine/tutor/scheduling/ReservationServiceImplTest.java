package com.studioengine.tutor.scheduling;

import com.studioengine.tutor.config.BrandProperties;
import com.studioengine.tutor.config.SchedulingProperties;
import com.studioengine.tutor.dataaccess.entities.TimeSlot;
import com.studioengine.tutor.dataaccess.enums.TimeSlotState;
import com.studioengine.tutor.dataaccess.repositories.TimeSlotRepository;
import com.studioengine.tutor.errors.exceptions.PastSlotException;
import com.studioengine.tutor.errors.exceptions.ResourceNotFoundException;
import com.studioengine.tutor.errors.exceptions.SlotConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private TimeSlotStateMachine stateMachine;

    @Mock
    private SchedulingProperties schedulingProperties;

    @Mock
    private BrandProperties brandProperties;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void shouldReserveAvailableSlot() {
        // given
        var slot = createAvailableSlotInFuture();
        var slotId = slot.getId();
        var command = ReserveSlotCommand.builder().slotId(slotId).build();

        when(timeSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.of(slot));
        when(brandProperties.getTimezone()).thenReturn("Europe/Zagreb");
        when(schedulingProperties.getReservationTimeout()).thenReturn(Duration.ofMinutes(15));

        // when
        var result = reservationService.reserve(command);

        // then
        assertThat(result.getTimeSlotId()).isEqualTo(slotId);
        assertThat(result.getExpiresAt()).isNotNull();
        verify(stateMachine).transition(slot, TimeSlotState.RESERVED, "GUEST");
        verify(timeSlotRepository).save(slot);
    }

    @Test
    void shouldThrowWhenSlotNotFound() {
        // given
        var slotId = UUID.randomUUID();
        var command = ReserveSlotCommand.builder().slotId(slotId).build();

        when(timeSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> reservationService.reserve(command))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(stateMachine, never()).transition(any(), any(), any());
        verify(timeSlotRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenSlotNotAvailable() {
        // given
        var slot = createReservedSlotInFuture();
        var slotId = slot.getId();
        var command = ReserveSlotCommand.builder().slotId(slotId).build();

        when(timeSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.of(slot));

        // when
        assertThatThrownBy(() -> reservationService.reserve(command))
                .isInstanceOf(SlotConflictException.class);

        // then
        verify(stateMachine, never()).transition(any(), any(), any());
        verify(timeSlotRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenSlotInPast() {
        // given
        var slot = createAvailableSlotInPast();
        var slotId = slot.getId();
        var command = ReserveSlotCommand.builder().slotId(slotId).build();

        when(timeSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.of(slot));
        when(brandProperties.getTimezone()).thenReturn("Europe/Zagreb");

        // when
        assertThatThrownBy(() -> reservationService.reserve(command)).isInstanceOf(PastSlotException.class);

        // then
        verify(timeSlotRepository).findByIdForUpdate(slotId);
        verify(brandProperties).getTimezone();
        verify(stateMachine, never()).transition(any(), any(), any());
    }

    // --- Helpers ---
    private TimeSlot createAvailableSlotInFuture() {
        var date = LocalDate.now().plusDays(1);
        var time = LocalTime.of(10, 0);
        return createSlotInStateAndTime(TimeSlotState.AVAILABLE, LocalDateTime.of(date, time));
    }

    private TimeSlot createAvailableSlotInPast() {
        var date = LocalDate.now().minusDays(1);
        var time = LocalTime.of(10, 0);
        return createSlotInStateAndTime(TimeSlotState.AVAILABLE, LocalDateTime.of(date, time));
    }

    private TimeSlot createReservedSlotInFuture() {
        var date = LocalDate.now().plusDays(1);
        var time = LocalTime.of(10, 0);
        return createSlotInStateAndTime(TimeSlotState.RESERVED, LocalDateTime.of(date, time));
    }

    private TimeSlot createSlotInStateAndTime(TimeSlotState state, LocalDateTime dateTime) {
        var date = dateTime.toLocalDate();
        var time = dateTime.toLocalTime();
        var slot = TimeSlot.create(date, time);
        if (state != TimeSlotState.DRAFT) {
            slot.transitionTo(state);
        }
        try{
            var idField = TimeSlot.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(slot, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return slot;
    }
}
