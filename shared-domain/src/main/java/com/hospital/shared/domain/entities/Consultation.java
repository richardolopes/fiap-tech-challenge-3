package com.hospital.shared.domain.entities;

import com.hospital.shared.domain.enums.ConsultationStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class Consultation {
	private Long id;
	private Long patientId;
	private Long doctorId;
	private LocalDateTime scheduledDateTime;
	private LocalDateTime actualStartTime;
	private LocalDateTime actualEndTime;
	private ConsultationStatus status;
	private String notes;
	private String symptoms;
	private String diagnosis;
	private String prescription;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Consultation(Long patientId, Long doctorId, LocalDateTime scheduledDateTime) {
		this.patientId = Objects.requireNonNull(patientId, "Patient ID is required");
		this.doctorId = Objects.requireNonNull(doctorId, "Doctor ID is required");
		this.scheduledDateTime = validateScheduledDateTime(scheduledDateTime);
		this.status = ConsultationStatus.AGENDADA;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Consultation(Long id, Long patientId, Long doctorId, LocalDateTime scheduledDateTime,
			LocalDateTime actualStartTime, LocalDateTime actualEndTime,
			ConsultationStatus status, String notes, String symptoms,
			String diagnosis, String prescription, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.id = id;
		this.patientId = Objects.requireNonNull(patientId, "Patient ID is required");
		this.doctorId = Objects.requireNonNull(doctorId, "Doctor ID is required");
		this.scheduledDateTime = validateScheduledDateTime(scheduledDateTime);
		this.actualStartTime = actualStartTime;
		this.actualEndTime = actualEndTime;
		this.status = Objects.requireNonNull(status, "Status is required");
		this.notes = notes;
		this.symptoms = symptoms;
		this.diagnosis = diagnosis;
		this.prescription = prescription;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	private LocalDateTime validateScheduledDateTime(LocalDateTime scheduledDateTime) {
		Objects.requireNonNull(scheduledDateTime, "Consultation date and time are required");
		if (scheduledDateTime.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("Cannot schedule consultation in the past");
		}
		return scheduledDateTime;
	}

	public void reschedule(LocalDateTime newDateTime) {
		if (this.status == ConsultationStatus.CONCLUIDA) {
			throw new IllegalStateException("Cannot reschedule completed consultation");
		}
		if (this.status == ConsultationStatus.CANCELADA) {
			throw new IllegalStateException("Cannot reschedule cancelled consultation");
		}
		this.scheduledDateTime = validateScheduledDateTime(newDateTime);
		this.updatedAt = LocalDateTime.now();
	}

	public void cancel(String reason) {
		if (this.status == ConsultationStatus.CONCLUIDA) {
			throw new IllegalStateException("Cannot cancel completed consultation");
		}
		this.status = ConsultationStatus.CANCELADA;
		this.notes = reason;
		this.updatedAt = LocalDateTime.now();
	}

	public boolean isActive() {
		return this.status != ConsultationStatus.CANCELADA;
	}

	public boolean isCompleted() {
		return this.status == ConsultationStatus.CONCLUIDA;
	}

	public boolean canBeRescheduled() {
		return this.status == ConsultationStatus.AGENDADA;
	}

	public boolean canBeCancelled() {
		return this.status != ConsultationStatus.CONCLUIDA && this.status != ConsultationStatus.CANCELADA;
	}

	public Long getId() {
		return id;
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

	public LocalDateTime getActualStartTime() {
		return actualStartTime;
	}

	public LocalDateTime getActualEndTime() {
		return actualEndTime;
	}

	public ConsultationStatus getStatus() {
		return status;
	}

	public String getNotes() {
		return notes;
	}

	public String getSymptoms() {
		return symptoms;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public String getPrescription() {
		return prescription;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Consultation that = (Consultation) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(patientId, that.patientId) &&
				Objects.equals(doctorId, that.doctorId) &&
				Objects.equals(scheduledDateTime, that.scheduledDateTime);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, patientId, doctorId, scheduledDateTime);
	}

	@Override
	public String toString() {
		return "Consultation{" +
				"id=" + id +
				", patientId=" + patientId +
				", doctorId=" + doctorId +
				", scheduledDateTime=" + scheduledDateTime +
				", status=" + status +
				", createdAt=" + createdAt +
				'}';
	}
}
