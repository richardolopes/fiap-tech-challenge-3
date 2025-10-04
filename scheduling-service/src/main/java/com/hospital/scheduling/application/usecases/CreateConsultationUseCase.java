package com.hospital.scheduling.application.usecases;

import com.hospital.scheduling.application.dtos.ConsultationResponse;
import com.hospital.scheduling.application.dtos.CreateConsultationRequest;
import com.hospital.scheduling.domain.repositories.ConsultationRepository;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.scheduling.infrastructure.events.EventPublisher;
import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.enums.UserType;
import com.hospital.shared.domain.events.ConsultationCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateConsultationUseCase {

    private final ConsultationRepository consultationRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public CreateConsultationUseCase(ConsultationRepository consultationRepository,
                                     UserRepository userRepository,
                                     EventPublisher eventPublisher) {
        this.consultationRepository = consultationRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ConsultationResponse execute(CreateConsultationRequest request) {
        User patient = userRepository.findById(request.patientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        if (patient.getUserType() != UserType.PACIENTE) {
            throw new IllegalArgumentException("Specified user is not a patient");
        }

        User doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        if (doctor.getUserType() != UserType.MEDICO) {
            throw new IllegalArgumentException("Specified user is not a doctor");
        }

        if (consultationRepository.existsByDoctorIdAndScheduledDateTime(
                request.doctorId(), request.scheduledDateTime())) {
            throw new IllegalArgumentException("Doctor already has a consultation scheduled at this time");
        }

        Consultation consultation = new Consultation(
                request.patientId(),
                request.doctorId(),
                request.scheduledDateTime());

        Consultation savedConsultation = consultationRepository.save(consultation);

        ConsultationCreatedEvent event = new ConsultationCreatedEvent(
                savedConsultation.getId(),
                patient.getId(),
                doctor.getId(),
                savedConsultation.getScheduledDateTime(),
                patient.getEmail(),
                patient.getName(),
                doctor.getName());
        eventPublisher.publishEvent(event);

        ConsultationResponse response = ConsultationResponse.fromEntity(savedConsultation);
        return response.withUserInfo(patient.getName(), patient.getEmail(), doctor.getName());
    }
}
