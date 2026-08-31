package com.studioengine.tutor.api.dto.calendar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.studioengine.tutor.api.dto.summary.AppointmentSummary;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = CalendarSlotResponse.CalendarSlotResponseBuilder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarSlotResponse {

    SlotResponse slot;
    AppointmentSummary appointment;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CalendarSlotResponseBuilder {}
}
