package com.hospital.scheduling.application.dtos;

public record AuthResponse(
        String token,
        String type,
        UserResponse user) {
}
