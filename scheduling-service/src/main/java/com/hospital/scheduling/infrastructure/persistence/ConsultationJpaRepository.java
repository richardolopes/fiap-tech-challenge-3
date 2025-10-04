package com.hospital.scheduling.infrastructure.persistence;

import com.hospital.shared.domain.enums.ConsultationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationJpaRepository extends JpaRepository<ConsultationJpaEntity, Long> {

    List<ConsultationJpaEntity> findByPatientId(Long patientId);

    List<ConsultationJpaEntity> findByPatientIdAndStatus(Long patientId, ConsultationStatus status);

    @Query("SELECT c FROM ConsultationJpaEntity c WHERE c.patientId = :patientId AND c.scheduledDateTime > :now AND c.status = 'AGENDADA' ORDER BY c.scheduledDateTime ASC")
    List<ConsultationJpaEntity> findFutureConsultationsByPatientId(@Param("patientId") Long patientId,
                                                                   @Param("now") LocalDateTime now);

    boolean existsByDoctorIdAndScheduledDateTime(Long doctorId, LocalDateTime scheduledDateTime);
}
