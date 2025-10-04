package com.hospital.scheduling.infrastructure.events;

import com.hospital.shared.domain.events.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventPublisher Tests")
class EventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private SendResult<String, Object> sendResult;

    @InjectMocks
    private EventPublisher eventPublisher;

    private TestDomainEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new TestDomainEvent("TEST_EVENT");
    }

    @Test
    @DisplayName("Should publish event successfully")
    void shouldPublishEventSuccessfully() {
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        eventPublisher.publishEvent(testEvent);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        assertEquals("consultation-events", topicCaptor.getValue());
        assertEquals(testEvent.getEventId(), keyCaptor.getValue());
        assertEquals(testEvent, valueCaptor.getValue());
    }

    @Test
    @DisplayName("Should handle kafka send exception gracefully")
    void shouldHandleKafkaSendExceptionGracefully() {
        when(kafkaTemplate.send(anyString(), anyString(), any()))
            .thenThrow(new RuntimeException("Kafka connection error"));

        assertDoesNotThrow(() -> eventPublisher.publishEvent(testEvent));

        verify(kafkaTemplate).send(eq("consultation-events"), eq(testEvent.getEventId()), eq(testEvent));
    }

    @Test
    @DisplayName("Should handle null event gracefully")
    void shouldHandleNullEventGracefully() {
        assertThrows(Exception.class, () -> eventPublisher.publishEvent(null));
    }

    @Test
    @DisplayName("Should use correct topic name")
    void shouldUseCorrectTopicName() {
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        eventPublisher.publishEvent(testEvent);

        verify(kafkaTemplate).send(eq("consultation-events"), anyString(), any());
    }

    @Test
    @DisplayName("Should use event ID as message key")
    void shouldUseEventIdAsMessageKey() {
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        String eventId = "test-event-id-123";
        TestDomainEvent eventWithSpecificId = new TestDomainEvent("TEST_EVENT");
        eventWithSpecificId.setEventId(eventId);

        eventPublisher.publishEvent(eventWithSpecificId);

        verify(kafkaTemplate).send(anyString(), eq(eventId), any());
    }

    @Test
    @DisplayName("Should handle completed future success callback")
    void shouldHandleCompletedFutureSuccessCallback() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        eventPublisher.publishEvent(testEvent);
        future.complete(sendResult); 

        verify(kafkaTemplate).send(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should handle completed future exception callback")
    void shouldHandleCompletedFutureExceptionCallback() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        eventPublisher.publishEvent(testEvent);
        future.completeExceptionally(new RuntimeException("Send failed")); 

        verify(kafkaTemplate).send(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should not fail when kafka template returns null future")
    void shouldNotFailWhenKafkaTemplateReturnsNullFuture() {
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

        assertDoesNotThrow(() -> eventPublisher.publishEvent(testEvent));
    }

    @Test
    @DisplayName("Should publish multiple events independently")
    void shouldPublishMultipleEventsIndependently() {
        CompletableFuture<SendResult<String, Object>> future1 = CompletableFuture.completedFuture(sendResult);
        CompletableFuture<SendResult<String, Object>> future2 = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(anyString(), anyString(), any()))
            .thenReturn(future1)
            .thenReturn(future2);

        TestDomainEvent event1 = new TestDomainEvent("EVENT_1");
        TestDomainEvent event2 = new TestDomainEvent("EVENT_2");

        eventPublisher.publishEvent(event1);
        eventPublisher.publishEvent(event2);

        verify(kafkaTemplate, times(2)).send(anyString(), anyString(), any());
        verify(kafkaTemplate).send(eq("consultation-events"), eq(event1.getEventId()), eq(event1));
        verify(kafkaTemplate).send(eq("consultation-events"), eq(event2.getEventId()), eq(event2));
    }

    private static class TestDomainEvent extends DomainEvent {
        public TestDomainEvent(String eventType) {
            super(eventType);
        }

        @Override
        public void setEventId(String eventId) {
            super.setEventId(eventId);
        }
    }
}
