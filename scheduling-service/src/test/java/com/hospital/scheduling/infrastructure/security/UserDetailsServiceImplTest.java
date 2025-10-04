package com.hospital.scheduling.infrastructure.security;

import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        activeUser = new User("João Silva", "joao@email.com", "password123", UserType.MEDICO);
        activeUser.setId(1L);
        activeUser.setCrm("123456");

        inactiveUser = new User("Maria Santos", "maria@email.com", "password456", UserType.ENFERMEIRO);
        inactiveUser.setId(2L);
        inactiveUser.deactivate();
        inactiveUser.setCoren("789123");
    }

    @Nested
    @DisplayName("Load User By Username Tests")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Should load active user by email successfully")
        void shouldLoadActiveUserByEmailSuccessfully() {
            when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(activeUser));

            UserDetails result = userDetailsService.loadUserByUsername("joao@email.com");

            assertNotNull(result);
            assertInstanceOf(CustomUserDetails.class, result);
            
            CustomUserDetails customUserDetails = (CustomUserDetails) result;
            assertEquals("joao@email.com", customUserDetails.getUsername());
            assertEquals("password123", customUserDetails.getPassword());
            assertTrue(customUserDetails.isEnabled());
            assertTrue(customUserDetails.isAccountNonExpired());
            assertTrue(customUserDetails.isAccountNonLocked());
            assertTrue(customUserDetails.isCredentialsNonExpired());

            verify(userRepository).findByEmail("joao@email.com");
        }

        @Test
        @DisplayName("Should load doctor user successfully")
        void shouldLoadDoctorUserSuccessfully() {
            when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(activeUser));

            UserDetails result = userDetailsService.loadUserByUsername("joao@email.com");

            assertNotNull(result);
            CustomUserDetails customUserDetails = (CustomUserDetails) result;
            
            assertEquals(1L, customUserDetails.getId());
            assertEquals("joao@email.com", customUserDetails.getUsername());
            assertTrue(customUserDetails.isEnabled());

            assertTrue(customUserDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_MEDICO")));
        }

        @Test
        @DisplayName("Should load nurse user successfully")
        void shouldLoadNurseUserSuccessfully() {
            User nurse = new User("Enfermeira Ana", "ana@email.com", "password789", UserType.ENFERMEIRO);
            nurse.setId(3L);
            nurse.setCoren("456789");

            when(userRepository.findByEmail("ana@email.com")).thenReturn(Optional.of(nurse));

            UserDetails result = userDetailsService.loadUserByUsername("ana@email.com");

            assertNotNull(result);
            CustomUserDetails customUserDetails = (CustomUserDetails) result;
            
            assertEquals(3L, customUserDetails.getId());
            assertEquals("ROLE_ENFERMEIRO", customUserDetails.getAuthorities().iterator().next().getAuthority());
            assertEquals("ana@email.com", customUserDetails.getUsername());
            assertTrue(customUserDetails.isEnabled());

            assertTrue(customUserDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ENFERMEIRO")));
        }

        @Test
        @DisplayName("Should load patient user successfully")
        void shouldLoadPatientUserSuccessfully() {
            User patient = new User("Paciente José", "jose@email.com", "password101", UserType.PACIENTE);
            patient.setId(4L);
            patient.setCpf("12345678901");

            when(userRepository.findByEmail("jose@email.com")).thenReturn(Optional.of(patient));

            UserDetails result = userDetailsService.loadUserByUsername("jose@email.com");

            assertNotNull(result);
            CustomUserDetails customUserDetails = (CustomUserDetails) result;
            
            assertEquals(4L, customUserDetails.getId());
            assertEquals("jose@email.com", customUserDetails.getUsername());
            assertTrue(customUserDetails.isEnabled());

            assertTrue(customUserDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_PACIENTE")));
        }
    }

    @Nested
    @DisplayName("Exception Handling Tests")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user not found")
        void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
            when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername("naoexiste@email.com")
            );

            assertEquals("User not found: naoexiste@email.com", exception.getMessage());
            verify(userRepository).findByEmail("naoexiste@email.com");
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user is inactive")
        void shouldThrowUsernameNotFoundExceptionWhenUserIsInactive() {
            when(userRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(inactiveUser));

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername("maria@email.com")
            );

            assertEquals("Inactive user: maria@email.com", exception.getMessage());
            verify(userRepository).findByEmail("maria@email.com");
        }

        @Test
        @DisplayName("Should handle null email gracefully")
        void shouldHandleNullEmailGracefully() {
            when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername(null)
            );

            assertEquals("User not found: null", exception.getMessage());
            verify(userRepository).findByEmail(null);
        }

        @Test
        @DisplayName("Should handle empty email gracefully")
        void shouldHandleEmptyEmailGracefully() {
            when(userRepository.findByEmail("")).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername("")
            );

            assertEquals("User not found: ", exception.getMessage());
            verify(userRepository).findByEmail("");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should return CustomUserDetails with correct user data")
        void shouldReturnCustomUserDetailsWithCorrectUserData() {
            when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(activeUser));

            UserDetails result = userDetailsService.loadUserByUsername("joao@email.com");

            assertInstanceOf(CustomUserDetails.class, result);
            CustomUserDetails customUserDetails = (CustomUserDetails) result;

            assertAll(
                    () -> assertEquals(activeUser.getId(), customUserDetails.getId()),
                    () -> assertEquals(activeUser.getEmail(), customUserDetails.getUsername()),
                    () -> assertEquals(activeUser.getPassword(), customUserDetails.getPassword()),
                    () -> assertEquals(activeUser.isActive(), customUserDetails.isEnabled()),
                    () -> assertTrue(customUserDetails.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_MEDICO")))
            );
        }

        @Test
        @DisplayName("Should handle repository exceptions")
        void shouldHandleRepositoryExceptions() {
            when(userRepository.findByEmail("joao@email.com"))
                    .thenThrow(new RuntimeException("Database connection error"));

            assertThrows(RuntimeException.class, 
                    () -> userDetailsService.loadUserByUsername("joao@email.com"));
        }

        @Test
        @DisplayName("Should verify user activity status correctly")
        void shouldVerifyUserActivityStatusCorrectly() {
            when(userRepository.findByEmail("active@email.com")).thenReturn(Optional.of(activeUser));
            
            assertDoesNotThrow(() -> userDetailsService.loadUserByUsername("active@email.com"));

            when(userRepository.findByEmail("inactive@email.com")).thenReturn(Optional.of(inactiveUser));
            
            assertThrows(UsernameNotFoundException.class, 
                    () -> userDetailsService.loadUserByUsername("inactive@email.com"));
        }

        @Test
        @DisplayName("Should handle different user types correctly")
        void shouldHandleDifferentUserTypesCorrectly() {
            User doctor = new User("Dr. Test", "doctor@email.com", "pass", UserType.MEDICO);
            when(userRepository.findByEmail("doctor@email.com")).thenReturn(Optional.of(doctor));

            UserDetails doctorDetails = userDetailsService.loadUserByUsername("doctor@email.com");
            assertTrue(doctorDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_MEDICO")));

            User nurse = new User("Nurse Test", "nurse@email.com", "pass", UserType.ENFERMEIRO);
            when(userRepository.findByEmail("nurse@email.com")).thenReturn(Optional.of(nurse));

            UserDetails nurseDetails = userDetailsService.loadUserByUsername("nurse@email.com");
            assertTrue(nurseDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ENFERMEIRO")));

            User patient = new User("Patient Test", "patient@email.com", "pass", UserType.PACIENTE);
            when(userRepository.findByEmail("patient@email.com")).thenReturn(Optional.of(patient));

            UserDetails patientDetails = userDetailsService.loadUserByUsername("patient@email.com");
            assertTrue(patientDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_PACIENTE")));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle user with minimum required data")
        void shouldHandleUserWithMinimumRequiredData() {
            User minimalUser = new User("User", "user@email.com", "password", UserType.PACIENTE);

            when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(minimalUser));

            UserDetails result = userDetailsService.loadUserByUsername("user@email.com");

            assertNotNull(result);
            assertEquals("user@email.com", result.getUsername());
            assertEquals("password", result.getPassword());
            assertTrue(result.isEnabled());
        }

        @Test
        @DisplayName("Should handle user with special characters in email")
        void shouldHandleUserWithSpecialCharactersInEmail() {
            User userWithSpecialEmail = new User("User", "user+test@email-domain.com", "password", UserType.MEDICO);

            when(userRepository.findByEmail("user+test@email-domain.com")).thenReturn(Optional.of(userWithSpecialEmail));

            UserDetails result = userDetailsService.loadUserByUsername("user+test@email-domain.com");

            assertNotNull(result);
            assertEquals("user+test@email-domain.com", result.getUsername());
        }

        @Test
        @DisplayName("Should handle case sensitivity in email")
        void shouldHandleCaseSensitivityInEmail() {
            when(userRepository.findByEmail("JOAO@EMAIL.COM")).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, 
                    () -> userDetailsService.loadUserByUsername("JOAO@EMAIL.COM"));

            verify(userRepository).findByEmail("JOAO@EMAIL.COM");
            verify(userRepository, never()).findByEmail("joao@email.com");
        }
    }
}
