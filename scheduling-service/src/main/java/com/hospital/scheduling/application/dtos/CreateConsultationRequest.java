package com.hospital.scheduling.application.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public record CreateConsultationRequest(
        @NotNull(message = "ID do paciente é obrigatório") Long patientId,

        @NotNull(message = "ID do médico é obrigatório") Long doctorId,

        @NotNull(message = "Data e hora da consulta são obrigatórias") @Future(message = "Data da consulta deve ser no futuro") LocalDateTime scheduledDateTime) {
}
