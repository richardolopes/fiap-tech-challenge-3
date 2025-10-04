package com.hospital.scheduling.presentation.controllers;

import com.hospital.scheduling.application.dtos.CancelConsultationRequest;
import com.hospital.scheduling.application.dtos.ConsultationResponse;
import com.hospital.scheduling.application.dtos.CreateConsultationRequest;
import com.hospital.scheduling.application.dtos.UpdateConsultationRequest;
import com.hospital.scheduling.application.usecases.CancelConsultationUseCase;
import com.hospital.scheduling.application.usecases.CreateConsultationUseCase;
import com.hospital.scheduling.application.usecases.UpdateConsultationUseCase;
import com.hospital.scheduling.domain.repositories.ConsultationRepository;
import com.hospital.shared.domain.entities.Consultation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/consultations")
@CrossOrigin(origins = "*")
public class ConsultationController {
    private final CreateConsultationUseCase createConsultationUseCase;
    private final UpdateConsultationUseCase updateConsultationUseCase;
    private final CancelConsultationUseCase cancelConsultationUseCase;
    private final ConsultationRepository consultationRepository;

    public ConsultationController(
            CreateConsultationUseCase createConsultationUseCase,
            UpdateConsultationUseCase updateConsultationUseCase,
            CancelConsultationUseCase cancelConsultationUseCase,
            ConsultationRepository consultationRepository
    ) {
        this.createConsultationUseCase = createConsultationUseCase;
        this.updateConsultationUseCase = updateConsultationUseCase;
        this.cancelConsultationUseCase = cancelConsultationUseCase;
        this.consultationRepository = consultationRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('MEDICO') or hasRole('ENFERMEIRO')")
    public ResponseEntity<ConsultationResponse> createConsultation(
            @Valid @RequestBody CreateConsultationRequest request) {

        try {
            ConsultationResponse response = createConsultationUseCase.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('MEDICO') or hasRole('ENFERMEIRO')")
    public ResponseEntity<List<ConsultationResponse>> getAllConsultations() {

        try {
            List<Consultation> consultations = consultationRepository.findAll();
            List<ConsultationResponse> responses = consultations.stream()
                    .map(ConsultationResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ENFERMEIRO') or (hasRole('PACIENTE') and @customSecurityService.canAccessConsultation(#id, authentication))")
    public ResponseEntity<ConsultationResponse> getConsultationById(@PathVariable("id") Long id) {

        try {
            Optional<Consultation> consultation = consultationRepository.findById(id);

            if (consultation.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ConsultationResponse response = ConsultationResponse.fromEntity(consultation.get());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ENFERMEIRO')")
    public ResponseEntity<ConsultationResponse> updateConsultation(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateConsultationRequest request) {

        try {
            ConsultationResponse response = updateConsultationUseCase.execute(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ENFERMEIRO')")
    public ResponseEntity<Void> cancelConsultation(
            @PathVariable("id") Long id,
            @RequestBody(required = false) CancelConsultationRequest request) {

        try {
            String reason = request != null ? request.reason() : "Consulta cancelada";
            cancelConsultationUseCase.execute(id, reason);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ENFERMEIRO') or (hasRole('PACIENTE') and @customSecurityService.isOwnerOrAuthorized(#patientId, authentication))")
    public ResponseEntity<List<ConsultationResponse>> getConsultationsByPatient(@PathVariable("patientId") Long patientId) {

        try {
            List<Consultation> consultations = consultationRepository.findByPatientId(patientId);
            List<ConsultationResponse> responses = consultations.stream()
                    .map(ConsultationResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
