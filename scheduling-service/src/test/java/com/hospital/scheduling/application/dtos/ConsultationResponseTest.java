package com.hospital.scheduling.application.dtos;

import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.enums.ConsultationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsultationResponse DTO Tests")
class ConsultationResponseTest {

    @Test
    @DisplayName("Should create ConsultationResponse with all parameters")
    void shouldCreateConsultationResponseWithAllParameters() {
        Long id = 1L;
        Long patientId = 2L;
        Long doctorId = 3L;
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime actualStartTime = LocalDateTime.now();
        LocalDateTime actualEndTime = LocalDateTime.now().plusHours(1);
        ConsultationStatus status = ConsultationStatus.CONCLUIDA;
        String notes = "Consulta realizada com sucesso";
        String symptoms = "Dor de cabeça";
        String diagnosis = "Enxaqueca";
        String prescription = "Ibuprofeno 600mg";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        String patientName = "João Silva";
        String patientEmail = "joao@email.com";
        String doctorName = "Dr. Maria Santos";

        ConsultationResponse response = new ConsultationResponse(
            id, patientId, doctorId, scheduledDateTime, actualStartTime, actualEndTime,
            status, notes, symptoms, diagnosis, prescription, createdAt, updatedAt,
            patientName, patientEmail, doctorName
        );

        assertEquals(id, response.id());
        assertEquals(patientId, response.patientId());
        assertEquals(doctorId, response.doctorId());
        assertEquals(scheduledDateTime, response.scheduledDateTime());
        assertEquals(actualStartTime, response.actualStartTime());
        assertEquals(actualEndTime, response.actualEndTime());
        assertEquals(status, response.status());
        assertEquals(notes, response.notes());
        assertEquals(symptoms, response.symptoms());
        assertEquals(diagnosis, response.diagnosis());
        assertEquals(prescription, response.prescription());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
        assertEquals(patientName, response.patientName());
        assertEquals(patientEmail, response.patientEmail());
        assertEquals(doctorName, response.doctorName());
    }

    @Test
    @DisplayName("Should create ConsultationResponse from Consultation entity")
    void shouldCreateConsultationResponseFromConsultationEntity() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        Consultation consultation = new Consultation(1L, 2L, scheduledDateTime);
        consultation.setId(10L);
        ConsultationResponse response = ConsultationResponse.fromEntity(consultation);

        assertEquals(consultation.getId(), response.id());
        assertEquals(consultation.getPatientId(), response.patientId());
        assertEquals(consultation.getDoctorId(), response.doctorId());
        assertEquals(consultation.getScheduledDateTime(), response.scheduledDateTime());
        assertEquals(consultation.getActualStartTime(), response.actualStartTime());
        assertEquals(consultation.getActualEndTime(), response.actualEndTime());
        assertEquals(consultation.getStatus(), response.status());
        assertEquals(consultation.getNotes(), response.notes());
        assertEquals(consultation.getSymptoms(), response.symptoms());
        assertEquals(consultation.getDiagnosis(), response.diagnosis());
        assertEquals(consultation.getPrescription(), response.prescription());
        assertEquals(consultation.getCreatedAt(), response.createdAt());
        assertEquals(consultation.getUpdatedAt(), response.updatedAt());
        
        assertNull(response.patientName());
        assertNull(response.patientEmail());
        assertNull(response.doctorName());
    }

    @Test
    @DisplayName("Should create ConsultationResponse from scheduled consultation")
    void shouldCreateConsultationResponseFromScheduledConsultation() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(2);
        Consultation consultation = new Consultation(5L, 6L, scheduledDateTime);
        consultation.setId(20L);

        ConsultationResponse response = ConsultationResponse.fromEntity(consultation);

        assertEquals(20L, response.id());
        assertEquals(5L, response.patientId());
        assertEquals(6L, response.doctorId());
        assertEquals(scheduledDateTime, response.scheduledDateTime());
        assertEquals(ConsultationStatus.AGENDADA, response.status());
        assertNull(response.actualStartTime());
        assertNull(response.actualEndTime());
        assertNull(response.notes());
        assertNull(response.symptoms());
        assertNull(response.diagnosis());
        assertNull(response.prescription());
    }

    @Test
    @DisplayName("Should add user information using withUserInfo")
    void shouldAddUserInformationUsingWithUserInfo() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        Consultation consultation = new Consultation(1L, 2L, scheduledDateTime);
        consultation.setId(10L);
        
        ConsultationResponse originalResponse = ConsultationResponse.fromEntity(consultation);
        String patientName = "João Silva";
        String patientEmail = "joao@email.com";
        String doctorName = "Dr. Maria Santos";

        ConsultationResponse responseWithUserInfo = originalResponse.withUserInfo(patientName, patientEmail, doctorName);

        assertEquals(originalResponse.id(), responseWithUserInfo.id());
        assertEquals(originalResponse.patientId(), responseWithUserInfo.patientId());
        assertEquals(originalResponse.doctorId(), responseWithUserInfo.doctorId());
        assertEquals(originalResponse.scheduledDateTime(), responseWithUserInfo.scheduledDateTime());
        assertEquals(originalResponse.status(), responseWithUserInfo.status());
        
        assertEquals(patientName, responseWithUserInfo.patientName());
        assertEquals(patientEmail, responseWithUserInfo.patientEmail());
        assertEquals(doctorName, responseWithUserInfo.doctorName());
    }

    @Test
    @DisplayName("Should preserve all fields when adding user info")
    void shouldPreserveAllFieldsWhenAddingUserInfo() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime actualStartTime = LocalDateTime.now();
        LocalDateTime actualEndTime = LocalDateTime.now().plusHours(1);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        ConsultationResponse originalResponse = new ConsultationResponse(
            1L, 2L, 3L, scheduledDateTime, actualStartTime, actualEndTime,
            ConsultationStatus.CONCLUIDA, "Consulta OK", "Febre", "Gripe", "Paracetamol",
            createdAt, updatedAt, null, null, null
        );

        ConsultationResponse responseWithUserInfo = originalResponse.withUserInfo(
            "Ana Silva", "ana@email.com", "Dr. Carlos"
        );

        assertEquals(1L, responseWithUserInfo.id());
        assertEquals(2L, responseWithUserInfo.patientId());
        assertEquals(3L, responseWithUserInfo.doctorId());
        assertEquals(scheduledDateTime, responseWithUserInfo.scheduledDateTime());
        assertEquals(actualStartTime, responseWithUserInfo.actualStartTime());
        assertEquals(actualEndTime, responseWithUserInfo.actualEndTime());
        assertEquals(ConsultationStatus.CONCLUIDA, responseWithUserInfo.status());
        assertEquals("Consulta OK", responseWithUserInfo.notes());
        assertEquals("Febre", responseWithUserInfo.symptoms());
        assertEquals("Gripe", responseWithUserInfo.diagnosis());
        assertEquals("Paracetamol", responseWithUserInfo.prescription());
        assertEquals(createdAt, responseWithUserInfo.createdAt());
        assertEquals(updatedAt, responseWithUserInfo.updatedAt());
        assertEquals("Ana Silva", responseWithUserInfo.patientName());
        assertEquals("ana@email.com", responseWithUserInfo.patientEmail());
        assertEquals("Dr. Carlos", responseWithUserInfo.doctorName());
    }

    @Test
    @DisplayName("Should handle null user information in withUserInfo")
    void shouldHandleNullUserInformationInWithUserInfo() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        Consultation consultation = new Consultation(1L, 2L, scheduledDateTime);
        consultation.setId(10L);
        
        ConsultationResponse originalResponse = ConsultationResponse.fromEntity(consultation);

        ConsultationResponse responseWithUserInfo = originalResponse.withUserInfo(null, null, null);

        assertNull(responseWithUserInfo.patientName());
        assertNull(responseWithUserInfo.patientEmail());
        assertNull(responseWithUserInfo.doctorName());
        assertEquals(originalResponse.id(), responseWithUserInfo.id());
        assertEquals(originalResponse.status(), responseWithUserInfo.status());
    }

    @Test
    @DisplayName("Should be equal when all fields are the same")
    void shouldBeEqualWhenAllFieldsAreTheSame() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        ConsultationResponse response1 = new ConsultationResponse(
            1L, 2L, 3L, scheduledDateTime, null, null, ConsultationStatus.AGENDADA,
            null, null, null, null, createdAt, updatedAt,
            "João Silva", "joao@email.com", "Dr. Maria"
        );

        ConsultationResponse response2 = new ConsultationResponse(
            1L, 2L, 3L, scheduledDateTime, null, null, ConsultationStatus.AGENDADA,
            null, null, null, null, createdAt, updatedAt,
            "João Silva", "joao@email.com", "Dr. Maria"
        );

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when fields are different")
    void shouldNotBeEqualWhenFieldsAreDifferent() {
        LocalDateTime scheduledDateTime1 = LocalDateTime.now().plusDays(1);
        LocalDateTime scheduledDateTime2 = LocalDateTime.now().plusDays(2);
        LocalDateTime now = LocalDateTime.now();

        ConsultationResponse response1 = new ConsultationResponse(
            1L, 2L, 3L, scheduledDateTime1, null, null, ConsultationStatus.AGENDADA,
            null, null, null, null, now, now,
            "João Silva", "joao@email.com", "Dr. Maria"
        );

        ConsultationResponse response2 = new ConsultationResponse(
            2L, 3L, 4L, scheduledDateTime2, null, null, ConsultationStatus.CANCELADA,
            null, null, null, null, now, now,
            "Ana Silva", "ana@email.com", "Dr. Carlos"
        );

        assertNotEquals(response1, response2);
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime now = LocalDateTime.now();

        ConsultationResponse response = new ConsultationResponse(
            1L, 2L, 3L, scheduledDateTime, null, null, ConsultationStatus.AGENDADA,
            null, null, null, null, now, now,
            "João Silva", "joao@email.com", "Dr. Maria Santos"
        );

        String result = response.toString();

        assertTrue(result.contains("ConsultationResponse"));
        assertTrue(result.contains("João Silva"));
        assertTrue(result.contains("Dr. Maria Santos"));
        assertTrue(result.contains("AGENDADA"));
    }
}
