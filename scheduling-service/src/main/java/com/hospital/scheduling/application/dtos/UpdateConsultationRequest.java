package com.hospital.scheduling.application.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateConsultationRequest(
        Long patientId,
        Long doctorId,
        @NotNull(message = "Scheduled date and time are required")
        LocalDateTime scheduledDateTime,
        String reason
) {
}
