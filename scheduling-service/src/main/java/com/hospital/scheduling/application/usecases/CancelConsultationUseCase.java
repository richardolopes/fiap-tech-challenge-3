package com.hospital.scheduling.application.usecases;

import com.hospital.scheduling.domain.repositories.ConsultationRepository;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.scheduling.infrastructure.events.EventPublisher;
import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.events.ConsultationCancelledEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelConsultationUseCase {

    private final ConsultationRepository consultationRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public CancelConsultationUseCase(ConsultationRepository consultationRepository,
                                     UserRepository userRepository,
                                     EventPublisher eventPublisher) {
        this.consultationRepository = consultationRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(Long consultationId, String reason) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

        User patient = userRepository.findById(consultation.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        consultation.cancel(reason != null ? reason : "Consulta cancelada");

        consultationRepository.save(consultation);

        ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                consultation.getId(),
                patient.getId(),
                reason != null ? reason : "Consulta cancelada",
                patient.getEmail(),
                patient.getName()
        );
        eventPublisher.publishEvent(event);
    }
}
