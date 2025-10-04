package com.hospital.shared.domain.entities;

import com.hospital.shared.domain.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create user with valid data")
        void shouldCreateUserWithValidData() {
            String name = "João Silva";
            String email = "joao@email.com";
            String password = "password123";
            UserType userType = UserType.MEDICO;

            User user = new User(name, email, password, userType);

            assertNotNull(user);
            assertEquals("João Silva", user.getName());
            assertEquals("joao@email.com", user.getEmail());
            assertEquals(password, user.getPassword());
            assertEquals(UserType.MEDICO, user.getUserType());
            assertTrue(user.isActive());
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create user with all parameters")
        void shouldCreateUserWithAllParameters() {
            Long id = 1L;
            String name = "Maria Santos";
            String email = "maria@email.com";
            String password = "encrypted_password";
            UserType userType = UserType.ENFERMEIRO;
            String crm = null;
            String coren = "123456";
            String cpf = null;
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();
            boolean active = true;

            User user = new User(id, name, email, password, userType, crm, coren, cpf, createdAt, updatedAt, active);

            assertEquals(id, user.getId());
            assertEquals("Maria Santos", user.getName());
            assertEquals("maria@email.com", user.getEmail());
            assertEquals(password, user.getPassword());
            assertEquals(UserType.ENFERMEIRO, user.getUserType());
            assertEquals(coren, user.getCoren());
            assertEquals(createdAt, user.getCreatedAt());
            assertEquals(updatedAt, user.getUpdatedAt());
            assertTrue(user.isActive());
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(null, "email@test.com", "password", UserType.PACIENTE)
            );
            
            assertEquals("Nome não pode ser vazio", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("", "email@test.com", "password", UserType.PACIENTE)
            );
            
            assertEquals("Nome não pode ser vazio", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when name is too short")
        void shouldThrowExceptionWhenNameIsTooShort() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("A", "email@test.com", "password", UserType.PACIENTE)
            );
            
            assertEquals("Nome deve ter pelo menos 2 caracteres", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("João Silva", null, "password", UserType.PACIENTE)
            );
            
            assertEquals("Email não pode ser vazio", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when email is invalid")
        void shouldThrowExceptionWhenEmailIsInvalid() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("João Silva", "invalid-email", "password", UserType.PACIENTE)
            );
            
            assertEquals("Email deve ter formato válido", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when userType is null")
        void shouldThrowExceptionWhenUserTypeIsNull() {
            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new User("João Silva", "joao@email.com", "password", null)
            );
            
            assertEquals("Tipo de usuário é obrigatório", exception.getMessage());
        }

        @Test
        @DisplayName("Should trim and lowercase email")
        void shouldTrimAndLowercaseEmail() {
            String email = "  JOAO@EMAIL.COM  ";

            User user = new User("João Silva", email, "password", UserType.PACIENTE);

            assertEquals("joao@email.com", user.getEmail());
        }

        @Test
        @DisplayName("Should trim name")
        void shouldTrimName() {
            String name = "  João Silva  ";

            User user = new User(name, "joao@email.com", "password", UserType.PACIENTE);

            assertEquals("João Silva", user.getName());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should update user info")
        void shouldUpdateUserInfo() {
            User user = new User("João Silva", "joao@email.com", "password", UserType.PACIENTE);
            LocalDateTime originalUpdatedAt = user.getUpdatedAt();

            user.updateInfo("João Santos", "joao.santos@email.com");

            assertEquals("João Santos", user.getName());
            assertEquals("joao.santos@email.com", user.getEmail());
        }

        @Test
        @DisplayName("Should set CRM for doctor")
        void shouldSetCrmForDoctor() {
            User doctor = new User("Dr. João", "joao@email.com", "password", UserType.MEDICO);

            doctor.setCrm("123456");

            assertEquals("123456", doctor.getCrm());
        }

        @Test
        @DisplayName("Should throw exception when setting CRM for non-doctor")
        void shouldThrowExceptionWhenSettingCrmForNonDoctor() {
            User patient = new User("João", "joao@email.com", "password", UserType.PACIENTE);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patient.setCrm("123456")
            );
            
            assertEquals("CRM só pode ser definido para médicos", exception.getMessage());
        }

        @Test
        @DisplayName("Should set COREN for nurse")
        void shouldSetCorenForNurse() {
            User nurse = new User("Enfermeira Maria", "maria@email.com", "password", UserType.ENFERMEIRO);

            nurse.setCoren("654321");

            assertEquals("654321", nurse.getCoren());
        }

        @Test
        @DisplayName("Should throw exception when setting COREN for non-nurse")
        void shouldThrowExceptionWhenSettingCorenForNonNurse() {
            User doctor = new User("Dr. João", "joao@email.com", "password", UserType.MEDICO);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctor.setCoren("654321")
            );
            
            assertEquals("COREN só pode ser definido para enfermeiros", exception.getMessage());
        }

        @Test
        @DisplayName("Should set CPF for patient")
        void shouldSetCpfForPatient() {
            User patient = new User("João", "joao@email.com", "password", UserType.PACIENTE);

            patient.setCpf("12345678901");

            assertEquals("12345678901", patient.getCpf());
        }

        @Test
        @DisplayName("Should throw exception when setting CPF for non-patient")
        void shouldThrowExceptionWhenSettingCpfForNonPatient() {
            User doctor = new User("Dr. João", "joao@email.com", "password", UserType.MEDICO);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctor.setCpf("12345678901")
            );
            
            assertEquals("CPF só pode ser definido para pacientes", exception.getMessage());
        }

        @Test
        @DisplayName("Should deactivate user")
        void shouldDeactivateUser() {
            User user = new User("João", "joao@email.com", "password", UserType.PACIENTE);
            assertTrue(user.isActive());

            user.deactivate();

            assertFalse(user.isActive());
        }

        @Test
        @DisplayName("Should activate user")
        void shouldActivateUser() {
            User user = new User(1L, "João", "joao@email.com", "password", UserType.PACIENTE,
                null, null, "123456789", LocalDateTime.now(), LocalDateTime.now(), false);
            assertFalse(user.isActive());

            user.activate();

            assertTrue(user.isActive());
        }

        @Test
        @DisplayName("Doctor should be able to view all consultations")
        void doctorShouldBeAbleToViewAllConsultations() {
            User doctor = new User("Dr. João", "joao@email.com", "password", UserType.MEDICO);

            assertTrue(doctor.canViewAllConsultations());
        }

        @Test
        @DisplayName("Nurse should be able to view all consultations")
        void nurseShouldBeAbleToViewAllConsultations() {
            User nurse = new User("Enfermeira Maria", "maria@email.com", "password", UserType.ENFERMEIRO);

            assertTrue(nurse.canViewAllConsultations());
        }

        @Test
        @DisplayName("Patient should not be able to view all consultations")
        void patientShouldNotBeAbleToViewAllConsultations() {
            User patient = new User("João", "joao@email.com", "password", UserType.PACIENTE);

            assertFalse(patient.canViewAllConsultations());
        }

        @Test
        @DisplayName("Doctor should be able to edit consultations")
        void doctorShouldBeAbleToEditConsultations() {
            User doctor = new User("Dr. João", "joao@email.com", "password", UserType.MEDICO);

            assertTrue(doctor.canEditConsultations());
        }

        @Test
        @DisplayName("Nurse should be able to edit consultations")
        void nurseShouldBeAbleToEditConsultations() {
            User nurse = new User("Enfermeira Maria", "maria@email.com", "password", UserType.ENFERMEIRO);

            assertTrue(nurse.canEditConsultations());
        }

        @Test
        @DisplayName("Patient should not be able to edit consultations")
        void patientShouldNotBeAbleToEditConsultations() {
            User patient = new User("João", "joao@email.com", "password", UserType.PACIENTE);

            assertFalse(patient.canEditConsultations());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when id and email are the same")
        void shouldBeEqualWhenIdAndEmailAreTheSame() {
            User user1 = new User(1L, "João", "joao@email.com", "password", UserType.PACIENTE,
                null, null, "123", LocalDateTime.now(), LocalDateTime.now(), true);
            User user2 = new User(1L, "Maria", "joao@email.com", "different_password", UserType.MEDICO,
                "456", null, null, LocalDateTime.now(), LocalDateTime.now(), false);

            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            User user1 = new User(1L, "João", "joao@email.com", "password", UserType.PACIENTE,
                null, null, "123", LocalDateTime.now(), LocalDateTime.now(), true);
            User user2 = new User(2L, "João", "joao@email.com", "password", UserType.PACIENTE,
                null, null, "123", LocalDateTime.now(), LocalDateTime.now(), true);

            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Should not be equal when emails are different")
        void shouldNotBeEqualWhenEmailsAreDifferent() {
            User user1 = new User(1L, "João", "joao@email.com", "password", UserType.PACIENTE,
                null, null, "123", LocalDateTime.now(), LocalDateTime.now(), true);
            User user2 = new User(1L, "João", "maria@email.com", "password", UserType.PACIENTE,
                null, null, "123", LocalDateTime.now(), LocalDateTime.now(), true);

            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            User user = new User("João", "joao@email.com", "password", UserType.PACIENTE);

            assertNotEquals(user, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            User user = new User("João", "joao@email.com", "password", UserType.PACIENTE);
            String notAUser = "not a user";

            assertNotEquals(user, notAUser);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            User user = new User("João", "joao@email.com", "password", UserType.PACIENTE);

            assertEquals(user, user);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return formatted string representation")
        void shouldReturnFormattedStringRepresentation() {
            User user = new User(1L, "João Silva", "joao@email.com", "password", UserType.MEDICO,
                "123456", null, null, LocalDateTime.now(), LocalDateTime.now(), true);

            String result = user.toString();

            assertTrue(result.contains("User{"));
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("name='João Silva'"));
            assertTrue(result.contains("email='joao@email.com'"));
            assertTrue(result.contains("userType=MEDICO"));
            assertTrue(result.contains("active=true"));
        }
    }
}