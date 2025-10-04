package com.hospital.shared.domain.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "eventType",
		visible = true
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = ConsultationCreatedEvent.class, name = "CONSULTATION_CREATED"),
		@JsonSubTypes.Type(value = ConsultationCancelledEvent.class, name = "CONSULTATION_CANCELLED"),
		@JsonSubTypes.Type(value = ConsultationRescheduledEvent.class, name = "CONSULTATION_RESCHEDULED")
})
public abstract class DomainEvent {
	private String eventId;
	private LocalDateTime occurredOn;
	private String eventType;

	protected DomainEvent(String eventType) {
		this.eventId = java.util.UUID.randomUUID().toString();
		this.occurredOn = LocalDateTime.now();
		this.eventType = eventType;
	}

	protected DomainEvent() {
	}

	public String getEventId() {
		return eventId;
	}

	public LocalDateTime getOccurredOn() {
		return occurredOn;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public void setOccurredOn(LocalDateTime occurredOn) {
		this.occurredOn = occurredOn;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
}
