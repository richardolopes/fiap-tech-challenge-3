package com.hospital.scheduling.application.dtos;

import com.hospital.shared.domain.entities.User;
import com.hospital.shared.domain.enums.UserType;

import java.time.LocalDateTime;


public record UserResponse(
        Long id,
        String name,
        String email,
        UserType userType,
        String crm,
        String coren,
        String cpf,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType(),
                user.getCrm(),
                user.getCoren(),
                user.getCpf(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
