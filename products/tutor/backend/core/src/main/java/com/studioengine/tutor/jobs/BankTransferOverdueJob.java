package com.studioengine.tutor.jobs;

import com.studioengine.tutor.config.SchedulingProperties;
import com.studioengine.tutor.dataaccess.entities.Appointment;
import com.studioengine.tutor.dataaccess.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankTransferOverdueJob {

    private final SchedulingProperties schedulingProperties;
    private final AppointmentRepository appointmentRepository;
    private final OverduePaymentHandler overduePaymentHandler;

    @Scheduled(fixedRate = 900_000)
    @SchedulerLock(name = "bankTransferOverdueJob", lockAtLeastFor = "PT5M", lockAtMostFor = "PT14M")
    public void notifyOverduePayments() {

        var cutoff = OffsetDateTime.now().minus(schedulingProperties.getPaymentOverdueThreshold());
        var overdueAppointmentIds = appointmentRepository.findOverduePendingPayments(cutoff).stream()
                .map(Appointment::getId)
                .toList();

        log.info("BankTransferOverdueJob: found {} overdue pending payments", overdueAppointmentIds.size());

        overdueAppointmentIds.forEach(overduePaymentHandler::handle);

        log.info("BankTransferOverdueJob: completed");
    }
}
