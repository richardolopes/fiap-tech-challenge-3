package com.hospital.notification.listener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.hospital.notification.service.EmailService;
import com.hospital.shared.domain.events.ConsultationCancelledEvent;
import com.hospital.shared.domain.events.ConsultationCreatedEvent;
import com.hospital.shared.domain.events.ConsultationRescheduledEvent;
import com.hospital.shared.domain.events.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultationEventListener Tests")
class ConsultationEventListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ConsultationEventListener listener;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(ConsultationEventListener.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Nested
    @DisplayName("Consultation Created Events")
    class ConsultationCreatedTests {

        @Test
        @DisplayName("Should handle consultation created event successfully")
        void shouldHandleConsultationCreatedEventSuccessfully() {
            LocalDateTime scheduledDateTime = LocalDateTime.of(2024, 12, 25, 10, 30);
            ConsultationCreatedEvent event = new ConsultationCreatedEvent(
                1L, 2L, 3L, scheduledDateTime,
                "patient@test.com", "John Doe", "Dr. Smith"
            );

            listener.handleConsultationEvent(event, "consultation-events", 0, 100L);

            ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

            verify(emailService).sendEmail(toCaptor.capture(), subjectCaptor.capture(), contentCaptor.capture());

            assertEquals("patient@test.com", toCaptor.getValue());
            assertEquals("Consultation Scheduled - Hospital", subjectCaptor.getValue());
            assertTrue(contentCaptor.getValue().contains("John Doe"));
            assertTrue(contentCaptor.getValue().contains("Dr. Smith"));
            assertTrue(contentCaptor.getValue().contains("2024-12-25T10:30"));

            var logsList = listAppender.list;
            assertTrue(logsList.stream().anyMatch(log -> 
                log.getMessage().equals("EVENT RECEIVED from topic {} [{}] offset {}: {}") &&
                log.getArgumentArray()[0].equals("consultation-events")
            ));
        }

        @Test
        @DisplayName("Should format consultation created email correctly")
        void shouldFormatConsultationCreatedEmailCorrectly() {
            LocalDateTime scheduledDateTime = LocalDateTime.of(2024, 12, 25, 14, 45);
            ConsultationCreatedEvent event = new ConsultationCreatedEvent(
                1L, 2L, 3L, scheduledDateTime,
                "jane@test.com", "Jane Smith", "Dr. Johnson"
            );

            listener.handleConsultationEvent(event, "consultation-events", 0, 100L);

            ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
            verify(emailService).sendEmail(anyString(), anyString(), contentCaptor.capture());

            String content = contentCaptor.getValue();
            assertTrue(content.contains("Hello Jane Smith!"));
            assertTrue(content.contains("Date: 2024-12-25T14:45"));
            assertTrue(content.contains("Doctor: Dr. Johnson"));
            assertTrue(content.contains("Please arrive 15 minutes early"));
            assertTrue(content.contains("Hospital Team"));
        }
    }

    @Nested
    @DisplayName("Consultation Rescheduled Events")
    class ConsultationRescheduledTests {

        @Test
        @DisplayName("Should handle consultation rescheduled event successfully")
        void shouldHandleConsultationRescheduledEventSuccessfully() {
            LocalDateTime oldDateTime = LocalDateTime.of(2024, 12, 25, 10, 30);
            LocalDateTime newDateTime = LocalDateTime.of(2024, 12, 26, 14, 00);
            ConsultationRescheduledEvent event = new ConsultationRescheduledEvent(
                1L, 2L, oldDateTime, newDateTime,
                "patient@test.com", "John Doe", "Dr. Smith"
            );

            listener.handleConsultationEvent(event, "consultation-events", 0, 101L);

            ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

            verify(emailService).sendEmail(toCaptor.capture(), subjectCaptor.capture(), contentCaptor.capture());

            assertEquals("patient@test.com", toCaptor.getValue());
            assertEquals("Consultation Rescheduled - Hospital", subjectCaptor.getValue());
            
            String content = contentCaptor.getValue();
            assertTrue(content.contains("John Doe"));
            assertTrue(content.contains("Dr. Smith"));
            assertTrue(content.contains("Previous date: 2024-12-25T10:30"));
            assertTrue(content.contains("New date: 2024-12-26T14:00"));
        }
    }

    @Nested
    @DisplayName("Consultation Cancelled Events")
    class ConsultationCancelledTests {

        @Test
        @DisplayName("Should handle consultation cancelled event successfully")
        void shouldHandleConsultationCancelledEventSuccessfully() {
            ConsultationCancelledEvent event = new ConsultationCancelledEvent(
                1L, 2L, "Doctor unavailable",
                "patient@test.com", "John Doe"
            );

            listener.handleConsultationEvent(event, "consultation-events", 0, 102L);

            ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

            verify(emailService).sendEmail(toCaptor.capture(), subjectCaptor.capture(), contentCaptor.capture());

            assertEquals("patient@test.com", toCaptor.getValue());
            assertEquals("Consultation Cancelled - Hospital", subjectCaptor.getValue());
            
            String content = contentCaptor.getValue();
            assertTrue(content.contains("John Doe"));
            assertTrue(content.contains("Reason: Doctor unavailable"));
            assertTrue(content.contains("(11) 1234-5678"));
            assertTrue(content.contains("scheduling@hospital.com"));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle unrecognized event type")
        void shouldHandleUnrecognizedEventType() {
            DomainEvent unknownEvent = new TestDomainEvent("UNKNOWN_EVENT");

            listener.handleConsultationEvent(unknownEvent, "consultation-events", 0, 103L);

            verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
            
            var logsList = listAppender.list;
            assertTrue(logsList.stream().anyMatch(log -> 
                log.getLevel() == Level.WARN &&
                log.getMessage().equals("Unrecognized event type: {}") &&
                log.getArgumentArray()[0].equals("UNKNOWN_EVENT")
            ));
        }

        @Test
        @DisplayName("Should propagate email service exception")
        void shouldPropagateEmailServiceException() {
            ConsultationCreatedEvent event = new ConsultationCreatedEvent(
                1L, 2L, 3L, LocalDateTime.now(),
                "patient@test.com", "John Doe", "Dr. Smith"
            );
            
            doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

            assertThrows(RuntimeException.class, () -> 
                listener.handleConsultationEvent(event, "consultation-events", 0, 104L)
            );
            
            var logsList = listAppender.list;
            assertTrue(logsList.stream().anyMatch(log -> 
                log.getLevel() == Level.ERROR &&
                log.getMessage().equals("Error processing event: {}")
            ));
        }

        @Test
        @DisplayName("Should handle null event gracefully")
        void shouldHandleNullEventGracefully() {
            assertThrows(Exception.class, () -> 
                listener.handleConsultationEvent(null, "consultation-events", 0, 105L)
            );
        }
    }

    @Nested
    @DisplayName("Logging Tests")
    class LoggingTests {

        @Test
        @DisplayName("Should log event details correctly")
        void shouldLogEventDetailsCorrectly() {
            ConsultationCreatedEvent event = new ConsultationCreatedEvent(
                1L, 2L, 3L, LocalDateTime.now(),
                "patient@test.com", "John Doe", "Dr. Smith"
            );

            listener.handleConsultationEvent(event, "test-topic", 2, 999L);

            var logsList = listAppender.list;
            assertTrue(logsList.stream().anyMatch(log -> 
                log.getMessage().equals("EVENT RECEIVED from topic {} [{}] offset {}: {}") &&
                log.getArgumentArray()[0].equals("test-topic") &&
                log.getArgumentArray()[1].equals(2) &&
                log.getArgumentArray()[2].equals(999L) &&
                log.getArgumentArray()[3].equals("ConsultationCreatedEvent")
            ));
            
            assertTrue(logsList.stream().anyMatch(log -> 
                log.getMessage().equals("Event processed successfully")
            ));
        }

        @Test
        @DisplayName("Should log consultation processing details")
        void shouldLogConsultationProcessingDetails() {
            LocalDateTime scheduledDateTime = LocalDateTime.of(2024, 12, 25, 10, 30);
            ConsultationCreatedEvent event = new ConsultationCreatedEvent(
                123L, 456L, 789L, scheduledDateTime,
                "patient@test.com", "John Doe", "Dr. Smith"
            );

            listener.handleConsultationEvent(event, "consultation-events", 0, 100L);

            var logsList = listAppender.list;
            assertTrue(logsList.stream().anyMatch(log -> 
                log.getMessage().equals("PROCESSING CONSULTATION CREATION")
            ));
            assertTrue(logsList.stream().anyMatch(log -> 
                log.getMessage().equals("Consultation ID: {}") &&
                log.getArgumentArray()[0].equals(123L)
            ));
            assertTrue(logsList.stream().anyMatch(log -> 
                log.getMessage().equals("Patient: {}") &&
                log.getArgumentArray()[0].equals("John Doe")
            ));
            assertTrue(logsList.stream().anyMatch(log -> 
                log.getMessage().equals("Doctor: {}") &&
                log.getArgumentArray()[0].equals("Dr. Smith")
            ));
        }
    }

    private static class TestDomainEvent extends DomainEvent {
        public TestDomainEvent(String eventType) {
            super(eventType);
        }
    }
}
