package com.hospital.notification.listener;

import com.hospital.notification.service.EmailService;
import com.hospital.shared.domain.events.ConsultationCreatedEvent;
import com.hospital.shared.domain.events.ConsultationRescheduledEvent;
import com.hospital.shared.domain.events.ConsultationCancelledEvent;
import com.hospital.shared.domain.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ConsultationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ConsultationEventListener.class);
    private final EmailService emailService;

    public ConsultationEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "consultation-events", groupId = "notification-service")
    public void handleConsultationEvent(
            @Payload DomainEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        try {
            logger.info("EVENT RECEIVED from topic {} [{}] offset {}: {}", 
                topic, partition, offset, event.getClass().getSimpleName());

            switch (event.getEventType()) {
                case "CONSULTATION_CREATED":
                    handleConsultationCreated((ConsultationCreatedEvent) event);
                    break;
                case "CONSULTATION_RESCHEDULED":
                    handleConsultationRescheduled((ConsultationRescheduledEvent) event);
                    break;
                case "CONSULTATION_CANCELLED":
                    handleConsultationCancelled((ConsultationCancelledEvent) event);
                    break;
                default:
                    logger.warn("Unrecognized event type: {}", event.getEventType());
                    return;
            }
            
            logger.info("Event processed successfully");
            
        } catch (Exception e) {
            logger.error("Error processing event: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void handleConsultationCreated(ConsultationCreatedEvent event) {
        logger.info("PROCESSING CONSULTATION CREATION");
        logger.info("Consultation ID: {}", event.getConsultationId());
        logger.info("Patient: {}", event.getPatientName());
        logger.info("Doctor: {}", event.getDoctorName());
        logger.info("Date/Time: {}", event.getScheduledDateTime());

        String patientSubject = "Consultation Scheduled - Hospital";
        String patientMessage = String.format(
            "Hello %s!\n\n" +
            "Your consultation has been successfully scheduled:\n" +
            "Date: %s\n" +
            "Doctor: %s\n\n" +
            "Please arrive 15 minutes early.\n" +
            "If you have any questions, please contact us.\n\n" +
            "Best regards,\n" +
            "Hospital Team",
            event.getPatientName(),
            event.getScheduledDateTime(),
            event.getDoctorName()
        );
        
        emailService.sendEmail(event.getPatientEmail(), patientSubject, patientMessage);
        logger.info("Confirmation email sent to: {}", event.getPatientEmail());
    }

    private void handleConsultationRescheduled(ConsultationRescheduledEvent event) {
        logger.info("PROCESSING CONSULTATION RESCHEDULING");
        logger.info("Consultation ID: {}", event.getConsultationId());
        logger.info("Patient: {}", event.getPatientName());
        logger.info("Previous Date: {}", event.getOldDateTime());
        logger.info("New Date: {}", event.getNewDateTime());
        
        String subject = "Consultation Rescheduled - Hospital";
        String message = String.format(
            "Hello %s!\n\n" +
            "Your consultation has been rescheduled:\n" +
            "Previous date: %s\n" +
            "New date: %s\n" +
            "Doctor: %s\n\n" +
            "Please arrive 15 minutes early.\n" +
            "If you have any questions, please contact us.\n\n" +
            "Best regards,\n" +
            "Hospital Team",
            event.getPatientName(),
            event.getOldDateTime(),
            event.getNewDateTime(),
            event.getDoctorName()
        );
        
        emailService.sendEmail(event.getPatientEmail(), subject, message);
        logger.info("Rescheduling email sent to: {}", event.getPatientEmail());
    }

    private void handleConsultationCancelled(ConsultationCancelledEvent event) {
        logger.info("PROCESSING CONSULTATION CANCELLATION");
        logger.info("Consultation ID: {}", event.getConsultationId());
        logger.info("Patient: {}", event.getPatientName());
        logger.info("Reason: {}", event.getReason());
        
        String subject = "Consultation Cancelled - Hospital";
        String message = String.format(
            "Hello %s!\n\n" +
            "Unfortunately, your consultation has been cancelled.\n" +
            "Reason: %s\n\n" +
            "To reschedule, please contact us:\n" +
            "(11) 1234-5678\n" +
            "scheduling@hospital.com\n\n" +
            "Best regards,\n" +
            "Hospital Team",
            event.getPatientName(),
            event.getReason()
        );
        
        emailService.sendEmail(event.getPatientEmail(), subject, message);
        logger.info("Cancellation email sent to: {}", event.getPatientEmail());
    }
}
