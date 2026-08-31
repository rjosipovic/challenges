package com.studioengine.tutor.api.mapper;

import com.studioengine.tutor.api.dto.calendar.CalendarSlotResponse;
import com.studioengine.tutor.api.dto.calendar.CreateSlotsRequest;
import com.studioengine.tutor.api.dto.calendar.SlotResponse;
import com.studioengine.tutor.api.dto.summary.AppointmentSummary;
import com.studioengine.tutor.scheduling.AssociatedAppointment;
import com.studioengine.tutor.scheduling.CalendarSlot;
import com.studioengine.tutor.scheduling.CreateSlotsCommand;
import com.studioengine.tutor.scheduling.CreatedSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CalendarMapper {

    @Mapping(source = "state", target = "state")
    SlotResponse toSlotResponse(CreatedSlot slot);

    List<SlotResponse> toSlotResponses(List<CreatedSlot> slots);

    CreateSlotsCommand.SlotDefinition toSlotDefinition(CreateSlotsRequest.SlotDefinition definition);

    List<CreateSlotsCommand.SlotDefinition> toSlotDefinitions(List<CreateSlotsRequest.SlotDefinition> definitions);

    AppointmentSummary toAppointmentSummary(AssociatedAppointment associatedAppointment);

    List<CalendarSlotResponse> toCalendarSlotResponseList(List<CalendarSlot> calendarSlots);
}
