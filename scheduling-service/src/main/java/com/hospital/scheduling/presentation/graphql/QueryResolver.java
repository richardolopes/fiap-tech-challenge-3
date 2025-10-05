package com.hospital.scheduling.presentation.graphql;

import com.hospital.scheduling.application.dtos.ConsultationResponse;
import com.hospital.scheduling.application.dtos.UserResponse;
import com.hospital.scheduling.domain.repositories.ConsultationRepository;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.scheduling.infrastructure.security.CustomUserDetails;
import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.enums.ConsultationStatus;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class QueryResolver {

    private final UserRepository userRepository;
    private final ConsultationRepository consultationRepository;

    public QueryResolver(UserRepository userRepository, ConsultationRepository consultationRepository) {
        this.userRepository = userRepository;
        this.consultationRepository = consultationRepository;
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO') or hasAuthority('ROLE_ENFERMEIRO')")
    public List<UserResponse> users() {
        List<User> users = userRepository.findActiveUsers();
        return users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO') or hasAuthority('ROLE_ENFERMEIRO') or hasAuthority('ROLE_PACIENTE')")
    public UserResponse user(@Argument("id") Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserResponse::fromEntity).orElse(null);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO') or hasAuthority('ROLE_ENFERMEIRO')")
    public List<ConsultationResponse> consultations() {
        List<Consultation> consultations = consultationRepository.findAll();
        return consultations.stream()
                .map(ConsultationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO') or hasAuthority('ROLE_ENFERMEIRO')")
    public ConsultationResponse consultation(@Argument("id") Long id, Authentication authentication) {
        Optional<Consultation> consultation = consultationRepository.findById(id);

        if (consultation.isEmpty()) {
            return null;
        }

        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PACIENTE"))) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (!userDetails.getId().equals(consultation.get().getPatientId())) {
                throw new org.springframework.security.access.AccessDeniedException("Access denied");
            }
        }

        return ConsultationResponse.fromEntity(consultation.get());
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO') or hasAuthority('ROLE_ENFERMEIRO') or hasAuthority('ROLE_PACIENTE')")
    public List<ConsultationResponse> patientConsultations(@Argument("patientId") Long patientId, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PACIENTE"))) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (!userDetails.getId().equals(patientId)) {
                throw new org.springframework.security.access.AccessDeniedException("Access denied");
            }
        }

        List<Consultation> consultations = consultationRepository.findByPatientId(patientId);
        return consultations.stream()
                .map(ConsultationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO') or hasAuthority('ROLE_ENFERMEIRO') or hasAuthority('ROLE_PACIENTE')")
    public List<ConsultationResponse> patientHistory(@Argument("patientId") Long patientId, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PACIENTE"))) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (!userDetails.getId().equals(patientId)) {
                throw new org.springframework.security.access.AccessDeniedException("Access denied");
            }
        }

        List<Consultation> consultations = consultationRepository.findByPatientId(patientId);
        return consultations.stream()
                .map(ConsultationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO') or hasAuthority('ROLE_ENFERMEIRO') or hasAuthority('ROLE_PACIENTE')")
    public List<ConsultationResponse> patientUpcomingConsultations(@Argument("patientId") Long patientId, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PACIENTE"))) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (!userDetails.getId().equals(patientId)) {
                throw new org.springframework.security.access.AccessDeniedException("Access denied");
            }
        }

        List<Consultation> consultations = consultationRepository.findFutureConsultationsByPatientId(patientId);
        return consultations.stream()
                .map(ConsultationResponse::fromEntity)
                .collect(Collectors.toList());
    }


    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO') or hasAuthority('ROLE_ENFERMEIRO') or hasAuthority('ROLE_PACIENTE')")
    public List<ConsultationResponse> patientConsultationsByStatus(@Argument("patientId") Long patientId,
                                                                   @Argument("status") ConsultationStatus status,
                                                                   Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PACIENTE"))) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (!userDetails.getId().equals(patientId)) {
                throw new org.springframework.security.access.AccessDeniedException("Access denied");
            }
        }

        List<Consultation> consultations = consultationRepository.findByPatientIdAndStatus(patientId, status);
        return consultations.stream()
                .map(ConsultationResponse::fromEntity)
                .collect(Collectors.toList());
    }

}
