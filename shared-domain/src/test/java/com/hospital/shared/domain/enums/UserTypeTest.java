package com.hospital.shared.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserType Enum Tests")
class UserTypeTest {

    @Test
    @DisplayName("Should have correct values")
    void shouldHaveCorrectValues() {
        assertEquals(3, UserType.values().length);
        assertNotNull(UserType.MEDICO);
        assertNotNull(UserType.ENFERMEIRO);
        assertNotNull(UserType.PACIENTE);
    }

    @Test
    @DisplayName("Should have correct codes and descriptions for MEDICO")
    void shouldHaveCorrectCodesAndDescriptionsForMedico() {
        UserType medico = UserType.MEDICO;

        assertEquals("MEDICO", medico.getCode());
        assertEquals("Médico", medico.getDescription());
    }

    @Test
    @DisplayName("Should have correct codes and descriptions for ENFERMEIRO")
    void shouldHaveCorrectCodesAndDescriptionsForEnfermeiro() {
        UserType enfermeiro = UserType.ENFERMEIRO;

        assertEquals("ENFERMEIRO", enfermeiro.getCode());
        assertEquals("Enfermeiro", enfermeiro.getDescription());
    }

    @Test
    @DisplayName("Should have correct codes and descriptions for PACIENTE")
    void shouldHaveCorrectCodesAndDescriptionsForPaciente() {
        UserType paciente = UserType.PACIENTE;

        assertEquals("PACIENTE", paciente.getCode());
        assertEquals("Paciente", paciente.getDescription());
    }

    @Test
    @DisplayName("Should maintain enum order")
    void shouldMaintainEnumOrder() {
        UserType[] values = UserType.values();

        assertEquals(UserType.MEDICO, values[0]);
        assertEquals(UserType.ENFERMEIRO, values[1]);
        assertEquals(UserType.PACIENTE, values[2]);
    }

    @Test
    @DisplayName("Should convert from string correctly")
    void shouldConvertFromStringCorrectly() {
        assertEquals(UserType.MEDICO, UserType.valueOf("MEDICO"));
        assertEquals(UserType.ENFERMEIRO, UserType.valueOf("ENFERMEIRO"));
        assertEquals(UserType.PACIENTE, UserType.valueOf("PACIENTE"));
    }

    @Test
    @DisplayName("Should throw exception for invalid string")
    void shouldThrowExceptionForInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> UserType.valueOf("INVALID"));
    }
}
