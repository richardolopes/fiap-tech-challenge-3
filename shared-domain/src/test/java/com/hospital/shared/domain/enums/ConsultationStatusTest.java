package com.hospital.shared.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsultationStatus Enum Tests")
class ConsultationStatusTest {

    @Test
    @DisplayName("Should have correct values")
    void shouldHaveCorrectValues() {
        assertEquals(5, ConsultationStatus.values().length);
        assertNotNull(ConsultationStatus.AGENDADA);
        assertNotNull(ConsultationStatus.CONCLUIDA);
        assertNotNull(ConsultationStatus.CANCELADA);
    }

    @Test
    @DisplayName("Should have correct codes and descriptions for AGENDADA")
    void shouldHaveCorrectCodesAndDescriptionsForAgendada() {
        ConsultationStatus agendada = ConsultationStatus.AGENDADA;

        assertEquals("AGENDADA", agendada.getCode());
        assertEquals("Consulta agendada", agendada.getDescription());
    }

    @Test
    @DisplayName("Should have correct codes and descriptions for CONCLUIDA")
    void shouldHaveCorrectCodesAndDescriptionsForConcluida() {
        ConsultationStatus concluida = ConsultationStatus.CONCLUIDA;

        assertEquals("CONCLUIDA", concluida.getCode());
        assertEquals("Consulta concluída", concluida.getDescription());
    }

    @Test
    @DisplayName("Should have correct codes and descriptions for CANCELADA")
    void shouldHaveCorrectCodesAndDescriptionsForCancelada() {
        ConsultationStatus cancelada = ConsultationStatus.CANCELADA;

        assertEquals("CANCELADA", cancelada.getCode());
        assertEquals("Consulta cancelada", cancelada.getDescription());
    }

    @Test
    @DisplayName("Should maintain enum order")
    void shouldMaintainEnumOrder() {
        ConsultationStatus[] values = ConsultationStatus.values();

        assertEquals(ConsultationStatus.AGENDADA, values[0]);
        assertEquals(ConsultationStatus.CONCLUIDA, values[1]);
        assertEquals(ConsultationStatus.CANCELADA, values[2]);
    }

    @Test
    @DisplayName("Should convert from string correctly")
    void shouldConvertFromStringCorrectly() {
        assertEquals(ConsultationStatus.AGENDADA, ConsultationStatus.valueOf("AGENDADA"));
        assertEquals(ConsultationStatus.CONCLUIDA, ConsultationStatus.valueOf("CONCLUIDA"));
        assertEquals(ConsultationStatus.CANCELADA, ConsultationStatus.valueOf("CANCELADA"));
    }

    @Test
    @DisplayName("Should throw exception for invalid string")
    void shouldThrowExceptionForInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> ConsultationStatus.valueOf("INVALID"));
    }
}
