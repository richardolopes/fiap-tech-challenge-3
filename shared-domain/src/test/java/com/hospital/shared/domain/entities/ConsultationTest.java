package com.hospital.shared.domain.entities;

import com.hospital.shared.domain.enums.ConsultationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Consultation Entity Tests")
class ConsultationTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create consultation with valid data")
        void shouldCreateConsultationWithValidData() {
            Long patientId = 1L;
            Long doctorId = 2L;
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);

            Consultation consultation = new Consultation(patientId, doctorId, scheduledDateTime);

            assertNotNull(consultation);
            assertEquals(patientId, consultation.getPatientId());
            assertEquals(doctorId, consultation.getDoctorId());
            assertEquals(scheduledDateTime, consultation.getScheduledDateTime());
            assertEquals(ConsultationStatus.AGENDADA, consultation.getStatus());
            assertNotNull(consultation.getCreatedAt());
            assertNotNull(consultation.getUpdatedAt());
            assertNull(consultation.getActualStartTime());
            assertNull(consultation.getActualEndTime());
        }

        @Test
        @DisplayName("Should create consultation with all parameters")
        void shouldCreateConsultationWithAllParameters() {
            Long id = 1L;
            Long patientId = 2L;
            Long doctorId = 3L;
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            LocalDateTime actualStartTime = LocalDateTime.now();
            LocalDateTime actualEndTime = LocalDateTime.now().plusHours(1);
            ConsultationStatus status = ConsultationStatus.CONCLUIDA;
            String notes = "Consulta realizada";
            String symptoms = "Dor de cabeça";
            String diagnosis = "Enxaqueca";
            String prescription = "Ibuprofeno";
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            Consultation consultation = new Consultation(id, patientId, doctorId, scheduledDateTime,
                actualStartTime, actualEndTime, status, notes, symptoms, diagnosis, prescription,
                createdAt, updatedAt);

            assertEquals(id, consultation.getId());
            assertEquals(patientId, consultation.getPatientId());
            assertEquals(doctorId, consultation.getDoctorId());
            assertEquals(scheduledDateTime, consultation.getScheduledDateTime());
            assertEquals(actualStartTime, consultation.getActualStartTime());
            assertEquals(actualEndTime, consultation.getActualEndTime());
            assertEquals(status, consultation.getStatus());
            assertEquals(notes, consultation.getNotes());
            assertEquals(symptoms, consultation.getSymptoms());
            assertEquals(diagnosis, consultation.getDiagnosis());
            assertEquals(prescription, consultation.getPrescription());
            assertEquals(createdAt, consultation.getCreatedAt());
            assertEquals(updatedAt, consultation.getUpdatedAt());
        }

        @Test
        @DisplayName("Should throw exception when patient ID is null")
        void shouldThrowExceptionWhenPatientIdIsNull() {
            Long doctorId = 2L;
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);

            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Consultation(null, doctorId, scheduledDateTime)
            );
            
            assertEquals("Patient ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when doctor ID is null")
        void shouldThrowExceptionWhenDoctorIdIsNull() {
            Long patientId = 1L;
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);

            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Consultation(patientId, null, scheduledDateTime)
            );
            
            assertEquals("Doctor ID is required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when scheduled date time is null")
        void shouldThrowExceptionWhenScheduledDateTimeIsNull() {
            Long patientId = 1L;
            Long doctorId = 2L;

            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Consultation(patientId, doctorId, null)
            );
            
            assertEquals("Consultation date and time are required", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when scheduled date time is in the past")
        void shouldThrowExceptionWhenScheduledDateTimeIsInThePast() {
            Long patientId = 1L;
            Long doctorId = 2L;
            LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Consultation(patientId, doctorId, pastDateTime)
            );
            
            assertEquals("Cannot schedule consultation in the past", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Reschedule Tests")
    class RescheduleTests {

        @Test
        @DisplayName("Should reschedule consultation when status is AGENDADA")
        void shouldRescheduleConsultationWhenStatusIsAgendada() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));
            LocalDateTime newDateTime = LocalDateTime.now().plusDays(2);

            consultation.reschedule(newDateTime);

            assertEquals(newDateTime, consultation.getScheduledDateTime());
        }

        @Test
        @DisplayName("Should throw exception when trying to reschedule completed consultation")
        void shouldThrowExceptionWhenTryingToRescheduleCompletedConsultation() {
            Consultation consultation = new Consultation(1L, 1L, 2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), ConsultationStatus.CONCLUIDA,
                "Completed", "Symptoms", "Diagnosis", "Prescription", LocalDateTime.now(), LocalDateTime.now());

            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> consultation.reschedule(LocalDateTime.now().plusDays(2))
            );
            
            assertEquals("Cannot reschedule completed consultation", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when trying to reschedule cancelled consultation")
        void shouldThrowExceptionWhenTryingToRescheduleCancelledConsultation() {
            Consultation consultation = new Consultation(1L, 1L, 2L, LocalDateTime.now().plusDays(1),
                null, null, ConsultationStatus.CANCELADA, "Cancelled", null, null, null, 
                LocalDateTime.now(), LocalDateTime.now());

            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> consultation.reschedule(LocalDateTime.now().plusDays(2))
            );
            
            assertEquals("Cannot reschedule cancelled consultation", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when rescheduling to past date")
        void shouldThrowExceptionWhenReschedulingToPastDate() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));
            LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> consultation.reschedule(pastDateTime)
            );
            
            assertEquals("Cannot schedule consultation in the past", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cancel Consultation Tests")
    class CancelConsultationTests {

        @Test
        @DisplayName("Should cancel scheduled consultation")
        void shouldCancelScheduledConsultation() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));
            String reason = "Paciente não pode comparecer";

            consultation.cancel(reason);

            assertEquals(ConsultationStatus.CANCELADA, consultation.getStatus());
            assertEquals(reason, consultation.getNotes());
        }
    }

    @Nested
    @DisplayName("Status Check Methods Tests")
    class StatusCheckMethodsTests {

        @Test
        @DisplayName("Should return true for isActive when not cancelled")
        void shouldReturnTrueForIsActiveWhenNotCancelled() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));

            assertTrue(consultation.isActive());
        }

        @Test
        @DisplayName("Should return false for isActive when cancelled")
        void shouldReturnFalseForIsActiveWhenCancelled() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));
            consultation.cancel("Cancelled");

            assertFalse(consultation.isActive());
        }

        @Test
        @DisplayName("Should return false for isCompleted when status is not CONCLUIDA")
        void shouldReturnFalseForIsCompletedWhenStatusIsNotConcluida() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));

            assertFalse(consultation.isCompleted());
        }

        @Test
        @DisplayName("Should return true for canBeRescheduled when status is AGENDADA")
        void shouldReturnTrueForCanBeRescheduledWhenStatusIsAgendada() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));

            assertTrue(consultation.canBeRescheduled());
        }

        @Test
        @DisplayName("Should return true for canBeCancelled when not completed or cancelled")
        void shouldReturnTrueForCanBeCancelledWhenNotCompletedOrCancelled() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));

            assertTrue(consultation.canBeCancelled());
        }

        @Test
        @DisplayName("Should return false for canBeCancelled when already cancelled")
        void shouldReturnFalseForCanBeCancelledWhenAlreadyCancelled() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));
            consultation.cancel("Cancelled");

            assertFalse(consultation.canBeCancelled());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when all key fields are the same")
        void shouldBeEqualWhenAllKeyFieldsAreTheSame() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            Consultation consultation1 = new Consultation(1L, 1L, 2L, scheduledDateTime,
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());
            Consultation consultation2 = new Consultation(1L, 1L, 2L, scheduledDateTime,
                LocalDateTime.now(), LocalDateTime.now(), ConsultationStatus.CONCLUIDA, "Different notes", 
                "Different symptoms", "Different diagnosis", "Different prescription",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1));

            assertEquals(consultation1, consultation2);
            assertEquals(consultation1.hashCode(), consultation2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            Consultation consultation1 = new Consultation(1L, 1L, 2L, scheduledDateTime,
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());
            Consultation consultation2 = new Consultation(2L, 1L, 2L, scheduledDateTime,
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());

            assertNotEquals(consultation1, consultation2);
        }

        @Test
        @DisplayName("Should not be equal when patient ids are different")
        void shouldNotBeEqualWhenPatientIdsAreDifferent() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            Consultation consultation1 = new Consultation(1L, 1L, 2L, scheduledDateTime,
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());
            Consultation consultation2 = new Consultation(1L, 3L, 2L, scheduledDateTime,
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());

            assertNotEquals(consultation1, consultation2);
        }

        @Test
        @DisplayName("Should not be equal when doctor ids are different")
        void shouldNotBeEqualWhenDoctorIdsAreDifferent() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            Consultation consultation1 = new Consultation(1L, 1L, 2L, scheduledDateTime,
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());
            Consultation consultation2 = new Consultation(1L, 1L, 3L, scheduledDateTime,
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());

            assertNotEquals(consultation1, consultation2);
        }

        @Test
        @DisplayName("Should not be equal when scheduled date times are different")
        void shouldNotBeEqualWhenScheduledDateTimesAreDifferent() {
            Consultation consultation1 = new Consultation(1L, 1L, 2L, LocalDateTime.now().plusDays(1),
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());
            Consultation consultation2 = new Consultation(1L, 1L, 2L, LocalDateTime.now().plusDays(2),
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());

            assertNotEquals(consultation1, consultation2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));

            assertNotEquals(consultation, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));
            String notAConsultation = "not a consultation";

            assertNotEquals(consultation, notAConsultation);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            Consultation consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));

            assertEquals(consultation, consultation);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return formatted string representation")
        void shouldReturnFormattedStringRepresentation() {
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            Consultation consultation = new Consultation(1L, 2L, 3L, scheduledDateTime,
                null, null, ConsultationStatus.AGENDADA, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());

            String result = consultation.toString();

            assertTrue(result.contains("Consultation{"));
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("patientId=2"));
            assertTrue(result.contains("doctorId=3"));
            assertTrue(result.contains("status=AGENDADA"));
        }
    }
}