package com.studioengine.tutor.api.dto.storefront;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.UUID;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = RescheduleBookingRequest.RescheduleBookingRequestBuilder.class)
public class RescheduleBookingRequest {

    UUID timeSlotId;
    String rescheduleToken;

    @JsonPOJOBuilder(withPrefix = "")
    public static class RescheduleBookingRequestBuilder {}
}
