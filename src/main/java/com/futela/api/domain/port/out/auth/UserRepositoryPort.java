package com.futela.api.domain.port.out.auth;

import com.futela.api.domain.model.auth.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrPhone(String identifier);

    boolean emailExists(String email);

    boolean phoneExists(String phone);

    User save(User user);

    void updateLastLogin(UUID userId);
}
