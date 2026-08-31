package com.studioengine.tutor.selfservice;

import java.util.UUID;

public interface SelfServiceManager {

    AppointmentDetails validateToken(String token);

    AppointmentCancellation confirmCancellation(String token);

    RescheduleInitiation confirmReschedule(String token);

    void completeReschedule(UUID slotId, String rescheduleToken);
}
