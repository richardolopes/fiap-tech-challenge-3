package com.hospital.shared.domain.events;

import java.time.LocalDateTime;

public class ConsultationCreatedEvent extends DomainEvent {
	private Long consultationId;
	private Long patientId;
	private Long doctorId;
	private LocalDateTime scheduledDateTime;
	private String patientEmail;
	private String patientName;
	private String doctorName;

	protected ConsultationCreatedEvent() {
		super();
	}

	public ConsultationCreatedEvent(Long consultationId, Long patientId, Long doctorId,
			LocalDateTime scheduledDateTime, String patientEmail,
			String patientName, String doctorName) {
		super("CONSULTATION_CREATED");
		this.consultationId = consultationId;
		this.patientId = patientId;
		this.doctorId = doctorId;
		this.scheduledDateTime = scheduledDateTime;
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

	public Long getDoctorId() {
		return doctorId;
	}

	public LocalDateTime getScheduledDateTime() {
		return scheduledDateTime;
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

	public void setConsultationId(Long consultationId) {
		this.consultationId = consultationId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}

	public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}

	public void setPatientEmail(String patientEmail) {
		this.patientEmail = patientEmail;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
}
