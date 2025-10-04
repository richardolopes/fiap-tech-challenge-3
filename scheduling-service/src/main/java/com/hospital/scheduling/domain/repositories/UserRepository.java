package com.hospital.scheduling.domain.repositories;

import com.hospital.shared.domain.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findActiveUsers();

    boolean existsByEmail(String email);

}
