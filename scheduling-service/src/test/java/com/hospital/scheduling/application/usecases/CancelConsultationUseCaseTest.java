package com.hospital.scheduling.application.usecases;

import com.hospital.scheduling.domain.repositories.ConsultationRepository;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.scheduling.infrastructure.events.EventPublisher;
import com.hospital.shared.domain.entities.Consultation;
import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.enums.ConsultationStatus;
import com.hospital.shared.domain.enums.UserType;
import com.hospital.shared.domain.events.ConsultationCancelledEvent;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelConsultationUseCase Tests")
class CancelConsultationUseCaseTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CancelConsultationUseCase cancelConsultationUseCase;

    private Consultation consultation;
    private User patient;

    @BeforeEach
    void setUp() {
        patient = new User("Paciente Teste", "paciente@email.com", "password", UserType.PACIENTE);
        patient.setId(1L);
        patient.setCpf("12345678901");

        consultation = new Consultation(1L, 2L, LocalDateTime.now().plusDays(1));
        consultation.setId(1L);
    }

    @Nested
    @DisplayName("Successful Cancellation Tests")
    class SuccessfulCancellationTests {

        @Test
        @DisplayName("Should cancel consultation successfully with reason")
        void shouldCancelConsultationSuccessfullyWithReason() {
            String reason = "Paciente doente";
            when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));

            cancelConsultationUseCase.execute(1L, reason);

            assertEquals(ConsultationStatus.CANCELADA, consultation.getStatus());
            assertThat(consultation.getNotes()).isEqualTo(reason);
            
            verify(consultationRepository).save(consultation);
            
            ArgumentCaptor<ConsultationCancelledEvent> eventCaptor = ArgumentCaptor.forClass(ConsultationCancelledEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            
            ConsultationCancelledEvent capturedEvent = eventCaptor.getValue();
            assertEquals(1L, capturedEvent.getConsultationId());
            assertEquals(1L, capturedEvent.getPatientId());
            assertEquals(reason, capturedEvent.getReason());
            assertEquals("paciente@email.com", capturedEvent.getPatientEmail());
            assertEquals("Paciente Teste", capturedEvent.getPatientName());
        }

        @Test
        @DisplayName("Should cancel consultation successfully with default reason when reason is null")
        void shouldCancelConsultationSuccessfullyWithDefaultReasonWhenReasonIsNull() {
            when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));

            cancelConsultationUseCase.execute(1L, null);

            assertEquals(ConsultationStatus.CANCELADA, consultation.getStatus());
            assertEquals("Consulta cancelada", consultation.getNotes());
            
            verify(consultationRepository).save(consultation);
            
            ArgumentCaptor<ConsultationCancelledEvent> eventCaptor = ArgumentCaptor.forClass(ConsultationCancelledEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            
            ConsultationCancelledEvent capturedEvent = eventCaptor.getValue();
            assertEquals("Consulta cancelada", capturedEvent.getReason());
        }

        @Test
        @DisplayName("Should cancel consultation successfully with default reason when reason is empty")
        void shouldCancelConsultationSuccessfullyWithDefaultReasonWhenReasonIsEmpty() {
            when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));

            cancelConsultationUseCase.execute(1L, "");

            assertEquals(ConsultationStatus.CANCELADA, consultation.getStatus());
            
            verify(consultationRepository).save(consultation);
            verify(eventPublisher).publishEvent(any(ConsultationCancelledEvent.class));
        }
    }

    @Nested
    @DisplayName("Consultation Validation Tests")
    class ConsultationValidationTests {

        @Test
        @DisplayName("Should throw exception when consultation not found")
        void shouldThrowExceptionWhenConsultationNotFound() {
            when(consultationRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> cancelConsultationUseCase.execute(999L, "Motivo qualquer")
            );

            assertEquals("Consultation not found", exception.getMessage());
            verify(userRepository, never()).findById(any());
            verify(consultationRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Patient Validation Tests")
    class PatientValidationTests {

        @Test
        @DisplayName("Should throw exception when patient not found")
        void shouldThrowExceptionWhenPatientNotFound() {
            when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> cancelConsultationUseCase.execute(1L, "Motivo qualquer")
            );

            assertEquals("Patient not found", exception.getMessage());
            verify(consultationRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should execute all operations in correct order")
        void shouldExecuteAllOperationsInCorrectOrder() {
            String reason = "Emergência familiar";
            when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));

            cancelConsultationUseCase.execute(1L, reason);

            
            verify(consultationRepository).findById(1L);
            verify(userRepository).findById(1L);
            verify(consultationRepository).save(consultation);
            verify(eventPublisher).publishEvent(any(ConsultationCancelledEvent.class));
        }

        @Test
        @DisplayName("Should publish event with correct consultation and patient data")
        void shouldPublishEventWithCorrectConsultationAndPatientData() {
            String reason = "Conflito de agenda";
            when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));

            cancelConsultationUseCase.execute(1L, reason);

            ArgumentCaptor<ConsultationCancelledEvent> eventCaptor = ArgumentCaptor.forClass(ConsultationCancelledEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());

            ConsultationCancelledEvent event = eventCaptor.getValue();
            assertAll(
                    () -> assertEquals(consultation.getId(), event.getConsultationId()),
                    () -> assertEquals(patient.getId(), event.getPatientId()),
                    () -> assertEquals(reason, event.getReason()),
                    () -> assertEquals(patient.getEmail(), event.getPatientEmail()),
                    () -> assertEquals(patient.getName(), event.getPatientName())
            );
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle consultation with different patient ID")
        void shouldHandleConsultationWithDifferentPatientId() {
            Consultation consultationWithDifferentPatient = new Consultation(
                    5L,
                    2L,
                    LocalDateTime.now().plusDays(1)
            );
            consultationWithDifferentPatient.setId(2L);

            User differentPatient = new User("Outro Paciente", "outro@email.com", "password", UserType.PACIENTE);
            differentPatient.setId(5L);

            when(consultationRepository.findById(2L)).thenReturn(Optional.of(consultationWithDifferentPatient));
            when(userRepository.findById(5L)).thenReturn(Optional.of(differentPatient));

            cancelConsultationUseCase.execute(2L, "Motivo especial");

            ArgumentCaptor<ConsultationCancelledEvent> eventCaptor = ArgumentCaptor.forClass(ConsultationCancelledEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());

            ConsultationCancelledEvent event = eventCaptor.getValue();
            assertEquals(5L, event.getPatientId());
            assertEquals("outro@email.com", event.getPatientEmail());
            assertEquals("Outro Paciente", event.getPatientName());
        }

        @Test
        @DisplayName("Should handle long cancellation reason")
        void shouldHandleLongCancellationReason() {
            String longReason = "Este é um motivo muito longo para cancelamento que pode conter muitos detalhes sobre a situação que levou ao cancelamento da consulta médica";
            when(consultationRepository.findById(1L)).thenReturn(Optional.of(consultation));
            when(userRepository.findById(1L)).thenReturn(Optional.of(patient));

            assertDoesNotThrow(() -> cancelConsultationUseCase.execute(1L, longReason));

            assertEquals(longReason, consultation.getNotes());
        }
    }
}
