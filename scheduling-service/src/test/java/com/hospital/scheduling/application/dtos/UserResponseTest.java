package com.hospital.scheduling.application.dtos;

import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserResponse DTO Tests")
class UserResponseTest {

    @Test
    @DisplayName("Should create UserResponse with all parameters")
    void shouldCreateUserResponseWithAllParameters() {
        Long id = 1L;
        String name = "Dr. João Silva";
        String email = "joao@email.com";
        UserType userType = UserType.MEDICO;
        String crm = "123456";
        String coren = null;
        String cpf = null;
        boolean active = true;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        UserResponse response = new UserResponse(id, name, email, userType, crm, coren, cpf, active, createdAt, updatedAt);

        assertEquals(id, response.id());
        assertEquals(name, response.name());
        assertEquals(email, response.email());
        assertEquals(userType, response.userType());
        assertEquals(crm, response.crm());
        assertNull(response.coren());
        assertNull(response.cpf());
        assertTrue(response.active());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }

    @Test
    @DisplayName("Should create UserResponse from User entity")
    void shouldCreateUserResponseFromUserEntity() {
        User user = new User("Dr. Maria Santos", "maria@email.com", "password", UserType.MEDICO);
        user.setId(2L);
        user.setCrm("654321");

        UserResponse response = UserResponse.fromEntity(user);

        assertEquals(user.getId(), response.id());
        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getUserType(), response.userType());
        assertEquals(user.getCrm(), response.crm());
        assertEquals(user.getCoren(), response.coren());
        assertEquals(user.getCpf(), response.cpf());
        assertEquals(user.isActive(), response.active());
        assertEquals(user.getCreatedAt(), response.createdAt());
        assertEquals(user.getUpdatedAt(), response.updatedAt());
    }

    @Test
    @DisplayName("Should create UserResponse for doctor from entity")
    void shouldCreateUserResponseForDoctorFromEntity() {
        User doctor = new User("Dr. João", "joao@email.com", "password", UserType.MEDICO);
        doctor.setId(1L);
        doctor.setCrm("123456");

        UserResponse response = UserResponse.fromEntity(doctor);

        assertEquals(UserType.MEDICO, response.userType());
        assertEquals("123456", response.crm());
        assertNull(response.coren());
        assertNull(response.cpf());
    }

    @Test
    @DisplayName("Should create UserResponse for nurse from entity")
    void shouldCreateUserResponseForNurseFromEntity() {
        User nurse = new User("Enfermeira Ana", "ana@email.com", "password", UserType.ENFERMEIRO);
        nurse.setId(2L);
        nurse.setCoren("789123");

        UserResponse response = UserResponse.fromEntity(nurse);

        assertEquals(UserType.ENFERMEIRO, response.userType());
        assertNull(response.crm());
        assertEquals("789123", response.coren());
        assertNull(response.cpf());
    }

    @Test
    @DisplayName("Should create UserResponse for patient from entity")
    void shouldCreateUserResponseForPatientFromEntity() {
        User patient = new User("José Silva", "jose@email.com", "password", UserType.PACIENTE);
        patient.setId(3L);
        patient.setCpf("12345678901");

        UserResponse response = UserResponse.fromEntity(patient);

        assertEquals(UserType.PACIENTE, response.userType());
        assertNull(response.crm());
        assertNull(response.coren());
        assertEquals("12345678901", response.cpf());
    }

    @Test
    @DisplayName("Should handle inactive user from entity")
    void shouldHandleInactiveUserFromEntity() {
        User inactiveUser = new User(1L, "Inactive User", "inactive@email.com", "password", 
            UserType.PACIENTE, null, null, "12345678901", 
            LocalDateTime.now().minusDays(1), LocalDateTime.now(), false);

        UserResponse response = UserResponse.fromEntity(inactiveUser);

        assertFalse(response.active());
        assertEquals("Inactive User", response.name());
        assertEquals("inactive@email.com", response.email());
    }

    @Test
    @DisplayName("Should handle null optional fields from entity")
    void shouldHandleNullOptionalFieldsFromEntity() {
        User user = new User("Test User", "test@email.com", "password", UserType.PACIENTE);
        user.setId(4L);
 
        UserResponse response = UserResponse.fromEntity(user);

        assertNull(response.crm());
        assertNull(response.coren());
        assertNull(response.cpf());
    }

    @Test
    @DisplayName("Should be equal when all fields are the same")
    void shouldBeEqualWhenAllFieldsAreTheSame() {
        LocalDateTime now = LocalDateTime.now();
        UserResponse response1 = new UserResponse(1L, "Test User", "test@email.com", 
            UserType.PACIENTE, null, null, "12345678901", true, now, now);
        UserResponse response2 = new UserResponse(1L, "Test User", "test@email.com", 
            UserType.PACIENTE, null, null, "12345678901", true, now, now);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when fields are different")
    void shouldNotBeEqualWhenFieldsAreDifferent() {
        LocalDateTime now = LocalDateTime.now();
        UserResponse response1 = new UserResponse(1L, "User One", "user1@email.com", 
            UserType.MEDICO, "123456", null, null, true, now, now);
        UserResponse response2 = new UserResponse(2L, "User Two", "user2@email.com", 
            UserType.PACIENTE, null, null, "98765432100", true, now, now);

        assertNotEquals(response1, response2);
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        LocalDateTime now = LocalDateTime.now();
        UserResponse response = new UserResponse(1L, "Dr. João", "joao@email.com", 
            UserType.MEDICO, "123456", null, null, true, now, now);

        String result = response.toString();

        assertTrue(result.contains("UserResponse"));
        assertTrue(result.contains("Dr. João"));
        assertTrue(result.contains("joao@email.com"));
        assertTrue(result.contains("MEDICO"));
        assertTrue(result.contains("123456"));
    }

    @Test
    @DisplayName("Should preserve all entity data when converting from entity")
    void shouldPreserveAllEntityDataWhenConvertingFromEntity() {
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        User user = new User(10L, "Complete User", "complete@email.com", "password", 
            UserType.MEDICO, "999999", null, null, createdAt, updatedAt, true);

        UserResponse response = UserResponse.fromEntity(user);

        assertEquals(10L, response.id());
        assertEquals("Complete User", response.name());
        assertEquals("complete@email.com", response.email());
        assertEquals(UserType.MEDICO, response.userType());
        assertEquals("999999", response.crm());
        assertNull(response.coren());
        assertNull(response.cpf());
        assertTrue(response.active());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }
}
