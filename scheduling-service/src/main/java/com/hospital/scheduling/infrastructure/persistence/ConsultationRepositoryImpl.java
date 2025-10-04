package com.hospital.scheduling.infrastructure.persistence;

import com.hospital.scheduling.domain.repositories.ConsultationRepository;
import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.enums.ConsultationStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ConsultationRepositoryImpl implements ConsultationRepository {

    private final ConsultationJpaRepository consultationJpaRepository;

    public ConsultationRepositoryImpl(ConsultationJpaRepository consultationJpaRepository) {
        this.consultationJpaRepository = consultationJpaRepository;
    }

    @Override
    public Consultation save(Consultation consultation) {
        ConsultationJpaEntity entity;

        if (consultation.getId() != null) {
            entity = consultationJpaRepository.findById(consultation.getId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Consulta não encontrada: " + consultation.getId()));
            entity.updateFromDomainEntity(consultation);
        } else {
            entity = new ConsultationJpaEntity(consultation);
        }

        ConsultationJpaEntity savedEntity = consultationJpaRepository.save(entity);
        Consultation savedConsultation = savedEntity.toDomainEntity();

        if (consultation.getId() == null) {
            savedConsultation.setId(savedEntity.getId());
        }

        return savedConsultation;
    }

    @Override
    public Optional<Consultation> findById(Long id) {
        return consultationJpaRepository.findById(id)
                .map(ConsultationJpaEntity::toDomainEntity);
    }

    @Override
    public List<Consultation> findAll() {
        return consultationJpaRepository.findAll()
                .stream()
                .map(ConsultationJpaEntity::toDomainEntity)
                .toList();
    }

    @Override
    public List<Consultation> findByPatientId(Long patientId) {
        return consultationJpaRepository.findByPatientId(patientId)
                .stream()
                .map(ConsultationJpaEntity::toDomainEntity)
                .toList();
    }

    @Override
    public List<Consultation> findFutureConsultationsByPatientId(Long patientId) {
        return consultationJpaRepository.findFutureConsultationsByPatientId(patientId, LocalDateTime.now())
                .stream()
                .map(ConsultationJpaEntity::toDomainEntity)
                .toList();
    }

    @Override
    public List<Consultation> findByPatientIdAndStatus(Long patientId, ConsultationStatus status) {
        return consultationJpaRepository.findByPatientIdAndStatus(patientId, status)
                .stream()
                .map(ConsultationJpaEntity::toDomainEntity)
                .toList();
    }

    @Override
    public boolean existsByDoctorIdAndScheduledDateTime(Long doctorId, LocalDateTime scheduledDateTime) {
        return consultationJpaRepository.existsByDoctorIdAndScheduledDateTime(doctorId, scheduledDateTime);
    }

}
