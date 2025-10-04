package com.hospital.scheduling.presentation.controllers;

import com.hospital.scheduling.application.dtos.UserResponse;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.shared.domain.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('MEDICO') or hasRole('ENFERMEIRO')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findActiveUsers();
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ENFERMEIRO') or (#id == authentication.principal.id)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserResponse.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
