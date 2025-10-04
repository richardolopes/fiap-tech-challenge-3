package com.hospital.scheduling.infrastructure.events;

import com.hospital.shared.domain.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    private static final String TOPIC_NAME = "consultation-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(DomainEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC_NAME, event.getEventId(),
                    event);

            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Evento {} publicado com sucesso no tópico {} com offset {}",
                            event.getEventType(), TOPIC_NAME, result.getRecordMetadata().offset());
                } else {
                    logger.error("Falha ao publicar evento {} no tópico {}: {}",
                            event.getEventType(), TOPIC_NAME, exception.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Erro ao enviar evento {} para Kafka: {}", event.getEventType(), e.getMessage(), e);
        }
    }
}
