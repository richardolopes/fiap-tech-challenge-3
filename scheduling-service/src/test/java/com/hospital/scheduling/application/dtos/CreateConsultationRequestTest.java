package com.hospital.scheduling.application.dtos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateConsultationRequest DTO Tests")
class CreateConsultationRequestTest {

    @Test
    @DisplayName("Should create CreateConsultationRequest with all parameters")
    void shouldCreateCreateConsultationRequestWithAllParameters() {
        Long patientId = 1L;
        Long doctorId = 2L;
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);

        CreateConsultationRequest request = new CreateConsultationRequest(patientId, doctorId, scheduledDateTime);

        assertEquals(patientId, request.patientId());
        assertEquals(doctorId, request.doctorId());
        assertEquals(scheduledDateTime, request.scheduledDateTime());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        CreateConsultationRequest request = new CreateConsultationRequest(null, null, null);

        assertNull(request.patientId());
        assertNull(request.doctorId());
        assertNull(request.scheduledDateTime());
    }

    @Test
    @DisplayName("Should be equal when all fields are the same")
    void shouldBeEqualWhenAllFieldsAreTheSame() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        CreateConsultationRequest request1 = new CreateConsultationRequest(1L, 2L, scheduledDateTime);
        CreateConsultationRequest request2 = new CreateConsultationRequest(1L, 2L, scheduledDateTime);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when fields are different")
    void shouldNotBeEqualWhenFieldsAreDifferent() {
        LocalDateTime scheduledDateTime1 = LocalDateTime.now().plusDays(1);
        LocalDateTime scheduledDateTime2 = LocalDateTime.now().plusDays(2);
        
        CreateConsultationRequest request1 = new CreateConsultationRequest(1L, 2L, scheduledDateTime1);
        CreateConsultationRequest request2 = new CreateConsultationRequest(3L, 4L, scheduledDateTime2);

        assertNotEquals(request1, request2);
    }

    @Test
    @DisplayName("Should not be equal when patient IDs are different")
    void shouldNotBeEqualWhenPatientIdsAreDifferent() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        CreateConsultationRequest request1 = new CreateConsultationRequest(1L, 2L, scheduledDateTime);
        CreateConsultationRequest request2 = new CreateConsultationRequest(3L, 2L, scheduledDateTime);

        assertNotEquals(request1, request2);
    }

    @Test
    @DisplayName("Should not be equal when doctor IDs are different")
    void shouldNotBeEqualWhenDoctorIdsAreDifferent() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        CreateConsultationRequest request1 = new CreateConsultationRequest(1L, 2L, scheduledDateTime);
        CreateConsultationRequest request2 = new CreateConsultationRequest(1L, 3L, scheduledDateTime);

        assertNotEquals(request1, request2);
    }

    @Test
    @DisplayName("Should not be equal when scheduled date times are different")
    void shouldNotBeEqualWhenScheduledDateTimesAreDifferent() {
        LocalDateTime scheduledDateTime1 = LocalDateTime.now().plusDays(1);
        LocalDateTime scheduledDateTime2 = LocalDateTime.now().plusDays(2);
        
        CreateConsultationRequest request1 = new CreateConsultationRequest(1L, 2L, scheduledDateTime1);
        CreateConsultationRequest request2 = new CreateConsultationRequest(1L, 2L, scheduledDateTime2);

        assertNotEquals(request1, request2);
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);
        CreateConsultationRequest request = new CreateConsultationRequest(1L, 2L, scheduledDateTime);

        String result = request.toString();

        assertTrue(result.contains("CreateConsultationRequest"));
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains(scheduledDateTime.toString()));
    }

    @Test
    @DisplayName("Should create request with different patient and doctor combinations")
    void shouldCreateRequestWithDifferentPatientAndDoctorCombinations() {
        LocalDateTime scheduledDateTime = LocalDateTime.now().plusDays(1);

        CreateConsultationRequest request1 = new CreateConsultationRequest(10L, 20L, scheduledDateTime);
        CreateConsultationRequest request2 = new CreateConsultationRequest(100L, 200L, scheduledDateTime);

        assertEquals(10L, request1.patientId());
        assertEquals(20L, request1.doctorId());
        assertEquals(100L, request2.patientId());
        assertEquals(200L, request2.doctorId());
        assertNotEquals(request1, request2);
    }

    @Test
    @DisplayName("Should handle edge case values")
    void shouldHandleEdgeCaseValues() {
        Long maxValue = Long.MAX_VALUE;
        Long minValue = Long.MIN_VALUE;
        LocalDateTime futureDateTime = LocalDateTime.now().plusYears(10);

        CreateConsultationRequest request = new CreateConsultationRequest(maxValue, minValue, futureDateTime);

        assertEquals(maxValue, request.patientId());
        assertEquals(minValue, request.doctorId());
        assertEquals(futureDateTime, request.scheduledDateTime());
    }
}
