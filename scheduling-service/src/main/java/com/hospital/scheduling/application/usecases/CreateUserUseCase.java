package com.hospital.scheduling.application.usecases;

import com.hospital.scheduling.application.dtos.CreateUserRequest;
import com.hospital.scheduling.application.dtos.UserResponse;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.shared.domain.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse execute(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email já está em uso: " + request.email());
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.name(), request.email(), encodedPassword, request.userType());

        switch (request.userType()) {
            case MEDICO -> {
                if (request.crm() == null || request.crm().trim().isEmpty()) {
                    throw new IllegalArgumentException("CRM é obrigatório para médicos");
                }
                user.setCrm(request.crm());
            }
            case ENFERMEIRO -> {
                if (request.coren() == null || request.coren().trim().isEmpty()) {
                    throw new IllegalArgumentException("COREN é obrigatório para enfermeiros");
                }
                user.setCoren(request.coren());
            }
            case PACIENTE -> {
                if (request.cpf() == null || request.cpf().trim().isEmpty()) {
                    throw new IllegalArgumentException("CPF é obrigatório para pacientes");
                }
                user.setCpf(request.cpf());
            }
        }

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }
}
