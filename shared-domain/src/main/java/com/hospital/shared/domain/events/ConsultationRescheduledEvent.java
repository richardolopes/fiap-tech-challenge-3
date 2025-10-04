package com.hospital.shared.domain.events;

import java.time.LocalDateTime;

public class ConsultationRescheduledEvent extends DomainEvent {
	private Long consultationId;
	private Long patientId;
	private LocalDateTime oldDateTime;
	private LocalDateTime newDateTime;
	private String patientEmail;
	private String patientName;
	private String doctorName;

	protected ConsultationRescheduledEvent() {
		super();
	}

	public ConsultationRescheduledEvent(Long consultationId, Long patientId,
			LocalDateTime oldDateTime, LocalDateTime newDateTime,
			String patientEmail, String patientName, String doctorName) {
		super("CONSULTATION_RESCHEDULED");
		this.consultationId = consultationId;
		this.patientId = patientId;
		this.oldDateTime = oldDateTime;
		this.newDateTime = newDateTime;
		this.patientEmail = patientEmail;
		this.patientName = patientName;
		this.doctorName = doctorName;
	}

	public Long getConsultationId() {
		return consultationId;
	}

	public Long getPatientId() {
		return patientId;
	}

	public LocalDateTime getOldDateTime() {
		return oldDateTime;
	}

	public LocalDateTime getNewDateTime() {
		return newDateTime;
	}

	public String getPatientEmail() {
		return patientEmail;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getDoctorName() {
		return doctorName;
	}
}
