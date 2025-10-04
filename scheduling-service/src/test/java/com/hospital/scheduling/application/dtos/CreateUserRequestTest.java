package com.hospital.scheduling.application.dtos;

import com.hospital.shared.domain.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateUserRequest DTO Tests")
class CreateUserRequestTest {

    @Test
    @DisplayName("Should create CreateUserRequest with all parameters")
    void shouldCreateCreateUserRequestWithAllParameters() {
        String name = "Dr. João Silva";
        String email = "joao@email.com";
        String password = "password123";
        UserType userType = UserType.MEDICO;
        String crm = "123456";
        String coren = null;
        String cpf = null;

        CreateUserRequest request = new CreateUserRequest(name, email, password, userType, crm, coren, cpf);

        assertEquals(name, request.name());
        assertEquals(email, request.email());
        assertEquals(password, request.password());
        assertEquals(userType, request.userType());
        assertEquals(crm, request.crm());
        assertNull(request.coren());
        assertNull(request.cpf());
    }

    @Test
    @DisplayName("Should create CreateUserRequest for doctor")
    void shouldCreateCreateUserRequestForDoctor() {
        CreateUserRequest request = new CreateUserRequest(
            "Dr. Maria Santos",
            "maria@email.com",
            "password123",
            UserType.MEDICO,
            "654321",
            null,
            null
        );

        assertEquals("Dr. Maria Santos", request.name());
        assertEquals("maria@email.com", request.email());
        assertEquals("password123", request.password());
        assertEquals(UserType.MEDICO, request.userType());
        assertEquals("654321", request.crm());
        assertNull(request.coren());
        assertNull(request.cpf());
    }

    @Test
    @DisplayName("Should create CreateUserRequest for nurse")
    void shouldCreateCreateUserRequestForNurse() {
        CreateUserRequest request = new CreateUserRequest(
            "Enfermeira Ana",
            "ana@email.com",
            "password123",
            UserType.ENFERMEIRO,
            null,
            "789123",
            null
        );

        assertEquals("Enfermeira Ana", request.name());
        assertEquals("ana@email.com", request.email());
        assertEquals("password123", request.password());
        assertEquals(UserType.ENFERMEIRO, request.userType());
        assertNull(request.crm());
        assertEquals("789123", request.coren());
        assertNull(request.cpf());
    }

    @Test
    @DisplayName("Should create CreateUserRequest for patient")
    void shouldCreateCreateUserRequestForPatient() {
        CreateUserRequest request = new CreateUserRequest(
            "José Silva",
            "jose@email.com",
            "password123",
            UserType.PACIENTE,
            null,
            null,
            "12345678901"
        );

        assertEquals("José Silva", request.name());
        assertEquals("jose@email.com", request.email());
        assertEquals("password123", request.password());
        assertEquals(UserType.PACIENTE, request.userType());
        assertNull(request.crm());
        assertNull(request.coren());
        assertEquals("12345678901", request.cpf());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void shouldHandleNullOptionalFields() {
        CreateUserRequest request = new CreateUserRequest(
            "Test User",
            "test@email.com",
            "password123",
            UserType.PACIENTE,
            null,
            null,
            null
        );

        assertEquals("Test User", request.name());
        assertEquals("test@email.com", request.email());
        assertEquals("password123", request.password());
        assertEquals(UserType.PACIENTE, request.userType());
        assertNull(request.crm());
        assertNull(request.coren());
        assertNull(request.cpf());
    }

    @Test
    @DisplayName("Should be equal when all fields are the same")
    void shouldBeEqualWhenAllFieldsAreTheSame() {
        CreateUserRequest request1 = new CreateUserRequest(
            "Dr. João",
            "joao@email.com",
            "password123",
            UserType.MEDICO,
            "123456",
            null,
            null
        );

        CreateUserRequest request2 = new CreateUserRequest(
            "Dr. João",
            "joao@email.com",
            "password123",
            UserType.MEDICO,
            "123456",
            null,
            null
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when fields are different")
    void shouldNotBeEqualWhenFieldsAreDifferent() {
        CreateUserRequest request1 = new CreateUserRequest(
            "Dr. João",
            "joao@email.com",
            "password123",
            UserType.MEDICO,
            "123456",
            null,
            null
        );

        CreateUserRequest request2 = new CreateUserRequest(
            "Dr. Maria",
            "maria@email.com",
            "password456",
            UserType.ENFERMEIRO,
            null,
            "789123",
            null
        );

        assertNotEquals(request1, request2);
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        CreateUserRequest request = new CreateUserRequest(
            "Dr. João",
            "joao@email.com",
            "password123",
            UserType.MEDICO,
            "123456",
            null,
            null
        );

        String result = request.toString();

        assertTrue(result.contains("CreateUserRequest"));
        assertTrue(result.contains("Dr. João"));
        assertTrue(result.contains("joao@email.com"));
        assertTrue(result.contains("MEDICO"));
        assertTrue(result.contains("123456"));
    }
}
