package com.hospital.scheduling.application.usecases;

import com.hospital.scheduling.application.dtos.CreateConsultationRequest;
import com.hospital.scheduling.application.dtos.ConsultationResponse;
import com.hospital.scheduling.domain.repositories.ConsultationRepository;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.scheduling.infrastructure.events.EventPublisher;
import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.enums.ConsultationStatus;
import com.hospital.shared.domain.enums.UserType;
import com.hospital.shared.domain.events.ConsultationCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateConsultationUseCase Tests")
class CreateConsultationUseCaseTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CreateConsultationUseCase createConsultationUseCase;

    private CreateConsultationRequest validRequest;
    private User patient;
    private User doctor;
    private Consultation savedConsultation;

    @BeforeEach
    void setUp() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        
        validRequest = new CreateConsultationRequest(
            1L,  
            2L, 
            scheduledDateTime
        );

        patient = new User("João Silva", "joao@email.com", "password", UserType.PACIENTE);
        patient.setId(1L);
        patient.setCpf("12345678901");

        doctor = new User("Dr. Maria Santos", "maria@email.com", "password", UserType.MEDICO);
        doctor.setId(2L);
        doctor.setCrm("123456");

        savedConsultation = new Consultation(1L, 2L, scheduledDateTime);
        savedConsultation.setId(10L);
    }

    @Nested
    @DisplayName("Successful Consultation Creation Tests")
    class SuccessfulConsultationCreationTests {

        @Test
        @DisplayName("Should create consultation successfully")
        void shouldCreateConsultationSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
            when(consultationRepository.existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime()))
                .thenReturn(false);
            when(consultationRepository.save(any(Consultation.class))).thenReturn(savedConsultation);

            ConsultationResponse response = createConsultationUseCase.execute(validRequest);

            assertNotNull(response);
            assertEquals(savedConsultation.getId(), response.id());
            assertEquals(savedConsultation.getPatientId(), response.patientId());
            assertEquals(savedConsultation.getDoctorId(), response.doctorId());
            assertEquals(savedConsultation.getScheduledDateTime(), response.scheduledDateTime());
            assertEquals(ConsultationStatus.AGENDADA, response.status());
            assertEquals(patient.getName(), response.patientName());
            assertEquals(patient.getEmail(), response.patientEmail());
            assertEquals(doctor.getName(), response.doctorName());

            verify(userRepository).findById(1L);
            verify(userRepository).findById(2L);
            verify(consultationRepository).existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime());
            
            ArgumentCaptor<Consultation> consultationCaptor = ArgumentCaptor.forClass(Consultation.class);
            verify(consultationRepository).save(consultationCaptor.capture());
            
            Consultation capturedConsultation = consultationCaptor.getValue();
            assertEquals(validRequest.patientId(), capturedConsultation.getPatientId());
            assertEquals(validRequest.doctorId(), capturedConsultation.getDoctorId());
            assertEquals(validRequest.scheduledDateTime(), capturedConsultation.getScheduledDateTime());
            assertEquals(ConsultationStatus.AGENDADA, capturedConsultation.getStatus());
        }

        @Test
        @DisplayName("Should publish consultation created event")
        void shouldPublishConsultationCreatedEvent() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
            when(consultationRepository.existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime()))
                .thenReturn(false);
            when(consultationRepository.save(any(Consultation.class))).thenReturn(savedConsultation);

            createConsultationUseCase.execute(validRequest);

            ArgumentCaptor<ConsultationCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ConsultationCreatedEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());

            ConsultationCreatedEvent capturedEvent = eventCaptor.getValue();
            assertEquals(savedConsultation.getId(), capturedEvent.getConsultationId());
            assertEquals(patient.getId(), capturedEvent.getPatientId());
            assertEquals(doctor.getId(), capturedEvent.getDoctorId());
            assertEquals(savedConsultation.getScheduledDateTime(), capturedEvent.getScheduledDateTime());
            assertEquals(patient.getEmail(), capturedEvent.getPatientEmail());
            assertEquals(patient.getName(), capturedEvent.getPatientName());
            assertEquals(doctor.getName(), capturedEvent.getDoctorName());
        }
    }

    @Nested
    @DisplayName("Patient Validation Tests")
    class PatientValidationTests {

        @Test
        @DisplayName("Should throw exception when patient not found")
        void shouldThrowExceptionWhenPatientNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createConsultationUseCase.execute(validRequest)
            );

            assertEquals("Patient not found", exception.getMessage());
            verify(userRepository).findById(1L);
            verify(userRepository, never()).findById(2L);
            verify(consultationRepository, never()).save(any(Consultation.class));
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw exception when specified user is not a patient")
        void shouldThrowExceptionWhenSpecifiedUserIsNotAPatient() {
            User notAPatient = new User("Dr. João", "joao@email.com", "password", UserType.MEDICO);
            notAPatient.setId(1L);
            
            when(userRepository.findById(1L)).thenReturn(Optional.of(notAPatient));

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createConsultationUseCase.execute(validRequest)
            );

            assertEquals("Specified user is not a patient", exception.getMessage());
            verify(userRepository).findById(1L);
            verify(userRepository, never()).findById(2L);
            verify(consultationRepository, never()).save(any(Consultation.class));
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Doctor Validation Tests")
    class DoctorValidationTests {

        @Test
        @DisplayName("Should throw exception when doctor not found")
        void shouldThrowExceptionWhenDoctorNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(userRepository.findById(2L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createConsultationUseCase.execute(validRequest)
            );

            assertEquals("Doctor not found", exception.getMessage());
            verify(userRepository).findById(1L);
            verify(userRepository).findById(2L);
            verify(consultationRepository, never()).save(any(Consultation.class));
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw exception when specified user is not a doctor")
        void shouldThrowExceptionWhenSpecifiedUserIsNotADoctor() {
            User notADoctor = new User("Enfermeira Maria", "maria@email.com", "password", UserType.ENFERMEIRO);
            notADoctor.setId(2L);
            
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(userRepository.findById(2L)).thenReturn(Optional.of(notADoctor));

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createConsultationUseCase.execute(validRequest)
            );

            assertEquals("Specified user is not a doctor", exception.getMessage());
            verify(userRepository).findById(1L);
            verify(userRepository).findById(2L);
            verify(consultationRepository, never()).save(any(Consultation.class));
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Doctor Availability Tests")
    class DoctorAvailabilityTests {

        @Test
        @DisplayName("Should throw exception when doctor already has consultation at same time")
        void shouldThrowExceptionWhenDoctorAlreadyHasConsultationAtSameTime() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
            when(consultationRepository.existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime()))
                .thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createConsultationUseCase.execute(validRequest)
            );

            assertEquals("Doctor already has a consultation scheduled at this time", exception.getMessage());
            verify(userRepository).findById(1L);
            verify(userRepository).findById(2L);
            verify(consultationRepository).existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime());
            verify(consultationRepository, never()).save(any(Consultation.class));
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should proceed when doctor is available at scheduled time")
        void shouldProceedWhenDoctorIsAvailableAtScheduledTime() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
            when(consultationRepository.existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime()))
                .thenReturn(false);
            when(consultationRepository.save(any(Consultation.class))).thenReturn(savedConsultation);

            ConsultationResponse response = createConsultationUseCase.execute(validRequest);

            assertNotNull(response);
            verify(consultationRepository).existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime());
            verify(consultationRepository).save(any(Consultation.class));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should execute complete flow in correct order")
        void shouldExecuteCompleteFlowInCorrectOrder() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
            when(consultationRepository.existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime()))
                .thenReturn(false);
            when(consultationRepository.save(any(Consultation.class))).thenReturn(savedConsultation);

            ConsultationResponse response = createConsultationUseCase.execute(validRequest);

            assertNotNull(response);

            var inOrder = inOrder(userRepository, consultationRepository, eventPublisher);
            inOrder.verify(userRepository).findById(1L);
            inOrder.verify(userRepository).findById(2L);
            inOrder.verify(consultationRepository).existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime());
            inOrder.verify(consultationRepository).save(any(Consultation.class));
            inOrder.verify(eventPublisher).publishEvent(any(ConsultationCreatedEvent.class));
        }

        @Test
        @DisplayName("Should create consultation with correct patient and doctor information")
        void shouldCreateConsultationWithCorrectPatientAndDoctorInformation() {
            User specificPatient = new User("Maria Silva", "maria.silva@email.com", "password", UserType.PACIENTE);
            specificPatient.setId(5L);
            specificPatient.setCpf("98765432100");

            User specificDoctor = new User("Dr. João Santos", "joao.santos@email.com", "password", UserType.MEDICO);
            specificDoctor.setId(6L);
            specificDoctor.setCrm("987654");

            CreateConsultationRequest specificRequest = new CreateConsultationRequest(
                5L, 6L, LocalDateTime.now().plusDays(2)
            );

            Consultation specificSavedConsultation = new Consultation(5L, 6L, specificRequest.scheduledDateTime());
            specificSavedConsultation.setId(20L);

            when(userRepository.findById(5L)).thenReturn(Optional.of(specificPatient));
            when(userRepository.findById(6L)).thenReturn(Optional.of(specificDoctor));
            when(consultationRepository.existsByDoctorIdAndScheduledDateTime(6L, specificRequest.scheduledDateTime()))
                .thenReturn(false);
            when(consultationRepository.save(any(Consultation.class))).thenReturn(specificSavedConsultation);

            ConsultationResponse response = createConsultationUseCase.execute(specificRequest);

            assertEquals(20L, response.id());
            assertEquals(5L, response.patientId());
            assertEquals(6L, response.doctorId());
            assertEquals("Maria Silva", response.patientName());
            assertEquals("maria.silva@email.com", response.patientEmail());
            assertEquals("Dr. João Santos", response.doctorName());
        }

        @Test
        @DisplayName("Should handle repository exceptions appropriately")
        void shouldHandleRepositoryExceptionsAppropriately() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
            when(consultationRepository.existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime()))
                .thenReturn(false);
            when(consultationRepository.save(any(Consultation.class)))
                .thenThrow(new RuntimeException("Database error"));

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createConsultationUseCase.execute(validRequest)
            );

            assertEquals("Database error", exception.getMessage());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should not call event publisher if consultation save fails")
        void shouldNotCallEventPublisherIfConsultationSaveFails() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
            when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
            when(consultationRepository.existsByDoctorIdAndScheduledDateTime(2L, validRequest.scheduledDateTime()))
                .thenReturn(false);
            when(consultationRepository.save(any(Consultation.class)))
                .thenThrow(new RuntimeException("Save failed"));

            assertThrows(RuntimeException.class, () -> createConsultationUseCase.execute(validRequest));
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null request gracefully")
        void shouldHandleNullRequestGracefully() {
            assertThrows(NullPointerException.class, () -> createConsultationUseCase.execute(null));
            verifyNoInteractions(userRepository, consultationRepository, eventPublisher);
        }

        @Test
        @DisplayName("Should handle patient and doctor with same ID but different types")
        void shouldHandlePatientAndDoctorWithSameIdButDifferentTypes() {
            CreateConsultationRequest sameIdRequest = new CreateConsultationRequest(
                1L, 1L, LocalDateTime.now().plusDays(1)
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createConsultationUseCase.execute(sameIdRequest)
            );

            assertEquals("Specified user is not a doctor", exception.getMessage());
            verify(userRepository, times(2)).findById(1L);
        }
    }
}