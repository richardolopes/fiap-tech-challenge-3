package com.hospital.scheduling.infrastructure.security;

import com.hospital.scheduling.domain.repositories.ConsultationRepository;
import com.hospital.shared.domain.entities.Consultation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service("customSecurityService")
public class CustomSecurityService {

    private final ConsultationRepository consultationRepository;

    public CustomSecurityService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }


    public boolean isOwnerOrAuthorized(Long patientId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return patientId.equals(userDetails.getId());
        }

        return false;
    }

    public boolean canAccessConsultation(Long consultationId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return false;
        }

        boolean isMedicalStaff = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MEDICO") || auth.getAuthority().equals("ROLE_ENFERMEIRO"));

        if (isMedicalStaff) {
            return true;
        }

        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PACIENTE"))) {
            Optional<Consultation> consultation = consultationRepository.findById(consultationId);
            return consultation.isPresent() && userDetails.getId().equals(consultation.get().getPatientId());
        }

        return false;
    }
}
