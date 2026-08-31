package com.studioengine.tutor.scheduling;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CalendarSlot {

    CreatedSlot slot;
    AssociatedAppointment appointment;
}
