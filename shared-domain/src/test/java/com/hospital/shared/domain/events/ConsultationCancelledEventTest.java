package com.hospital.shared.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsultationCancelledEvent Tests")
class ConsultationCancelledEventTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create event with all required parameters")
        void shouldCreateEventWithAllRequiredParameters() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L,
                    2L,
                    "Paciente doente",
                    "paciente@email.com",
                    "João Silva"
            );

            assertNotNull(event);
            assertEquals("CONSULTATION_CANCELLED", event.getEventType());
            assertEquals(1L, event.getConsultationId());
            assertEquals(2L, event.getPatientId());
            assertEquals("Paciente doente", event.getReason());
            assertEquals("paciente@email.com", event.getPatientEmail());
            assertEquals("João Silva", event.getPatientName());
            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
        }

        @Test
        @DisplayName("Should create event with null reason")
        void shouldCreateEventWithNullReason() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L,
                    2L,
                    null,
                    "paciente@email.com",
                    "João Silva"
            );

            assertNotNull(event);
            assertEquals(1L, event.getConsultationId());
            assertEquals(2L, event.getPatientId());
            assertNull(event.getReason());
            assertEquals("paciente@email.com", event.getPatientEmail());
            assertEquals("João Silva", event.getPatientName());
        }

        @Test
        @DisplayName("Should create event with empty reason")
        void shouldCreateEventWithEmptyReason() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L,
                    2L,
                    "",
                    "paciente@email.com",
                    "João Silva"
            );

            assertEquals("", event.getReason());
        }

        @Test
        @DisplayName("Should create event with long reason text")
        void shouldCreateEventWithLongReasonText() {
            String longReason = "Este é um motivo muito longo para cancelamento que pode conter muitos detalhes sobre a situação que levou ao cancelamento da consulta médica incluindo problemas de saúde do paciente ou conflitos de agenda";
            
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L,
                    2L,
                    longReason,
                    "paciente@email.com",
                    "João Silva"
            );

            assertEquals(longReason, event.getReason());
        }

        @Test
        @DisplayName("Should create protected constructor event")
        void shouldCreateProtectedConstructorEvent() {
            assertDoesNotThrow(() -> {
                ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                        1L, 2L, "reason", "email", "name"
                );
                assertNotNull(event);
            });
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct consultation ID")
        void shouldReturnCorrectConsultationId() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    123L, 456L, "reason", "email", "name"
            );

            assertEquals(123L, event.getConsultationId());
        }

        @Test
        @DisplayName("Should return correct patient ID")
        void shouldReturnCorrectPatientId() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    123L, 456L, "reason", "email", "name"
            );

            assertEquals(456L, event.getPatientId());
        }

        @Test
        @DisplayName("Should return correct reason")
        void shouldReturnCorrectReason() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    123L, 456L, "Motivo específico", "email", "name"
            );

            assertEquals("Motivo específico", event.getReason());
        }

        @Test
        @DisplayName("Should return correct patient email")
        void shouldReturnCorrectPatientEmail() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    123L, 456L, "reason", "paciente@hospital.com", "name"
            );

            assertEquals("paciente@hospital.com", event.getPatientEmail());
        }

        @Test
        @DisplayName("Should return correct patient name")
        void shouldReturnCorrectPatientName() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    123L, 456L, "reason", "email", "Maria Santos"
            );

            assertEquals("Maria Santos", event.getPatientName());
        }
    }

    @Nested
    @DisplayName("Domain Event Inheritance Tests")
    class DomainEventInheritanceTests {

        @Test
        @DisplayName("Should inherit from DomainEvent")
        void shouldInheritFromDomainEvent() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L, 2L, "reason", "email", "name"
            );

            assertInstanceOf(DomainEvent.class, event);
        }

        @Test
        @DisplayName("Should have correct event type")
        void shouldHaveCorrectEventType() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L, 2L, "reason", "email", "name"
            );

            assertEquals("CONSULTATION_CANCELLED", event.getEventType());
        }

        @Test
        @DisplayName("Should have unique event ID")
        void shouldHaveUniqueEventId() {
            ConsultationCancelledEvent event1 = new ConsultationCancelledEvent(
                    1L, 2L, "reason", "email", "name"
            );
            ConsultationCancelledEvent event2 = new ConsultationCancelledEvent(
                    1L, 2L, "reason", "email", "name"
            );

            assertNotNull(event1.getEventId());
            assertNotNull(event2.getEventId());
            assertNotEquals(event1.getEventId(), event2.getEventId());
        }

        @Test
        @DisplayName("Should have timestamp")
        void shouldHaveTimestamp() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L, 2L, "reason", "email", "name"
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
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    null, 2L, "reason", "email", "name"
            );

            assertNull(event.getConsultationId());
        }

        @Test
        @DisplayName("Should handle null patient ID")
        void shouldHandleNullPatientId() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L, null, "reason", "email", "name"
            );

            assertNull(event.getPatientId());
        }

        @Test
        @DisplayName("Should handle null patient email")
        void shouldHandleNullPatientEmail() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L, 2L, "reason", null, "name"
            );

            assertNull(event.getPatientEmail());
        }

        @Test
        @DisplayName("Should handle null patient name")
        void shouldHandleNullPatientName() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L, 2L, "reason", "email", null
            );

            assertNull(event.getPatientName());
        }

        @Test
        @DisplayName("Should handle all null values")
        void shouldHandleAllNullValues() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    null, null, null, null, null
            );

            assertNull(event.getConsultationId());
            assertNull(event.getPatientId());
            assertNull(event.getReason());
            assertNull(event.getPatientEmail());
            assertNull(event.getPatientName());
            assertEquals("CONSULTATION_CANCELLED", event.getEventType());
        }

        @Test
        @DisplayName("Should handle special characters in strings")
        void shouldHandleSpecialCharactersInStrings() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L,
                    2L,
                    "Motivo com açentõs e çaractéres especiáis!@#$%",
                    "email+test@domain-name.com",
                    "José da Silva-Santos Jr."
            );

            assertEquals("Motivo com açentõs e çaractéres especiáis!@#$%", event.getReason());
            assertEquals("email+test@domain-name.com", event.getPatientEmail());
            assertEquals("José da Silva-Santos Jr.", event.getPatientName());
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L, 2L, "", "", ""
            );

            assertEquals("", event.getReason());
            assertEquals("", event.getPatientEmail());
            assertEquals("", event.getPatientName());
        }

        @Test
        @DisplayName("Should handle very large IDs")
        void shouldHandleVeryLargeIds() {
            Long largeConsultationId = Long.MAX_VALUE;
            Long largePatientId = Long.MAX_VALUE - 1;

            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    largeConsultationId, largePatientId, "reason", "email", "name"
            );

            assertEquals(largeConsultationId, event.getConsultationId());
            assertEquals(largePatientId, event.getPatientId());
        }

        @Test
        @DisplayName("Should handle whitespace in strings")
        void shouldHandleWhitespaceInStrings() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1L, 2L, "   reason with spaces   ", "  email@domain.com  ", "  Patient Name  "
            );

            assertEquals("   reason with spaces   ", event.getReason());
            assertEquals("  email@domain.com  ", event.getPatientEmail());
            assertEquals("  Patient Name  ", event.getPatientName());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should create event with realistic data")
        void shouldCreateEventWithRealisticData() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                    1001L,
                    2001L,
                    "Paciente com febre alta, não pode comparecer à consulta",
                    "joao.silva@email.com",
                    "João Silva dos Santos"
            );

            assertEquals(1001L, event.getConsultationId());
            assertEquals(2001L, event.getPatientId());
            assertEquals("Paciente com febre alta, não pode comparecer à consulta", event.getReason());
            assertEquals("joao.silva@email.com", event.getPatientEmail());
            assertEquals("João Silva dos Santos", event.getPatientName());
            assertEquals("CONSULTATION_CANCELLED", event.getEventType());
            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
        }

        @Test
        @DisplayName("Should work with different cancellation scenarios")
        void shouldWorkWithDifferentCancellationScenarios() {
            ConsultationCancelledEvent emergencyEvent = new ConsultationCancelledEvent(
                    1L, 2L, "Emergência médica", "patient@email.com", "Patient Name"
            );
            assertEquals("Emergência médica", emergencyEvent.getReason());

            ConsultationCancelledEvent doctorUnavailableEvent = new ConsultationCancelledEvent(
                    2L, 3L, "Médico indisponível", "patient2@email.com", "Patient 2"
            );
            assertEquals("Médico indisponível", doctorUnavailableEvent.getReason());

            ConsultationCancelledEvent scheduleConflictEvent = new ConsultationCancelledEvent(
                    3L, 4L, "Conflito de agenda", "patient3@email.com", "Patient 3"
            );
            assertEquals("Conflito de agenda", scheduleConflictEvent.getReason());
        }
    }
}
