package com.hospital.scheduling.domain.repositories;

import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.enums.ConsultationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepository {

    Consultation save(Consultation consultation);

    Optional<Consultation> findById(Long id);

    List<Consultation> findAll();

    List<Consultation> findByPatientId(Long patientId);

    List<Consultation> findFutureConsultationsByPatientId(Long patientId);

    List<Consultation> findByPatientIdAndStatus(Long patientId, ConsultationStatus status);

    boolean existsByDoctorIdAndScheduledDateTime(Long doctorId, LocalDateTime scheduledDateTime);

}
