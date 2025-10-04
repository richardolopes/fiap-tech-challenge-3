package com.hospital.scheduling.infrastructure.persistence;

import com.hospital.scheduling.domain.repositories.UserRepository;
import com.hospital.shared.domain.entities.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity;

        if (user.getId() != null) {
            entity = userJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + user.getId()));
            entity.updateFromDomainEntity(user);
        } else {
            entity = new UserJpaEntity(user);
        }

        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        User savedUser = savedEntity.toDomainEntity();

        if (user.getId() == null) {
            savedUser.setId(savedEntity.getId());
        }

        return savedUser;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(UserJpaEntity::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserJpaEntity::toDomainEntity);
    }

    @Override
    public List<User> findActiveUsers() {
        return userJpaRepository.findByActiveTrue()
                .stream()
                .map(UserJpaEntity::toDomainEntity)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
