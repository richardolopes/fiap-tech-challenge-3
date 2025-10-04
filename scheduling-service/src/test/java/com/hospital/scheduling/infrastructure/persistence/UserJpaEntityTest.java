package com.hospital.scheduling.infrastructure.persistence;

import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserJpaEntity Tests")
class UserJpaEntityTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty UserJpaEntity with default constructor")
        void shouldCreateEmptyUserJpaEntityWithDefaultConstructor() {
            UserJpaEntity entity = new UserJpaEntity();

            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getName());
            assertNull(entity.getEmail());
            assertNull(entity.getPassword());
            assertNull(entity.getUserType());
            assertNull(entity.getCrm());
            assertNull(entity.getCoren());
            assertNull(entity.getCpf());
            assertNull(entity.getCreatedAt());
            assertNull(entity.getUpdatedAt());
            assertTrue(entity.isActive());
        }

        @Test
        @DisplayName("Should create UserJpaEntity from User domain entity")
        void shouldCreateUserJpaEntityFromUserDomainEntity() {
            User user = new User("Dr. João Silva", "joao@email.com", "password123", UserType.MEDICO);
            user.setId(1L);
            user.setCrm("123456");

            UserJpaEntity entity = new UserJpaEntity(user);

            assertEquals(user.getId(), entity.getId());
            assertEquals(user.getName(), entity.getName());
            assertEquals(user.getEmail(), entity.getEmail());
            assertEquals(user.getPassword(), entity.getPassword());
            assertEquals(user.getUserType(), entity.getUserType());
            assertEquals(user.getCrm(), entity.getCrm());
            assertEquals(user.getCoren(), entity.getCoren());
            assertEquals(user.getCpf(), entity.getCpf());
            assertEquals(user.getCreatedAt(), entity.getCreatedAt());
            assertEquals(user.getUpdatedAt(), entity.getUpdatedAt());
            assertEquals(user.isActive(), entity.isActive());
        }

        @Test
        @DisplayName("Should create UserJpaEntity from User with all fields")
        void shouldCreateUserJpaEntityFromUserWithAllFields() {
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();
            User user = new User(10L, "Complete User", "complete@email.com", "password", 
                UserType.PACIENTE, null, null, "12345678901", createdAt, updatedAt, false);

            UserJpaEntity entity = new UserJpaEntity(user);

            assertEquals(10L, entity.getId());
            assertEquals("Complete User", entity.getName());
            assertEquals("complete@email.com", entity.getEmail());
            assertEquals("password", entity.getPassword());
            assertEquals(UserType.PACIENTE, entity.getUserType());
            assertNull(entity.getCrm());
            assertNull(entity.getCoren());
            assertEquals("12345678901", entity.getCpf());
            assertEquals(createdAt, entity.getCreatedAt());
            assertEquals(updatedAt, entity.getUpdatedAt());
            assertFalse(entity.isActive());
        }
    }

    @Nested
    @DisplayName("Domain Entity Conversion Tests")
    class DomainEntityConversionTests {

        @Test
        @DisplayName("Should convert to domain entity correctly")
        void shouldConvertToDomainEntityCorrectly() {
            UserJpaEntity entity = new UserJpaEntity();
            entity.setId(1L);
            entity.setName("Dr. Maria Santos");
            entity.setEmail("maria@email.com");
            entity.setPassword("encrypted_password");
            entity.setUserType(UserType.MEDICO);
            entity.setCrm("654321");
            entity.setCoren(null);
            entity.setCpf(null);
            entity.setCreatedAt(LocalDateTime.now().minusDays(1));
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setActive(true);

            User domainEntity = entity.toDomainEntity();

            assertEquals(entity.getId(), domainEntity.getId());
            assertEquals(entity.getName(), domainEntity.getName());
            assertEquals(entity.getEmail(), domainEntity.getEmail());
            assertEquals(entity.getPassword(), domainEntity.getPassword());
            assertEquals(entity.getUserType(), domainEntity.getUserType());
            assertEquals(entity.getCrm(), domainEntity.getCrm());
            assertEquals(entity.getCoren(), domainEntity.getCoren());
            assertEquals(entity.getCpf(), domainEntity.getCpf());
            assertEquals(entity.getCreatedAt(), domainEntity.getCreatedAt());
            assertEquals(entity.getUpdatedAt(), domainEntity.getUpdatedAt());
            assertEquals(entity.isActive(), domainEntity.isActive());
        }

        @Test
        @DisplayName("Should convert nurse entity to domain entity")
        void shouldConvertNurseEntityToDomainEntity() {
            LocalDateTime now = LocalDateTime.now();
            UserJpaEntity entity = new UserJpaEntity();
            entity.setId(2L);
            entity.setName("Enfermeira Ana");
            entity.setEmail("ana@email.com");
            entity.setPassword("password");
            entity.setUserType(UserType.ENFERMEIRO);
            entity.setCoren("789123");
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setActive(true);

            User domainEntity = entity.toDomainEntity();

            assertEquals(UserType.ENFERMEIRO, domainEntity.getUserType());
            assertEquals("789123", domainEntity.getCoren());
            assertNull(domainEntity.getCrm());
            assertNull(domainEntity.getCpf());
        }

        @Test
        @DisplayName("Should convert patient entity to domain entity")
        void shouldConvertPatientEntityToDomainEntity() {
            LocalDateTime now = LocalDateTime.now();
            UserJpaEntity entity = new UserJpaEntity();
            entity.setId(3L);
            entity.setName("Paciente José");
            entity.setEmail("jose@email.com");
            entity.setPassword("password");
            entity.setUserType(UserType.PACIENTE);
            entity.setCpf("98765432100");
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setActive(true);

            User domainEntity = entity.toDomainEntity();

            assertEquals(UserType.PACIENTE, domainEntity.getUserType());
            assertEquals("98765432100", domainEntity.getCpf());
            assertNull(domainEntity.getCrm());
            assertNull(domainEntity.getCoren());
        }
    }

    @Nested
    @DisplayName("Update From Domain Entity Tests")
    class UpdateFromDomainEntityTests {

        @Test
        @DisplayName("Should update JPA entity from domain entity")
        void shouldUpdateJpaEntityFromDomainEntity() {
            UserJpaEntity entity = new UserJpaEntity();
            entity.setId(1L);
            entity.setName("Old Name");
            entity.setEmail("old@email.com");
            entity.setCreatedAt(LocalDateTime.now().minusDays(2));

            User updatedUser = new User("New Name", "new@email.com", "new_password", UserType.MEDICO);
            updatedUser.setId(1L);
            updatedUser.setCrm("999999");

            entity.updateFromDomainEntity(updatedUser);

            assertEquals(1L, entity.getId());
            assertEquals("New Name", entity.getName());
            assertEquals("new@email.com", entity.getEmail());
            assertEquals("new_password", entity.getPassword());
            assertEquals(UserType.MEDICO, entity.getUserType());
            assertEquals("999999", entity.getCrm());
            assertEquals(updatedUser.getUpdatedAt(), entity.getUpdatedAt());
            assertEquals(updatedUser.isActive(), entity.isActive());
        }

        @Test
        @DisplayName("Should preserve created date when updating from domain entity")
        void shouldPreserveCreatedDateWhenUpdatingFromDomainEntity() {
            LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(5);
            UserJpaEntity entity = new UserJpaEntity();
            entity.setId(1L);
            entity.setCreatedAt(originalCreatedAt);

            User updatedUser = new User("Updated User", "updated@email.com", "password", UserType.PACIENTE);
            updatedUser.setId(1L);

            entity.updateFromDomainEntity(updatedUser);

            assertEquals(originalCreatedAt, entity.getCreatedAt());
            assertEquals(updatedUser.getUpdatedAt(), entity.getUpdatedAt());
        }

        @Test
        @DisplayName("Should update nurse-specific fields")
        void shouldUpdateNurseSpecificFields() {
            UserJpaEntity entity = new UserJpaEntity();
            entity.setId(1L);
            entity.setUserType(UserType.MEDICO);
            entity.setCrm("old_crm");

            User nurseUser = new User("Nurse Name", "nurse@email.com", "password", UserType.ENFERMEIRO);
            nurseUser.setId(1L);
            nurseUser.setCoren("new_coren");

            entity.updateFromDomainEntity(nurseUser);

            assertEquals(UserType.ENFERMEIRO, entity.getUserType());
            assertEquals("new_coren", entity.getCoren());
            assertNull(entity.getCrm());
        }

        @Test
        @DisplayName("Should update patient-specific fields")
        void shouldUpdatePatientSpecificFields() {
            UserJpaEntity entity = new UserJpaEntity();
            entity.setId(1L);
            entity.setUserType(UserType.MEDICO);
            entity.setCrm("old_crm");

            User patientUser = new User("Patient Name", "patient@email.com", "password", UserType.PACIENTE);
            patientUser.setId(1L);
            patientUser.setCpf("12345678901");

            entity.updateFromDomainEntity(patientUser);

            assertEquals(UserType.PACIENTE, entity.getUserType());
            assertEquals("12345678901", entity.getCpf());
            assertNull(entity.getCrm());
            assertNull(entity.getCoren());
        }

        @Test
        @DisplayName("Should handle deactivated user update")
        void shouldHandleDeactivatedUserUpdate() {
            UserJpaEntity entity = new UserJpaEntity();
            entity.setId(1L);
            entity.setActive(true);

            User deactivatedUser = new User(1L, "User", "user@email.com", "password", 
                UserType.PACIENTE, null, null, "123", LocalDateTime.now(), LocalDateTime.now(), false);

            entity.updateFromDomainEntity(deactivatedUser);

            assertFalse(entity.isActive());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersAndSettersTests {

        @Test
        @DisplayName("Should set and get all properties correctly")
        void shouldSetAndGetAllPropertiesCorrectly() {
            UserJpaEntity entity = new UserJpaEntity();
            Long id = 1L;
            String name = "Test User";
            String email = "test@email.com";
            String password = "password";
            UserType userType = UserType.MEDICO;
            String crm = "123456";
            String coren = "789123";
            String cpf = "12345678901";
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime updatedAt = LocalDateTime.now();
            boolean active = false;

            entity.setId(id);
            entity.setName(name);
            entity.setEmail(email);
            entity.setPassword(password);
            entity.setUserType(userType);
            entity.setCrm(crm);
            entity.setCoren(coren);
            entity.setCpf(cpf);
            entity.setCreatedAt(createdAt);
            entity.setUpdatedAt(updatedAt);
            entity.setActive(active);

            assertEquals(id, entity.getId());
            assertEquals(name, entity.getName());
            assertEquals(email, entity.getEmail());
            assertEquals(password, entity.getPassword());
            assertEquals(userType, entity.getUserType());
            assertEquals(crm, entity.getCrm());
            assertEquals(coren, entity.getCoren());
            assertEquals(cpf, entity.getCpf());
            assertEquals(createdAt, entity.getCreatedAt());
            assertEquals(updatedAt, entity.getUpdatedAt());
            assertEquals(active, entity.isActive());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            UserJpaEntity entity = new UserJpaEntity();

            entity.setId(null);
            entity.setName(null);
            entity.setEmail(null);
            entity.setPassword(null);
            entity.setUserType(null);
            entity.setCrm(null);
            entity.setCoren(null);
            entity.setCpf(null);
            entity.setCreatedAt(null);
            entity.setUpdatedAt(null);

            assertNull(entity.getId());
            assertNull(entity.getName());
            assertNull(entity.getEmail());
            assertNull(entity.getPassword());
            assertNull(entity.getUserType());
            assertNull(entity.getCrm());
            assertNull(entity.getCoren());
            assertNull(entity.getCpf());
            assertNull(entity.getCreatedAt());
            assertNull(entity.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Round Trip Conversion Tests")
    class RoundTripConversionTests {

        @Test
        @DisplayName("Should maintain data integrity in round trip conversion")
        void shouldMaintainDataIntegrityInRoundTripConversion() {
            User originalUser = new User("Dr. Test", "test@email.com", "password", UserType.MEDICO);
            originalUser.setId(99L);
            originalUser.setCrm("999999");

            UserJpaEntity entity = new UserJpaEntity(originalUser);
            User convertedUser = entity.toDomainEntity();

            assertEquals(originalUser.getId(), convertedUser.getId());
            assertEquals(originalUser.getName(), convertedUser.getName());
            assertEquals(originalUser.getEmail(), convertedUser.getEmail());
            assertEquals(originalUser.getPassword(), convertedUser.getPassword());
            assertEquals(originalUser.getUserType(), convertedUser.getUserType());
            assertEquals(originalUser.getCrm(), convertedUser.getCrm());
            assertEquals(originalUser.getCoren(), convertedUser.getCoren());
            assertEquals(originalUser.getCpf(), convertedUser.getCpf());
            assertEquals(originalUser.getCreatedAt(), convertedUser.getCreatedAt());
            assertEquals(originalUser.getUpdatedAt(), convertedUser.getUpdatedAt());
            assertEquals(originalUser.isActive(), convertedUser.isActive());
        }

        @Test
        @DisplayName("Should handle multiple round trip conversions")
        void shouldHandleMultipleRoundTripConversions() {
            User originalUser = new User("Round Trip User", "roundtrip@email.com", "password", UserType.ENFERMEIRO);
            originalUser.setId(100L);
            originalUser.setCoren("100100");

            UserJpaEntity entity1 = new UserJpaEntity(originalUser);
            User convertedUser1 = entity1.toDomainEntity();
            
            UserJpaEntity entity2 = new UserJpaEntity(convertedUser1);
            User convertedUser2 = entity2.toDomainEntity();

            assertEquals(originalUser.getId(), convertedUser2.getId());
            assertEquals(originalUser.getName(), convertedUser2.getName());
            assertEquals(originalUser.getEmail(), convertedUser2.getEmail());
            assertEquals(originalUser.getUserType(), convertedUser2.getUserType());
            assertEquals(originalUser.getCoren(), convertedUser2.getCoren());
        }
    }
}
