package com.hospital.scheduling.application.usecases;

import com.hospital.scheduling.application.dtos.ConsultationResponse;
import com.hospital.scheduling.application.dtos.UpdateConsultationRequest;
import com.hospital.scheduling.domain.repositories.ConsultationRepository;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.scheduling.infrastructure.events.EventPublisher;
import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.events.ConsultationRescheduledEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdateConsultationUseCase {

    private final ConsultationRepository consultationRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public UpdateConsultationUseCase(ConsultationRepository consultationRepository,
                                     UserRepository userRepository,
                                     EventPublisher eventPublisher) {
        this.consultationRepository = consultationRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ConsultationResponse execute(Long consultationId, UpdateConsultationRequest request) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

        boolean dateTimeChanged = !consultation.getScheduledDateTime().equals(request.scheduledDateTime());

        if (dateTimeChanged) {
            Long doctorId = request.doctorId() != null ? request.doctorId() : consultation.getDoctorId();

            if (consultationRepository.existsByDoctorIdAndScheduledDateTime(doctorId, request.scheduledDateTime())) {
                throw new IllegalArgumentException("Doctor already has a consultation scheduled at this time");
            }
        }

        Long patientId = request.patientId() != null ? request.patientId() : consultation.getPatientId();
        Long doctorId = request.doctorId() != null ? request.doctorId() : consultation.getDoctorId();

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        LocalDateTime oldDateTime = consultation.getScheduledDateTime();

        if (dateTimeChanged) {
            consultation.reschedule(request.scheduledDateTime());
        }

        Consultation savedConsultation = consultationRepository.save(consultation);

        if (dateTimeChanged) {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    savedConsultation.getId(),
                    patient.getId(),
                    oldDateTime,
                    savedConsultation.getScheduledDateTime(),
                    patient.getEmail(),
                    patient.getName(),
                    doctor.getName()
            );
            eventPublisher.publishEvent(event);
        }

        ConsultationResponse response = ConsultationResponse.fromEntity(savedConsultation);
        return response.withUserInfo(patient.getName(), patient.getEmail(), doctor.getName());
    }
}
