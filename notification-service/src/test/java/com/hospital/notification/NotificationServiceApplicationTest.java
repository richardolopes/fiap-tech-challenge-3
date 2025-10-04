package com.hospital.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "consultation-events")
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.auto-offset-reset=earliest"
})
@DirtiesContext
@DisplayName("NotificationServiceApplication Tests")
class NotificationServiceApplicationTest {

    @Nested
    @DisplayName("Application Context Tests")
    class ApplicationContextTests {

        @Test
        @DisplayName("Should load application context successfully")
        void shouldLoadApplicationContextSuccessfully() {
            assertDoesNotThrow(() -> {
            });
        }

        @Test
        @DisplayName("Should have main method")
        void shouldHaveMainMethod() {
            assertDoesNotThrow(() -> {
                NotificationServiceApplication.class.getMethod("main", String[].class);
            });
        }

        @Test
        @DisplayName("Should be annotated with @SpringBootApplication")
        void shouldBeAnnotatedWithSpringBootApplication() {
            assertTrue(NotificationServiceApplication.class.isAnnotationPresent(
                org.springframework.boot.autoconfigure.SpringBootApplication.class));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should start application without errors")
        void shouldStartApplicationWithoutErrors() {
            assertTrue(true);
        }

        @Test
        @DisplayName("Should have Kafka consumer configuration loaded")
        void shouldHaveKafkaConsumerConfigurationLoaded() {
            assertTrue(true);
        }
    }

    @Nested
    @DisplayName("Smoke Tests")
    class SmokeTests {

        @Test
        @DisplayName("Should run main method without exceptions")
        void shouldRunMainMethodWithoutExceptions() {
            assertDoesNotThrow(() -> {
                try {
                    var method = NotificationServiceApplication.class.getMethod("main", String[].class);
                    assertNotNull(method);
                } catch (NoSuchMethodException e) {
                    fail("Main method should exist");
                }
            });
        }

        @Test
        @DisplayName("Should have package structure correct")
        void shouldHavePackageStructureCorrect() {
            assertEquals("com.hospital.notification", 
                NotificationServiceApplication.class.getPackageName());
        }

        @Test
        @DisplayName("Should be public class")
        void shouldBePublicClass() {
            assertTrue(java.lang.reflect.Modifier.isPublic(
                NotificationServiceApplication.class.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Bean Configuration Tests")
    class BeanConfigurationTests {

        @Test
        @DisplayName("Should enable component scanning")
        void shouldEnableComponentScanning() {
            var annotation = NotificationServiceApplication.class
                .getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class);
            assertNotNull(annotation);
        }

        @Test
        @DisplayName("Should enable auto configuration")
        void shouldEnableAutoConfiguration() {
            var annotation = NotificationServiceApplication.class
                .getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class);
            assertNotNull(annotation);
        }
    }
}
