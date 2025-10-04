package com.hospital.scheduling.presentation.controllers;

import com.hospital.scheduling.application.dtos.AuthResponse;
import com.hospital.scheduling.application.dtos.CreateUserRequest;
import com.hospital.scheduling.application.dtos.LoginRequest;
import com.hospital.scheduling.application.dtos.UserResponse;
import com.hospital.scheduling.application.usecases.CreateUserUseCase;
import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.scheduling.infrastructure.config.JwtTokenUtil;
import com.hospital.shared.domain.entities.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final CreateUserUseCase createUserUseCase;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtTokenUtil jwtTokenUtil,
                          CreateUserUseCase createUserUseCase,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.createUserUseCase = createUserUseCase;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
        final String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse userResponse = UserResponse.fromEntity(user);
        AuthResponse authResponse = new AuthResponse(token, "Bearer", userResponse);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CreateUserRequest createUserRequest) {
        try {
            UserResponse userResponse = createUserUseCase.execute(createUserRequest);
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
