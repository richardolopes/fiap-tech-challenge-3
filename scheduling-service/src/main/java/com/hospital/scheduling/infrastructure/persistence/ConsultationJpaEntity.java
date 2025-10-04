package com.hospital.scheduling.infrastructure.persistence;

import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.enums.ConsultationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
public class ConsultationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "scheduled_date_time", nullable = false)
    private LocalDateTime scheduledDateTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsultationStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String prescription;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ConsultationJpaEntity() {
    }

    public ConsultationJpaEntity(Consultation consultation) {
        this.id = consultation.getId();
        this.patientId = consultation.getPatientId();
        this.doctorId = consultation.getDoctorId();
        this.scheduledDateTime = consultation.getScheduledDateTime();
        this.actualStartTime = consultation.getActualStartTime();
        this.actualEndTime = consultation.getActualEndTime();
        this.status = consultation.getStatus();
        this.notes = consultation.getNotes();
        this.symptoms = consultation.getSymptoms();
        this.diagnosis = consultation.getDiagnosis();
        this.prescription = consultation.getPrescription();
        this.createdAt = consultation.getCreatedAt();
        this.updatedAt = consultation.getUpdatedAt();
    }

    public Consultation toDomainEntity() {
        return new Consultation(id, patientId, doctorId, scheduledDateTime,
                actualStartTime, actualEndTime, status, notes,
                symptoms, diagnosis, prescription, createdAt, updatedAt);
    }

    public void updateFromDomainEntity(Consultation consultation) {
        this.patientId = consultation.getPatientId();
        this.doctorId = consultation.getDoctorId();
        this.scheduledDateTime = consultation.getScheduledDateTime();
        this.actualStartTime = consultation.getActualStartTime();
        this.actualEndTime = consultation.getActualEndTime();
        this.status = consultation.getStatus();
        this.notes = consultation.getNotes();
        this.symptoms = consultation.getSymptoms();
        this.diagnosis = consultation.getDiagnosis();
        this.prescription = consultation.getPrescription();
        this.updatedAt = consultation.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public ConsultationStatus getStatus() {
        return status;
    }

    public void setStatus(ConsultationStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
