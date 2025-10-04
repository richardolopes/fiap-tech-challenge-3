package com.hospital.shared.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsultationRescheduledEvent Tests")
class ConsultationRescheduledEventTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create event with all required parameters")
        void shouldCreateEventWithAllRequiredParameters() {
            LocalDateTime oldDateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
            LocalDateTime newDateTime = LocalDateTime.of(2024, 1, 16, 14, 30);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L,
                    2L,
                    oldDateTime,
                    newDateTime,
                    "paciente@email.com",
                    "João Silva",
                    "Dr. Maria Santos"
            );

            assertNotNull(event);
            assertEquals("CONSULTATION_RESCHEDULED", event.getEventType());
            assertEquals(1L, event.getConsultationId());
            assertEquals(2L, event.getPatientId());
            assertEquals(oldDateTime, event.getOldDateTime());
            assertEquals(newDateTime, event.getNewDateTime());
            assertEquals("paciente@email.com", event.getPatientEmail());
            assertEquals("João Silva", event.getPatientName());
            assertEquals("Dr. Maria Santos", event.getDoctorName());
            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
        }

        @Test
        @DisplayName("Should create event with null date times")
        void shouldCreateEventWithNullDateTimes() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L,
                    2L,
                    null,
                    null,
                    "paciente@email.com",
                    "João Silva",
                    "Dr. Maria Santos"
            );

            assertNotNull(event);
            assertEquals(1L, event.getConsultationId());
            assertEquals(2L, event.getPatientId());
            assertNull(event.getOldDateTime());
            assertNull(event.getNewDateTime());
            assertEquals("paciente@email.com", event.getPatientEmail());
            assertEquals("João Silva", event.getPatientName());
            assertEquals("Dr. Maria Santos", event.getDoctorName());
        }

        @Test
        @DisplayName("Should create event with same old and new date times")
        void shouldCreateEventWithSameOldAndNewDateTimes() {
            LocalDateTime sameDateTime = LocalDateTime.of(2024, 1, 15, 10, 0);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L,
                    2L,
                    sameDateTime,
                    sameDateTime,
                    "paciente@email.com",
                    "João Silva",
                    "Dr. Maria Santos"
            );

            assertEquals(sameDateTime, event.getOldDateTime());
            assertEquals(sameDateTime, event.getNewDateTime());
        }

        @Test
        @DisplayName("Should create event with future to past reschedule")
        void shouldCreateEventWithFutureToPastReschedule() {
            LocalDateTime futureDateTime = LocalDateTime.of(2025, 1, 15, 10, 0);
            LocalDateTime pastDateTime = LocalDateTime.of(2023, 1, 15, 10, 0);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L,
                    2L,
                    futureDateTime,
                    pastDateTime,
                    "paciente@email.com",
                    "João Silva",
                    "Dr. Maria Santos"
            );

            assertEquals(futureDateTime, event.getOldDateTime());
            assertEquals(pastDateTime, event.getNewDateTime());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct consultation ID")
        void shouldReturnCorrectConsultationId() {
            LocalDateTime oldDateTime = LocalDateTime.now();
            LocalDateTime newDateTime = LocalDateTime.now().plusDays(1);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    123L, 456L, oldDateTime, newDateTime, "email", "name", "doctor"
            );

            assertEquals(123L, event.getConsultationId());
        }

        @Test
        @DisplayName("Should return correct patient ID")
        void shouldReturnCorrectPatientId() {
            LocalDateTime oldDateTime = LocalDateTime.now();
            LocalDateTime newDateTime = LocalDateTime.now().plusDays(1);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    123L, 456L, oldDateTime, newDateTime, "email", "name", "doctor"
            );

            assertEquals(456L, event.getPatientId());
        }

        @Test
        @DisplayName("Should return correct old date time")
        void shouldReturnCorrectOldDateTime() {
            LocalDateTime oldDateTime = LocalDateTime.of(2024, 3, 15, 9, 30);
            LocalDateTime newDateTime = LocalDateTime.of(2024, 3, 16, 14, 0);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    123L, 456L, oldDateTime, newDateTime, "email", "name", "doctor"
            );

            assertEquals(oldDateTime, event.getOldDateTime());
        }

        @Test
        @DisplayName("Should return correct new date time")
        void shouldReturnCorrectNewDateTime() {
            LocalDateTime oldDateTime = LocalDateTime.of(2024, 3, 15, 9, 30);
            LocalDateTime newDateTime = LocalDateTime.of(2024, 3, 16, 14, 0);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    123L, 456L, oldDateTime, newDateTime, "email", "name", "doctor"
            );

            assertEquals(newDateTime, event.getNewDateTime());
        }

        @Test
        @DisplayName("Should return correct patient email")
        void shouldReturnCorrectPatientEmail() {
            LocalDateTime oldDateTime = LocalDateTime.now();
            LocalDateTime newDateTime = LocalDateTime.now().plusDays(1);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    123L, 456L, oldDateTime, newDateTime, "paciente@hospital.com", "name", "doctor"
            );

            assertEquals("paciente@hospital.com", event.getPatientEmail());
        }

        @Test
        @DisplayName("Should return correct patient name")
        void shouldReturnCorrectPatientName() {
            LocalDateTime oldDateTime = LocalDateTime.now();
            LocalDateTime newDateTime = LocalDateTime.now().plusDays(1);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    123L, 456L, oldDateTime, newDateTime, "email", "Maria Santos", "doctor"
            );

            assertEquals("Maria Santos", event.getPatientName());
        }

        @Test
        @DisplayName("Should return correct doctor name")
        void shouldReturnCorrectDoctorName() {
            LocalDateTime oldDateTime = LocalDateTime.now();
            LocalDateTime newDateTime = LocalDateTime.now().plusDays(1);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    123L, 456L, oldDateTime, newDateTime, "email", "name", "Dr. José Silva"
            );

            assertEquals("Dr. José Silva", event.getDoctorName());
        }
    }

    @Nested
    @DisplayName("Domain Event Inheritance Tests")
    class DomainEventInheritanceTests {

        @Test
        @DisplayName("Should inherit from DomainEvent")
        void shouldInheritFromDomainEvent() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                    "email", "name", "doctor"
            );

            assertInstanceOf(DomainEvent.class, event);
        }

        @Test
        @DisplayName("Should have correct event type")
        void shouldHaveCorrectEventType() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                    "email", "name", "doctor"
            );

            assertEquals("CONSULTATION_RESCHEDULED", event.getEventType());
        }

        @Test
        @DisplayName("Should have unique event ID")
        void shouldHaveUniqueEventId() {
            LocalDateTime oldDateTime = LocalDateTime.now();
            LocalDateTime newDateTime = LocalDateTime.now().plusDays(1);

            ConsultationRescheduledEvent event1 = new ConsultationRescheduledEvent(
                    1L, 2L, oldDateTime, newDateTime, "email", "name", "doctor"
            );
            ConsultationRescheduledEvent event2 = new ConsultationRescheduledEvent(
                    1L, 2L, oldDateTime, newDateTime, "email", "name", "doctor"
            );

            assertNotNull(event1.getEventId());
            assertNotNull(event2.getEventId());
            assertNotEquals(event1.getEventId(), event2.getEventId());
        }

        @Test
        @DisplayName("Should have timestamp")
        void shouldHaveTimestamp() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                    "email", "name", "doctor"
            );

            assertNotNull(event.getOccurredOn());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null consultation ID")
        void shouldHandleNullConsultationId() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    null, 2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                    "email", "name", "doctor"
            );

            assertNull(event.getConsultationId());
        }

        @Test
        @DisplayName("Should handle null patient ID")
        void shouldHandleNullPatientId() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                    "email", "name", "doctor"
            );

            assertNull(event.getPatientId());
        }

        @Test
        @DisplayName("Should handle null patient email")
        void shouldHandleNullPatientEmail() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                    null, "name", "doctor"
            );

            assertNull(event.getPatientEmail());
        }

        @Test
        @DisplayName("Should handle null patient name")
        void shouldHandleNullPatientName() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                    "email", null, "doctor"
            );

            assertNull(event.getPatientName());
        }

        @Test
        @DisplayName("Should handle null doctor name")
        void shouldHandleNullDoctorName() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                    "email", "name", null
            );

            assertNull(event.getDoctorName());
        }

        @Test
        @DisplayName("Should handle all null values")
        void shouldHandleAllNullValues() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    null, null, null, null, null, null, null
            );

            assertNull(event.getConsultationId());
            assertNull(event.getPatientId());
            assertNull(event.getOldDateTime());
            assertNull(event.getNewDateTime());
            assertNull(event.getPatientEmail());
            assertNull(event.getPatientName());
            assertNull(event.getDoctorName());
            assertEquals("CONSULTATION_RESCHEDULED", event.getEventType());
        }

        @Test
        @DisplayName("Should handle special characters in strings")
        void shouldHandleSpecialCharactersInStrings() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L,
                    2L,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1),
                    "email+test@domain-name.com",
                    "José da Silva-Santos Jr.",
                    "Dr. María García-López"
            );

            assertEquals("email+test@domain-name.com", event.getPatientEmail());
            assertEquals("José da Silva-Santos Jr.", event.getPatientName());
            assertEquals("Dr. María García-López", event.getDoctorName());
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                    "", "", ""
            );

            assertEquals("", event.getPatientEmail());
            assertEquals("", event.getPatientName());
            assertEquals("", event.getDoctorName());
        }

        @Test
        @DisplayName("Should handle very large IDs")
        void shouldHandleVeryLargeIds() {
            Long largeConsultationId = Long.MAX_VALUE;
            Long largePatientId = Long.MAX_VALUE - 1;

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    largeConsultationId, largePatientId, LocalDateTime.now(), 
                    LocalDateTime.now().plusDays(1), "email", "name", "doctor"
            );

            assertEquals(largeConsultationId, event.getConsultationId());
            assertEquals(largePatientId, event.getPatientId());
        }

        @Test
        @DisplayName("Should handle precise date time values")
        void shouldHandlePreciseDateTimeValues() {
            LocalDateTime preciseOldDateTime = LocalDateTime.of(2024, 3, 15, 9, 30, 45, 123456789);
            LocalDateTime preciseNewDateTime = LocalDateTime.of(2024, 3, 16, 14, 15, 30, 987654321);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, 2L, preciseOldDateTime, preciseNewDateTime, 
                    "email", "name", "doctor"
            );

            assertEquals(preciseOldDateTime, event.getOldDateTime());
            assertEquals(preciseNewDateTime, event.getNewDateTime());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should create event with realistic rescheduling scenario")
        void shouldCreateEventWithRealisticReschedulingScenario() {
            LocalDateTime originalAppointment = LocalDateTime.of(2024, 3, 15, 10, 0);
            LocalDateTime newAppointment = LocalDateTime.of(2024, 3, 16, 14, 30);

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1001L,
                    2001L,
                    originalAppointment,
                    newAppointment,
                    "joao.silva@email.com",
                    "João Silva dos Santos",
                    "Dr. Maria Santos Oliveira"
            );

            assertEquals(1001L, event.getConsultationId());
            assertEquals(2001L, event.getPatientId());
            assertEquals(originalAppointment, event.getOldDateTime());
            assertEquals(newAppointment, event.getNewDateTime());
            assertEquals("joao.silva@email.com", event.getPatientEmail());
            assertEquals("João Silva dos Santos", event.getPatientName());
            assertEquals("Dr. Maria Santos Oliveira", event.getDoctorName());
            assertEquals("CONSULTATION_RESCHEDULED", event.getEventType());
            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
        }

        @Test
        @DisplayName("Should work with different rescheduling scenarios")
        void shouldWorkWithDifferentReschedulingScenarios() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 3, 15, 10, 0);

            ConsultationRescheduledEvent morningToAfternoon = new ConsultationRescheduledEvent(
                    1L, 2L, baseTime.withHour(9), baseTime.withHour(15), 
                    "patient1@email.com", "Patient 1", "Dr. A"
            );
            assertEquals(9, morningToAfternoon.getOldDateTime().getHour());
            assertEquals(15, morningToAfternoon.getNewDateTime().getHour());

            ConsultationRescheduledEvent sameDayDifferentTime = new ConsultationRescheduledEvent(
                    2L, 3L, baseTime.withHour(10), baseTime.withHour(11), 
                    "patient2@email.com", "Patient 2", "Dr. B"
            );
            assertEquals(baseTime.toLocalDate(), sameDayDifferentTime.getOldDateTime().toLocalDate());
            assertEquals(baseTime.toLocalDate(), sameDayDifferentTime.getNewDateTime().toLocalDate());

            ConsultationRescheduledEvent differentDay = new ConsultationRescheduledEvent(
                    3L, 4L, baseTime, baseTime.plusDays(7), 
                    "patient3@email.com", "Patient 3", "Dr. C"
            );
            assertEquals(7, java.time.temporal.ChronoUnit.DAYS.between(
                    differentDay.getOldDateTime().toLocalDate(), 
                    differentDay.getNewDateTime().toLocalDate()
            ));
        }

        @Test
        @DisplayName("Should handle different time zones implicitly")
        void shouldHandleDifferentTimeZonesImplicitly() {
            LocalDateTime oldDateTime = LocalDateTime.of(2024, 3, 15, 10, 0);
            LocalDateTime newDateTime = LocalDateTime.of(2024, 3, 15, 16, 0); 

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                    1L, 2L, oldDateTime, newDateTime, 
                    "patient@email.com", "Patient Name", "Dr. Name"
            );

            assertEquals(oldDateTime, event.getOldDateTime());
            assertEquals(newDateTime, event.getNewDateTime());
            assertEquals(6, newDateTime.getHour() - oldDateTime.getHour());
        }
    }
}
