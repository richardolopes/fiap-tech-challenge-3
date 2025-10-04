package com.hospital.shared.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Events Tests")
class DomainEventsTest {

    @Nested
    @DisplayName("DomainEvent Base Class Tests")
    class DomainEventBaseClassTests {

        @Test
        @DisplayName("Should generate event id and timestamp when creating domain event")
        void shouldGenerateEventIdAndTimestampWhenCreatingDomainEvent() {
            String eventType = "TEST_EVENT";

            TestDomainEvent event = new TestDomainEvent(eventType);

            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
            assertEquals(eventType, event.getEventType());
            assertTrue(event.getOccurredOn().isBefore(LocalDateTime.now().plusSeconds(1)));
            assertTrue(event.getOccurredOn().isAfter(LocalDateTime.now().minusSeconds(1)));
        }

        @Test
        @DisplayName("Should allow setting event properties")
        void shouldAllowSettingEventProperties() {
            TestDomainEvent event = new TestDomainEvent("TEST_EVENT");
            String newEventId = "custom-event-id";
            LocalDateTime newOccurredOn = LocalDateTime.now().minusHours(1);
            String newEventType = "NEW_TEST_EVENT";

            event.setEventId(newEventId);
            event.setOccurredOn(newOccurredOn);
            event.setEventType(newEventType);

            assertEquals(newEventId, event.getEventId());
            assertEquals(newOccurredOn, event.getOccurredOn());
            assertEquals(newEventType, event.getEventType());
        }

        @Test
        @DisplayName("Should create event with default constructor")
        void shouldCreateEventWithDefaultConstructor() {
            TestDomainEvent event = new TestDomainEvent();

            assertNull(event.getEventId());
            assertNull(event.getOccurredOn());
            assertNull(event.getEventType());
        }

        private static class TestDomainEvent extends DomainEvent {
            public TestDomainEvent() {
                super();
            }

            public TestDomainEvent(String eventType) {
                super(eventType);
            }
        }
    }

    @Nested
    @DisplayName("ConsultationCreatedEvent Tests")
    class ConsultationCreatedEventTests {

        @Test
        @DisplayName("Should create consultation created event with all properties")
        void shouldCreateConsultationCreatedEventWithAllProperties() {
            Long consultationId = 1L;
            Long patientId = 2L;
            Long doctorId = 3L;
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            String patientEmail = "patient@email.com";
            String patientName = "João Silva";
            String doctorName = "Dr. Maria Santos";

            ConsultationCreatedEvent event = new ConsultationCreatedEvent(
                consultationId, patientId, doctorId, scheduledDateTime,
                patientEmail, patientName, doctorName
            );

            assertEquals("CONSULTATION_CREATED", event.getEventType());
            assertEquals(consultationId, event.getConsultationId());
            assertEquals(patientId, event.getPatientId());
            assertEquals(doctorId, event.getDoctorId());
            assertEquals(scheduledDateTime, event.getScheduledDateTime());
            assertEquals(patientEmail, event.getPatientEmail());
            assertEquals(patientName, event.getPatientName());
            assertEquals(doctorName, event.getDoctorName());
            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
        }

        @Test
        @DisplayName("Should create event with default constructor")
        void shouldCreateEventWithDefaultConstructor() {
            ConsultationCreatedEvent event = new ConsultationCreatedEvent();

            assertNull(event.getEventId());
            assertNull(event.getOccurredOn());
            assertNull(event.getEventType());
            assertNull(event.getConsultationId());
            assertNull(event.getPatientId());
            assertNull(event.getDoctorId());
            assertNull(event.getScheduledDateTime());
            assertNull(event.getPatientEmail());
            assertNull(event.getPatientName());
            assertNull(event.getDoctorName());
        }

        @Test
        @DisplayName("Should allow setting properties via setters")
        void shouldAllowSettingPropertiesViaSetters() {
            ConsultationCreatedEvent event = new ConsultationCreatedEvent();
            Long consultationId = 1L;
            Long patientId = 2L;
            Long doctorId = 3L;
            LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
            String patientEmail = "patient@email.com";
            String patientName = "João Silva";
            String doctorName = "Dr. Maria Santos";

            event.setConsultationId(consultationId);
            event.setPatientId(patientId);
            event.setDoctorId(doctorId);
            event.setScheduledDateTime(scheduledDateTime);
            event.setPatientEmail(patientEmail);
            event.setPatientName(patientName);
            event.setDoctorName(doctorName);

            assertEquals(consultationId, event.getConsultationId());
            assertEquals(patientId, event.getPatientId());
            assertEquals(doctorId, event.getDoctorId());
            assertEquals(scheduledDateTime, event.getScheduledDateTime());
            assertEquals(patientEmail, event.getPatientEmail());
            assertEquals(patientName, event.getPatientName());
            assertEquals(doctorName, event.getDoctorName());
        }
    }

    @Nested
    @DisplayName("ConsultationCancelledEvent Tests")
    class ConsultationCancelledEventTests {

        @Test
        @DisplayName("Should create consultation cancelled event with all properties")
        void shouldCreateConsultationCancelledEventWithAllProperties() {
            Long consultationId = 1L;
            Long patientId = 2L;
            String reason = "Paciente não pode comparecer";
            String patientEmail = "patient@email.com";
            String patientName = "João Silva";

            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                consultationId, patientId, reason, patientEmail, patientName
            );

            assertEquals("CONSULTATION_CANCELLED", event.getEventType());
            assertEquals(consultationId, event.getConsultationId());
            assertEquals(patientId, event.getPatientId());
            assertEquals(reason, event.getReason());
            assertEquals(patientEmail, event.getPatientEmail());
            assertEquals(patientName, event.getPatientName());
            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
        }

        @Test
        @DisplayName("Should create event with default constructor")
        void shouldCreateEventWithDefaultConstructor() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent();

            assertNull(event.getEventId());
            assertNull(event.getOccurredOn());
            assertNull(event.getEventType());
            assertNull(event.getConsultationId());
            assertNull(event.getPatientId());
            assertNull(event.getReason());
            assertNull(event.getPatientEmail());
            assertNull(event.getPatientName());
        }
    }

    @Nested
    @DisplayName("ConsultationRescheduledEvent Tests")
    class ConsultationRescheduledEventTests {

        @Test
        @DisplayName("Should create consultation rescheduled event with all properties")
        void shouldCreateConsultationRescheduledEventWithAllProperties() {
            Long consultationId = 1L;
            Long patientId = 2L;
            LocalDateTime oldDateTime = LocalDateTime.now().plusDays(1);
            LocalDateTime newDateTime = LocalDateTime.now().plusDays(2);
            String patientEmail = "patient@email.com";
            String patientName = "João Silva";
            String doctorName = "Dr. Maria Santos";

            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                consultationId, patientId, oldDateTime, newDateTime,
                patientEmail, patientName, doctorName
            );

            assertEquals("CONSULTATION_RESCHEDULED", event.getEventType());
            assertEquals(consultationId, event.getConsultationId());
            assertEquals(patientId, event.getPatientId());
            assertEquals(oldDateTime, event.getOldDateTime());
            assertEquals(newDateTime, event.getNewDateTime());
            assertEquals(patientEmail, event.getPatientEmail());
            assertEquals(patientName, event.getPatientName());
            assertEquals(doctorName, event.getDoctorName());
            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
        }

        @Test
        @DisplayName("Should create event with default constructor")
        void shouldCreateEventWithDefaultConstructor() {
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent();

            assertNull(event.getEventId());
            assertNull(event.getOccurredOn());
            assertNull(event.getEventType());
            assertNull(event.getConsultationId());
            assertNull(event.getPatientId());
            assertNull(event.getOldDateTime());
            assertNull(event.getNewDateTime());
            assertNull(event.getPatientEmail());
            assertNull(event.getPatientName());
            assertNull(event.getDoctorName());
        }
    }
}