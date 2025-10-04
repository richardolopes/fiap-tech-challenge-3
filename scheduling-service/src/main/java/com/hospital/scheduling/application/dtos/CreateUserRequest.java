package com.hospital.scheduling.application.dtos;

import com.hospital.shared.domain.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CreateUserRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres") String name,

        @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ter formato válido") String email,

        @NotBlank(message = "Senha é obrigatória") @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres") String password,

        @NotNull(message = "Tipo de usuário é obrigatório") UserType userType,

        String crm,
        String coren,
        String cpf
) {
}
