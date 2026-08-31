package com.studioengine.tutor.jobs;

import com.studioengine.tutor.config.SchedulingProperties;
import com.studioengine.tutor.dataaccess.entities.TimeSlot;
import com.studioengine.tutor.dataaccess.repositories.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationTimeoutJob {

    private final TimeSlotRepository timeSlotRepository;
    private final SchedulingProperties schedulingProperties;
    private final ExpiredTimeSlotHandler expiredTimeSlotHandler;

    @Scheduled(fixedRate = 60_000)
    @SchedulerLock(name = "reservationsTimeoutJob", lockAtLeastFor = "PT30S", lockAtMostFor = "PT5M")
    public void releaseExpiredReservations() {
        var cutoff = OffsetDateTime.now().minus(schedulingProperties.getReservationTimeout());
        var expiredSlotIds = timeSlotRepository.findExpiredReservations(cutoff).stream()
                .map(TimeSlot::getId)
                .toList();

        log.info("ReservationTimeoutJob: found {} expired reservations", expiredSlotIds.size());

        expiredSlotIds.forEach(expiredTimeSlotHandler::handle);

        log.info("ReservationTimeoutJob: completed");
    }
}
