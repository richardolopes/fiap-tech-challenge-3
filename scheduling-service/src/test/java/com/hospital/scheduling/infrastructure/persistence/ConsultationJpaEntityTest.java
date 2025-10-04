package com.hospital.scheduling.infrastructure.persistence;

import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.enums.ConsultationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsultationJpaEntity Tests")
class ConsultationJpaEntityTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty ConsultationJpaEntity with default constructor")
        void shouldCreateEmptyConsultationJpaEntityWithDefaultConstructor() {
            ConsultationJpaEntity entity = new ConsultationJpaEntity();

            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getPatientId());
            assertNull(entity.getDoctorId());
            assertNull(entity.getScheduledDateTime());
            assertNull(entity.getActualStartTime());
            assertNull(entity.getActualEndTime());
            assertNull(entity.getStatus());
            assertNull(entity.getNotes());
            assertNull(entity.getSymptoms());
            assertNull(entity.getDiagnosis());
            assertNull(entity.getPrescription());
            assertNull(entity.getCreatedAt());
            assertNull(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create ConsultationJpaEntity from Consultation domain entity")
        void shouldCreateConsultationJpaEntityFromConsultationDomainEntity() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            Consultation consultation = new Consultation(1L, 2L, scheduledDateTime);
            consultation.setId(10L);

            ConsultationJpaEntity entity = new ConsultationJpaEntity(consultation);

            assertEquals(consultation.getId(), entity.getId());
            assertEquals(consultation.getPatientId(), entity.getPatientId());
            assertEquals(consultation.getDoctorId(), entity.getDoctorId());
            assertEquals(consultation.getScheduledDateTime(), entity.getScheduledDateTime());
            assertEquals(consultation.getActualStartTime(), entity.getActualStartTime());
            assertEquals(consultation.getActualEndTime(), entity.getActualEndTime());
            assertEquals(consultation.getStatus(), entity.getStatus());
            assertEquals(consultation.getNotes(), entity.getNotes());
            assertEquals(consultation.getSymptoms(), entity.getSymptoms());
            assertEquals(consultation.getDiagnosis(), entity.getDiagnosis());
            assertEquals(consultation.getPrescription(), entity.getPrescription());
            assertEquals(consultation.getCreatedAt(), entity.getCreatedAt());
            assertEquals(consultation.getUpdatedAt(), entity.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Domain Entity Conversion Tests")
    class DomainEntityConversionTests {

        @Test
        @DisplayName("Should convert to domain entity correctly")
        void shouldConvertToDomainEntityCorrectly() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            LocalDateTime actualStartTime = LocalDateTime.now();
            LocalDateTime actualEndTime = LocalDateTime.now().plusHours(1);
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            ConsultationJpaEntity entity = new ConsultationJpaEntity();
            entity.setId(1L);
            entity.setPatientId(2L);
            entity.setDoctorId(3L);
            entity.setScheduledDateTime(scheduledDateTime);
            entity.setActualStartTime(actualStartTime);
            entity.setActualEndTime(actualEndTime);
            entity.setStatus(ConsultationStatus.CONCLUIDA);
            entity.setNotes("Consulta realizada");
            entity.setSymptoms("Febre");
            entity.setDiagnosis("Gripe");
            entity.setPrescription("Paracetamol");
            entity.setCreatedAt(createdAt);
            entity.setUpdatedAt(updatedAt);

            Consultation domainEntity = entity.toDomainEntity();

            assertEquals(entity.getId(), domainEntity.getId());
            assertEquals(entity.getPatientId(), domainEntity.getPatientId());
            assertEquals(entity.getDoctorId(), domainEntity.getDoctorId());
            assertEquals(entity.getScheduledDateTime(), domainEntity.getScheduledDateTime());
            assertEquals(entity.getActualStartTime(), domainEntity.getActualStartTime());
            assertEquals(entity.getActualEndTime(), domainEntity.getActualEndTime());
            assertEquals(entity.getStatus(), domainEntity.getStatus());
            assertEquals(entity.getNotes(), domainEntity.getNotes());
            assertEquals(entity.getSymptoms(), domainEntity.getSymptoms());
            assertEquals(entity.getDiagnosis(), domainEntity.getDiagnosis());
            assertEquals(entity.getPrescription(), domainEntity.getPrescription());
            assertEquals(entity.getCreatedAt(), domainEntity.getCreatedAt());
            assertEquals(entity.getUpdatedAt(), domainEntity.getUpdatedAt());
        }

        @Test
        @DisplayName("Should convert scheduled consultation to domain entity")
        void shouldConvertScheduledConsultationToDomainEntity() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(2);
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime updatedAt = LocalDateTime.now();

            ConsultationJpaEntity entity = new ConsultationJpaEntity();
            entity.setId(5L);
            entity.setPatientId(10L);
            entity.setDoctorId(20L);
            entity.setScheduledDateTime(scheduledDateTime);
            entity.setStatus(ConsultationStatus.AGENDADA);
            entity.setCreatedAt(createdAt);
            entity.setUpdatedAt(updatedAt);

            Consultation domainEntity = entity.toDomainEntity();

            assertEquals(5L, domainEntity.getId());
            assertEquals(10L, domainEntity.getPatientId());
            assertEquals(20L, domainEntity.getDoctorId());
            assertEquals(scheduledDateTime, domainEntity.getScheduledDateTime());
            assertEquals(ConsultationStatus.AGENDADA, domainEntity.getStatus());
            assertNull(domainEntity.getActualStartTime());
            assertNull(domainEntity.getActualEndTime());
            assertNull(domainEntity.getNotes());
            assertNull(domainEntity.getSymptoms());
            assertNull(domainEntity.getDiagnosis());
            assertNull(domainEntity.getPrescription());
        }

        @Test
        @DisplayName("Should convert cancelled consultation to domain entity")
        void shouldConvertCancelledConsultationToDomainEntity() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            ConsultationJpaEntity entity = new ConsultationJpaEntity();
            entity.setId(15L);
            entity.setPatientId(25L);
            entity.setDoctorId(35L);
            entity.setScheduledDateTime(scheduledDateTime);
            entity.setStatus(ConsultationStatus.CANCELADA);
            entity.setNotes("Paciente não pode comparecer");
            entity.setCreatedAt(LocalDateTime.now().minusDays(1));
            entity.setUpdatedAt(LocalDateTime.now());

            Consultation domainEntity = entity.toDomainEntity();

            assertEquals(ConsultationStatus.CANCELADA, domainEntity.getStatus());
            assertEquals("Paciente não pode comparecer", domainEntity.getNotes());
            assertFalse(domainEntity.isActive());
        }
    }

    @Nested
    @DisplayName("Update From Domain Entity Tests")
    class UpdateFromDomainEntityTests {

        @Test
        @DisplayName("Should update JPA entity from domain entity")
        void shouldUpdateJpaEntityFromDomainEntity() {
            LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(2);
            ConsultationJpaEntity entity = new ConsultationJpaEntity();
            entity.setId(1L);
            entity.setPatientId(10L);
            entity.setDoctorId(20L);
            entity.setStatus(ConsultationStatus.AGENDADA);
            entity.setCreatedAt(originalCreatedAt);

            LocalDateTime newScheduledDateTime = LocalDateTime.now().plusDays(3);
            Consultation updatedConsultation = new Consultation(15L, 25L, newScheduledDateTime);
            updatedConsultation.setId(1L);

            entity.updateFromDomainEntity(updatedConsultation);

            assertEquals(1L, entity.getId()); 
            assertEquals(15L, entity.getPatientId());
            assertEquals(25L, entity.getDoctorId());
            assertEquals(newScheduledDateTime, entity.getScheduledDateTime());
            assertEquals(updatedConsultation.getUpdatedAt(), entity.getUpdatedAt());
            assertEquals(originalCreatedAt, entity.getCreatedAt()); 
        }

        @Test
        @DisplayName("Should preserve created date when updating from domain entity")
        void shouldPreserveCreatedDateWhenUpdatingFromDomainEntity() {
            LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(5);
            ConsultationJpaEntity entity = new ConsultationJpaEntity();
            entity.setId(1L);
            entity.setCreatedAt(originalCreatedAt);

            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            Consultation updatedConsultation = new Consultation(1L, 2L, scheduledDateTime);
            updatedConsultation.setId(1L);

            entity.updateFromDomainEntity(updatedConsultation);

            assertEquals(originalCreatedAt, entity.getCreatedAt()); 
            assertEquals(updatedConsultation.getUpdatedAt(), entity.getUpdatedAt());
        }

        @Test
        @DisplayName("Should update cancelled consultation")
        void shouldUpdateCancelledConsultation() {
            ConsultationJpaEntity entity = new ConsultationJpaEntity();
            entity.setId(10L);
            entity.setStatus(ConsultationStatus.AGENDADA);

            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            Consultation consultation = new Consultation(1L, 2L, scheduledDateTime);
            consultation.setId(10L);
            consultation.cancel("Emergência familiar");

            entity.updateFromDomainEntity(consultation);

            assertEquals(ConsultationStatus.CANCELADA, entity.getStatus());
            assertEquals("Emergência familiar", entity.getNotes());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersAndSettersTests {

        @Test
        @DisplayName("Should set and get all properties correctly")
        void shouldSetAndGetAllPropertiesCorrectly() {
            ConsultationJpaEntity entity = new ConsultationJpaEntity();
            Long id = 1L;
            Long patientId = 2L;
            Long doctorId = 3L;
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            LocalDateTime actualStartTime = LocalDateTime.now();
            LocalDateTime actualEndTime = LocalDateTime.now().plusHours(1);
            ConsultationStatus status = ConsultationStatus.CONCLUIDA;
            String notes = "Consulta realizada";
            String symptoms = "Febre";
            String diagnosis = "Gripe";
            String prescription = "Paracetamol";
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            entity.setId(id);
            entity.setPatientId(patientId);
            entity.setDoctorId(doctorId);
            entity.setScheduledDateTime(scheduledDateTime);
            entity.setActualStartTime(actualStartTime);
            entity.setActualEndTime(actualEndTime);
            entity.setStatus(status);
            entity.setNotes(notes);
            entity.setSymptoms(symptoms);
            entity.setDiagnosis(diagnosis);
            entity.setPrescription(prescription);
            entity.setCreatedAt(createdAt);
            entity.setUpdatedAt(updatedAt);

            assertEquals(id, entity.getId());
            assertEquals(patientId, entity.getPatientId());
            assertEquals(doctorId, entity.getDoctorId());
            assertEquals(scheduledDateTime, entity.getScheduledDateTime());
            assertEquals(actualStartTime, entity.getActualStartTime());
            assertEquals(actualEndTime, entity.getActualEndTime());
            assertEquals(status, entity.getStatus());
            assertEquals(notes, entity.getNotes());
            assertEquals(symptoms, entity.getSymptoms());
            assertEquals(diagnosis, entity.getDiagnosis());
            assertEquals(prescription, entity.getPrescription());
            assertEquals(createdAt, entity.getCreatedAt());
            assertEquals(updatedAt, entity.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            ConsultationJpaEntity entity = new ConsultationJpaEntity();

            entity.setId(null);
            entity.setPatientId(null);
            entity.setDoctorId(null);
            entity.setScheduledDateTime(null);
            entity.setActualStartTime(null);
            entity.setActualEndTime(null);
            entity.setStatus(null);
            entity.setNotes(null);
            entity.setSymptoms(null);
            entity.setDiagnosis(null);
            entity.setPrescription(null);
            entity.setCreatedAt(null);
            entity.setUpdatedAt(null);

            assertNull(entity.getId());
            assertNull(entity.getPatientId());
            assertNull(entity.getDoctorId());
            assertNull(entity.getScheduledDateTime());
            assertNull(entity.getActualStartTime());
            assertNull(entity.getActualEndTime());
            assertNull(entity.getStatus());
            assertNull(entity.getNotes());
            assertNull(entity.getSymptoms());
            assertNull(entity.getDiagnosis());
            assertNull(entity.getPrescription());
            assertNull(entity.getCreatedAt());
            assertNull(entity.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Round Trip Conversion Tests")
    class RoundTripConversionTests {

        @Test
        @DisplayName("Should maintain data integrity in round trip conversion")
        void shouldMaintainDataIntegrityInRoundTripConversion() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            Consultation originalConsultation = new Consultation(1L, 2L, scheduledDateTime);
            originalConsultation.setId(99L);

            ConsultationJpaEntity entity = new ConsultationJpaEntity(originalConsultation);
            Consultation convertedConsultation = entity.toDomainEntity();

            assertEquals(originalConsultation.getId(), convertedConsultation.getId());
            assertEquals(originalConsultation.getPatientId(), convertedConsultation.getPatientId());
            assertEquals(originalConsultation.getDoctorId(), convertedConsultation.getDoctorId());
            assertEquals(originalConsultation.getScheduledDateTime(), convertedConsultation.getScheduledDateTime());
            assertEquals(originalConsultation.getStatus(), convertedConsultation.getStatus());
            assertEquals(originalConsultation.getSymptoms(), convertedConsultation.getSymptoms());
            assertEquals(originalConsultation.getDiagnosis(), convertedConsultation.getDiagnosis());
            assertEquals(originalConsultation.getPrescription(), convertedConsultation.getPrescription());
            assertEquals(originalConsultation.getNotes(), convertedConsultation.getNotes());
        }

        @Test
        @DisplayName("Should handle multiple round trip conversions")
        void shouldHandleMultipleRoundTripConversions() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(2);
            Consultation originalConsultation = new Consultation(5L, 10L, scheduledDateTime);
            originalConsultation.setId(100L);

            ConsultationJpaEntity entity1 = new ConsultationJpaEntity(originalConsultation);
            Consultation convertedConsultation1 = entity1.toDomainEntity();
            
            ConsultationJpaEntity entity2 = new ConsultationJpaEntity(convertedConsultation1);
            Consultation convertedConsultation2 = entity2.toDomainEntity();

            assertEquals(originalConsultation.getId(), convertedConsultation2.getId());
            assertEquals(originalConsultation.getPatientId(), convertedConsultation2.getPatientId());
            assertEquals(originalConsultation.getDoctorId(), convertedConsultation2.getDoctorId());
            assertEquals(originalConsultation.getScheduledDateTime(), convertedConsultation2.getScheduledDateTime());
            assertEquals(originalConsultation.getStatus(), convertedConsultation2.getStatus());
        }
    }
}
