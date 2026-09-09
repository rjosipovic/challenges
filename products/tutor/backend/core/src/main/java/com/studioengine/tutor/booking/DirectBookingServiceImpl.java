package com.studioengine.tutor.booking;

import com.studioengine.tutor.config.BrandProperties;
import com.studioengine.tutor.dataaccess.entities.Appointment;
import com.studioengine.tutor.dataaccess.entities.ServiceCategory;
import com.studioengine.tutor.dataaccess.entities.Student;
import com.studioengine.tutor.dataaccess.entities.TimeSlot;
import com.studioengine.tutor.dataaccess.enums.AppointmentOrigin;
import com.studioengine.tutor.dataaccess.enums.AppointmentState;
import com.studioengine.tutor.dataaccess.enums.TimeSlotState;
import com.studioengine.tutor.dataaccess.repositories.AppointmentRepository;
import com.studioengine.tutor.dataaccess.repositories.ServiceCategoryRepository;
import com.studioengine.tutor.dataaccess.repositories.StudentRepository;
import com.studioengine.tutor.dataaccess.repositories.TimeSlotRepository;
import com.studioengine.tutor.errors.exceptions.InvalidStateTransitionException;
import com.studioengine.tutor.errors.exceptions.PastSlotException;
import com.studioengine.tutor.errors.exceptions.ResourceNotFoundException;
import com.studioengine.tutor.scheduling.TimeSlotStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DirectBookingServiceImpl implements DirectBookingService {

    private static final Set<TimeSlotState> ALLOWED_SLOT_STATES = Set.of(TimeSlotState.DRAFT, TimeSlotState.AVAILABLE);

    private final TimeSlotRepository timeSlotRepository;
    private final StudentRepository studentRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotStateMachine timeSlotStateMachine;
    private final DirectBookingServiceMapper directBookingServiceMapper;
    private final BrandProperties brandProperties;

    @Override
    @Transactional
    public DirectBooking book(DirectBookingCommand command) {
        var slotId = command.getTimeSlotId();
        var studentId = command.getStudentId();
        var serviceCategoryId = command.getServiceCategoryId();

        var slot = findTimeSlot(slotId);
        verifyTimeSlotInAllowedState(slot);
        verifyNotInPast(slot);
        var student = findStudent(studentId);
        var category = findServiceCategory(serviceCategoryId);

        var appointment = Appointment.create(
                slot,
                category,
                student,
                AppointmentState.PRE_BOOKED,
                category.getPrice(),
                category.getPrice(),
                AppointmentOrigin.DASHBOARD_DIRECT,
                null
        );
        appointmentRepository.save(appointment);

        timeSlotStateMachine.transition(slot, TimeSlotState.PRE_BOOKED, "TUTOR");
        timeSlotRepository.save(slot);

        return directBookingServiceMapper.toDirectBooking(appointment, slot, student, category);
    }

    private TimeSlot findTimeSlot(UUID id) {
        return timeSlotRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("TimeSlot not found: " + id));
    }

    private void verifyTimeSlotInAllowedState(TimeSlot slot) {
        if (!ALLOWED_SLOT_STATES.contains(slot.getState())) {
            throw new InvalidStateTransitionException("Slot %s is in state %s, expected %s".formatted(slot.getId(), slot.getState(), ALLOWED_SLOT_STATES));
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

    private Student findStudent(UUID id) {
        return studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    private ServiceCategory findServiceCategory(UUID id) {
        return serviceCategoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ServiceCategory not found: " + id));
    }
}
