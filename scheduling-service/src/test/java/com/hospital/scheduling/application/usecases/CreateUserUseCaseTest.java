package com.hospital.scheduling.application.usecases;

import com.hospital.scheduling.application.dtos.CreateUserRequest;
import com.hospital.scheduling.application.dtos.UserResponse;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase Tests")
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private CreateUserRequest validDoctorRequest;
    private CreateUserRequest validNurseRequest;
    private CreateUserRequest validPatientRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        validDoctorRequest = new CreateUserRequest(
            "Dr. João Silva",
            "joao@email.com",
            "password123",
            UserType.MEDICO,
            "123456",
            null,
            null
        );

        validNurseRequest = new CreateUserRequest(
            "Enfermeira Maria",
            "maria@email.com",
            "password123",
            UserType.ENFERMEIRO,
            null,
            "654321",
            null
        );

        validPatientRequest = new CreateUserRequest(
            "Paciente José",
            "jose@email.com",
            "password123",
            UserType.PACIENTE,
            null,
            null,
            "12345678901"
        );

        savedUser = new User("Test User", "test@email.com", "encoded_password", UserType.MEDICO);
        savedUser.setId(1L);
    }

    @Nested
    @DisplayName("Successful User Creation Tests")
    class SuccessfulUserCreationTests {

        @Test
        @DisplayName("Should create doctor successfully")
        void shouldCreateDoctorSuccessfully() {
            when(userRepository.existsByEmail(validDoctorRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(validDoctorRequest.password())).thenReturn("encoded_password");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            UserResponse response = createUserUseCase.execute(validDoctorRequest);

            assertNotNull(response);
            assertEquals(savedUser.getId(), response.id());
            assertEquals(savedUser.getName(), response.name());
            assertEquals(savedUser.getEmail(), response.email());
            assertEquals(savedUser.getUserType(), response.userType());

            verify(userRepository).existsByEmail(validDoctorRequest.email());
            verify(passwordEncoder).encode(validDoctorRequest.password());

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertEquals(validDoctorRequest.name(), capturedUser.getName());
            assertEquals(validDoctorRequest.email(), capturedUser.getEmail());
            assertEquals("encoded_password", capturedUser.getPassword());
            assertEquals(UserType.MEDICO, capturedUser.getUserType());
            assertEquals(validDoctorRequest.crm(), capturedUser.getCrm());
        }

        @Test
        @DisplayName("Should create nurse successfully")
        void shouldCreateNurseSuccessfully() {
            when(userRepository.existsByEmail(validNurseRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(validNurseRequest.password())).thenReturn("encoded_password");
            
            User savedNurse = new User("Enfermeira Maria", "maria@email.com", "encoded_password", UserType.ENFERMEIRO);
            savedNurse.setId(2L);
            savedNurse.setCoren("654321");
            
            when(userRepository.save(any(User.class))).thenReturn(savedNurse);

            UserResponse response = createUserUseCase.execute(validNurseRequest);

            assertNotNull(response);
            assertEquals(savedNurse.getId(), response.id());
            assertEquals(savedNurse.getUserType(), response.userType());
            assertEquals(savedNurse.getCoren(), response.coren());

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertEquals(UserType.ENFERMEIRO, capturedUser.getUserType());
            assertEquals(validNurseRequest.coren(), capturedUser.getCoren());
        }

        @Test
        @DisplayName("Should create patient successfully")
        void shouldCreatePatientSuccessfully() {
            when(userRepository.existsByEmail(validPatientRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(validPatientRequest.password())).thenReturn("encoded_password");
            
            User savedPatient = new User("Paciente José", "jose@email.com", "encoded_password", UserType.PACIENTE);
            savedPatient.setId(3L);
            savedPatient.setCpf("12345678901");
            
            when(userRepository.save(any(User.class))).thenReturn(savedPatient);

            UserResponse response = createUserUseCase.execute(validPatientRequest);

            assertNotNull(response);
            assertEquals(savedPatient.getId(), response.id());
            assertEquals(savedPatient.getUserType(), response.userType());
            assertEquals(savedPatient.getCpf(), response.cpf());

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertEquals(UserType.PACIENTE, capturedUser.getUserType());
            assertEquals(validPatientRequest.cpf(), capturedUser.getCpf());
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            when(userRepository.existsByEmail(validDoctorRequest.email())).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createUserUseCase.execute(validDoctorRequest)
            );

            assertEquals("Email já está em uso: " + validDoctorRequest.email(), exception.getMessage());
            verify(userRepository).existsByEmail(validDoctorRequest.email());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Doctor Validation Tests")
    class DoctorValidationTests {

        @Test
        @DisplayName("Should throw exception when CRM is null for doctor")
        void shouldThrowExceptionWhenCrmIsNullForDoctor() {
            CreateUserRequest doctorWithoutCrm = new CreateUserRequest(
                "Dr. João",
                "joao@email.com",
                "password123",
                UserType.MEDICO,
                null,
                null,
                null
            );
            when(userRepository.existsByEmail(doctorWithoutCrm.email())).thenReturn(false);
            when(passwordEncoder.encode(doctorWithoutCrm.password())).thenReturn("encoded_password");

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createUserUseCase.execute(doctorWithoutCrm)
            );

            assertEquals("CRM é obrigatório para médicos", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when CRM is empty for doctor")
        void shouldThrowExceptionWhenCrmIsEmptyForDoctor() {
            CreateUserRequest doctorWithEmptyCrm = new CreateUserRequest(
                "Dr. João",
                "joao@email.com",
                "password123",
                UserType.MEDICO,
                "   ",
                null,
                null
            );
            when(userRepository.existsByEmail(doctorWithEmptyCrm.email())).thenReturn(false);
            when(passwordEncoder.encode(doctorWithEmptyCrm.password())).thenReturn("encoded_password");

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createUserUseCase.execute(doctorWithEmptyCrm)
            );

            assertEquals("CRM é obrigatório para médicos", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Nurse Validation Tests")
    class NurseValidationTests {

        @Test
        @DisplayName("Should throw exception when COREN is null for nurse")
        void shouldThrowExceptionWhenCorenIsNullForNurse() {
            CreateUserRequest nurseWithoutCoren = new CreateUserRequest(
                "Enfermeira Maria",
                "maria@email.com",
                "password123",
                UserType.ENFERMEIRO,
                null,
                null,
                null
            );
            when(userRepository.existsByEmail(nurseWithoutCoren.email())).thenReturn(false);
            when(passwordEncoder.encode(nurseWithoutCoren.password())).thenReturn("encoded_password");

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createUserUseCase.execute(nurseWithoutCoren)
            );

            assertEquals("COREN é obrigatório para enfermeiros", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when COREN is empty for nurse")
        void shouldThrowExceptionWhenCorenIsEmptyForNurse() {
            CreateUserRequest nurseWithEmptyCoren = new CreateUserRequest(
                "Enfermeira Maria",
                "maria@email.com",
                "password123",
                UserType.ENFERMEIRO,
                null,
                "   ",
                null
            );
            when(userRepository.existsByEmail(nurseWithEmptyCoren.email())).thenReturn(false);
            when(passwordEncoder.encode(nurseWithEmptyCoren.password())).thenReturn("encoded_password");

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createUserUseCase.execute(nurseWithEmptyCoren)
            );

            assertEquals("COREN é obrigatório para enfermeiros", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Patient Validation Tests")
    class PatientValidationTests {

        @Test
        @DisplayName("Should throw exception when CPF is null for patient")
        void shouldThrowExceptionWhenCpfIsNullForPatient() {
            CreateUserRequest patientWithoutCpf = new CreateUserRequest(
                "Paciente José",
                "jose@email.com",
                "password123",
                UserType.PACIENTE,
                null,
                null,
                null
            );
            when(userRepository.existsByEmail(patientWithoutCpf.email())).thenReturn(false);
            when(passwordEncoder.encode(patientWithoutCpf.password())).thenReturn("encoded_password");

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createUserUseCase.execute(patientWithoutCpf)
            );

            assertEquals("CPF é obrigatório para pacientes", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when CPF is empty for patient")
        void shouldThrowExceptionWhenCpfIsEmptyForPatient() {
            CreateUserRequest patientWithEmptyCpf = new CreateUserRequest(
                "Paciente José",
                "jose@email.com",
                "password123",
                UserType.PACIENTE,
                null,
                null,
                "   "
            );
            when(userRepository.existsByEmail(patientWithEmptyCpf.email())).thenReturn(false);
            when(passwordEncoder.encode(patientWithEmptyCpf.password())).thenReturn("encoded_password");

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createUserUseCase.execute(patientWithEmptyCpf)
            );

            assertEquals("CPF é obrigatório para pacientes", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should call password encoder with correct password")
        void shouldCallPasswordEncoderWithCorrectPassword() {
            when(userRepository.existsByEmail(validDoctorRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(validDoctorRequest.password())).thenReturn("encoded_password");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            createUserUseCase.execute(validDoctorRequest);

            verify(passwordEncoder).encode("password123");
        }

        @Test
        @DisplayName("Should save user with encoded password")
        void shouldSaveUserWithEncodedPassword() {
            when(userRepository.existsByEmail(validDoctorRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(validDoctorRequest.password())).thenReturn("super_secure_encoded_password");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            createUserUseCase.execute(validDoctorRequest);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertEquals("super_secure_encoded_password", capturedUser.getPassword());
        }

        @Test
        @DisplayName("Should return response from saved user entity")
        void shouldReturnResponseFromSavedUserEntity() {
            when(userRepository.existsByEmail(validDoctorRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(validDoctorRequest.password())).thenReturn("encoded_password");
            
            User specificSavedUser = new User("Specific Doctor", "specific@email.com", "encoded_password", UserType.MEDICO);
            specificSavedUser.setId(999L);
            specificSavedUser.setCrm("999999");
            
            when(userRepository.save(any(User.class))).thenReturn(specificSavedUser);

            UserResponse response = createUserUseCase.execute(validDoctorRequest);

            assertEquals(999L, response.id());
            assertEquals("Specific Doctor", response.name());
            assertEquals("specific@email.com", response.email());
            assertEquals(UserType.MEDICO, response.userType());
            assertEquals("999999", response.crm());
        }
    }
}