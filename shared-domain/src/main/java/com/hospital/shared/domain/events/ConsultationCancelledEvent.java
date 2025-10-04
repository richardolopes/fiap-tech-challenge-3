package com.hospital.shared.domain.events;

public class ConsultationCancelledEvent extends DomainEvent {
	private Long consultationId;
	private Long patientId;
	private String reason;
	private String patientEmail;
	private String patientName;

	protected ConsultationCancelledEvent() {
		super();
	}

	public ConsultationCancelledEvent(Long consultationId, Long patientId, String reason,
			String patientEmail, String patientName) {
		super("CONSULTATION_CANCELLED");
		this.consultationId = consultationId;
		this.patientId = patientId;
		this.reason = reason;
		this.patientEmail = patientEmail;
		this.patientName = patientName;
	}

	public Long getConsultationId() {
		return consultationId;
	}

	public Long getPatientId() {
		return patientId;
	}

	public String getReason() {
		return reason;
	}

	public String getPatientEmail() {
		return patientEmail;
	}

	public String getPatientName() {
		return patientName;
	}
}
