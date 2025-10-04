package com.hospital.scheduling.application.dtos;

import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.enums.ConsultationStatus;

import java.time.LocalDateTime;

public record ConsultationResponse(
        Long id,
        Long patientId,
        Long doctorId,
        LocalDateTime scheduledDateTime,
        LocalDateTime actualStartTime,
        LocalDateTime actualEndTime,
        ConsultationStatus status,
        String notes,
        String symptoms,
        String diagnosis,
        String prescription,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String patientName,
        String patientEmail,
        String doctorName) {

    public static ConsultationResponse fromEntity(Consultation consultation) {
        return new ConsultationResponse(
                consultation.getId(),
                consultation.getPatientId(),
                consultation.getDoctorId(),
                consultation.getScheduledDateTime(),
                consultation.getActualStartTime(),
                consultation.getActualEndTime(),
                consultation.getStatus(),
                consultation.getNotes(),
                consultation.getSymptoms(),
                consultation.getDiagnosis(),
                consultation.getPrescription(),
                consultation.getCreatedAt(),
                consultation.getUpdatedAt(),
                null,
                null,
                null
        );
    }

    public ConsultationResponse withUserInfo(String patientName, String patientEmail, String doctorName) {
        return new ConsultationResponse(
                id, patientId, doctorId, scheduledDateTime, actualStartTime, actualEndTime,
                status, notes, symptoms, diagnosis, prescription, createdAt, updatedAt,
                patientName, patientEmail, doctorName);
    }
}
