package com.hospital.scheduling.application.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginRequest Tests")
class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    @DisplayName("Valid LoginRequest Tests")
    class ValidLoginRequestTests {

        @Test
        @DisplayName("Should create valid LoginRequest with all required fields")
        void shouldCreateValidLoginRequestWithAllRequiredFields() {
            LoginRequest request = new LoginRequest("user@email.com", "password123");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
            assertEquals("user@email.com", request.email());
            assertEquals("password123", request.password());
        }

        @Test
        @DisplayName("Should create valid LoginRequest with minimum valid data")
        void shouldCreateValidLoginRequestWithMinimumValidData() {
            LoginRequest request = new LoginRequest("a@b.c", "1");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
            assertEquals("a@b.c", request.email());
            assertEquals("1", request.password());
        }

        @Test
        @DisplayName("Should create valid LoginRequest with special characters")
        void shouldCreateValidLoginRequestWithSpecialCharacters() {
            LoginRequest request = new LoginRequest("user+test@email-domain.com", "P@ssw0rd!");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
            assertEquals("user+test@email-domain.com", request.email());
            assertEquals("P@ssw0rd!", request.password());
        }

        @Test
        @DisplayName("Should create valid LoginRequest with long values")
        void shouldCreateValidLoginRequestWithLongValues() {
            String longEmail = "very.long.email.address.with.many.dots@very-long-domain-name.com";
            String longPassword = "this_is_a_very_long_password_with_many_characters_123456789";

            LoginRequest request = new LoginRequest(longEmail, longPassword);

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
            assertEquals(longEmail, request.email());
            assertEquals(longPassword, request.password());
        }
    }

    @Nested
    @DisplayName("Invalid LoginRequest Tests")
    class InvalidLoginRequestTests {

        @Test
        @DisplayName("Should fail validation when email is null")
        void shouldFailValidationWhenEmailIsNull() {
            LoginRequest request = new LoginRequest(null, "password123");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            
            ConstraintViolation<LoginRequest> violation = violations.iterator().next();
            assertEquals("Email é obrigatório", violation.getMessage());
            assertEquals("email", violation.getPropertyPath().toString());
        }

        @Test
        @DisplayName("Should fail validation when email is empty")
        void shouldFailValidationWhenEmailIsEmpty() {
            LoginRequest request = new LoginRequest("", "password123");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            
            ConstraintViolation<LoginRequest> violation = violations.iterator().next();
            assertEquals("Email é obrigatório", violation.getMessage());
            assertEquals("email", violation.getPropertyPath().toString());
        }

        @Test
        @DisplayName("Should fail validation when email is blank")
        void shouldFailValidationWhenEmailIsBlank() {
            LoginRequest request = new LoginRequest("   ", "password123");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            
            ConstraintViolation<LoginRequest> violation = violations.iterator().next();
            assertEquals("Email é obrigatório", violation.getMessage());
        }

        @Test
        @DisplayName("Should fail validation when password is null")
        void shouldFailValidationWhenPasswordIsNull() {
            LoginRequest request = new LoginRequest("user@email.com", null);

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            
            ConstraintViolation<LoginRequest> violation = violations.iterator().next();
            assertEquals("Senha é obrigatória", violation.getMessage());
            assertEquals("password", violation.getPropertyPath().toString());
        }

        @Test
        @DisplayName("Should fail validation when password is empty")
        void shouldFailValidationWhenPasswordIsEmpty() {
            LoginRequest request = new LoginRequest("user@email.com", "");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            
            ConstraintViolation<LoginRequest> violation = violations.iterator().next();
            assertEquals("Senha é obrigatória", violation.getMessage());
        }

        @Test
        @DisplayName("Should fail validation when password is blank")
        void shouldFailValidationWhenPasswordIsBlank() {
            LoginRequest request = new LoginRequest("user@email.com", "   ");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertEquals(1, violations.size());
            
            ConstraintViolation<LoginRequest> violation = violations.iterator().next();
            assertEquals("Senha é obrigatória", violation.getMessage());
        }

        @Test
        @DisplayName("Should fail validation when both email and password are null")
        void shouldFailValidationWhenBothEmailAndPasswordAreNull() {
            LoginRequest request = new LoginRequest(null, null);

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertEquals(2, violations.size());
            
            boolean hasEmailViolation = violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email") && 
                                  v.getMessage().equals("Email é obrigatório"));
            boolean hasPasswordViolation = violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password") && 
                                  v.getMessage().equals("Senha é obrigatória"));
            
            assertTrue(hasEmailViolation);
            assertTrue(hasPasswordViolation);
        }

        @Test
        @DisplayName("Should fail validation when both email and password are empty")
        void shouldFailValidationWhenBothEmailAndPasswordAreEmpty() {
            LoginRequest request = new LoginRequest("", "");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertEquals(2, violations.size());
        }

        @Test
        @DisplayName("Should fail validation when both email and password are blank")
        void shouldFailValidationWhenBothEmailAndPasswordAreBlank() {
            LoginRequest request = new LoginRequest("   ", "   ");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertEquals(2, violations.size());
        }
    }

    @Nested
    @DisplayName("Record Behavior Tests")
    class RecordBehaviorTests {

        @Test
        @DisplayName("Should implement equals correctly")
        void shouldImplementEqualsCorrectly() {
            LoginRequest request1 = new LoginRequest("user@email.com", "password123");
            LoginRequest request2 = new LoginRequest("user@email.com", "password123");
            LoginRequest request3 = new LoginRequest("other@email.com", "password123");

            assertEquals(request1, request2);
            assertNotEquals(request1, request3);
            assertNotEquals(request1, null);
        }

        @Test
        @DisplayName("Should implement hashCode correctly")
        void shouldImplementHashCodeCorrectly() {
            LoginRequest request1 = new LoginRequest("user@email.com", "password123");
            LoginRequest request2 = new LoginRequest("user@email.com", "password123");
            LoginRequest request3 = new LoginRequest("other@email.com", "password123");

            assertEquals(request1.hashCode(), request2.hashCode());
            assertNotEquals(request1.hashCode(), request3.hashCode());
        }

        @Test
        @DisplayName("Should implement toString correctly")
        void shouldImplementToStringCorrectly() {
            LoginRequest request = new LoginRequest("user@email.com", "password123");

            String toString = request.toString();

            assertTrue(toString.contains("LoginRequest"));
            assertTrue(toString.contains("user@email.com"));
            assertTrue(toString.contains("password123"));
        }

        @Test
        @DisplayName("Should be immutable")
        void shouldBeImmutable() {
            LoginRequest request = new LoginRequest("user@email.com", "password123");

            assertEquals("user@email.com", request.email());
            assertEquals("password123", request.password());

            LoginRequest newRequest = new LoginRequest("new@email.com", "newpassword");
            assertNotEquals(request, newRequest);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle email with various formats")
        void shouldHandleEmailWithVariousFormats() {
            String[] validEmails = {
                "user@domain.com",
                "user.name@domain.com",
                "user+tag@domain.com",
                "user_name@domain-name.com",
                "123@domain.com",
                "user@domain.co.uk"
            };

            for (String email : validEmails) {
                LoginRequest request = new LoginRequest(email, "password");
                Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
                
                assertTrue(violations.isEmpty(), "Email should be valid: " + email);
            }
        }

        @Test
        @DisplayName("Should handle password with various characters")
        void shouldHandlePasswordWithVariousCharacters() {
            String[] validPasswords = {
                "password",
                "123456",
                "P@ssw0rd!",
                "çãêñõü",
                "密码",
                "パスワード",
                "пароль"
            };

            for (String password : validPasswords) {
                LoginRequest request = new LoginRequest("user@email.com", password);
                Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
                
                assertTrue(violations.isEmpty(), "Password should be valid: " + password);
            }
        }

        @Test
        @DisplayName("Should handle tab and newline characters as blank")
        void shouldHandleTabAndNewlineCharactersAsBlank() {
            LoginRequest requestWithTab = new LoginRequest("\t", "\t");
            LoginRequest requestWithNewline = new LoginRequest("\n", "\n");
            LoginRequest requestWithMixed = new LoginRequest(" \t\n ", " \n\t ");

            Set<ConstraintViolation<LoginRequest>> violationsTab = validator.validate(requestWithTab);
            Set<ConstraintViolation<LoginRequest>> violationsNewline = validator.validate(requestWithNewline);
            Set<ConstraintViolation<LoginRequest>> violationsMixed = validator.validate(requestWithMixed);

            assertEquals(2, violationsTab.size());
            assertEquals(2, violationsNewline.size());
            assertEquals(2, violationsMixed.size());
        }
    }
}
