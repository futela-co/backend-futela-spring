package com.futela.api.infrastructure.persistence.adapter.auth;

import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.mapper.auth.UserPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.auth.JpaUserRepository;
import com.futela.api.infrastructure.persistence.repository.core.JpaCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final JpaCompanyRepository jpaCompanyRepository;

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmailAndDeletedAtIsNull(email)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailOrPhone(String identifier) {
        return jpaUserRepository.findByEmailOrPhone(identifier)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean emailExists(String email) {
        return jpaUserRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public boolean phoneExists(String phone) {
        return jpaUserRepository.existsByPhoneAndDeletedAtIsNull(phone);
    }

    @Override
    public User save(User user) {
        CompanyEntity companyEntity = null;
        if (user.companyId() != null) {
            companyEntity = jpaCompanyRepository.findById(user.companyId()).orElse(null);
        }
        UserEntity entity = UserPersistenceMapper.toEntity(user, companyEntity);
        UserEntity saved = jpaUserRepository.save(entity);
        return UserPersistenceMapper.toDomain(saved);
    }

    @Override
    public void updateLastLogin(UUID userId) {
        jpaUserRepository.updateLastLogin(userId, Instant.now());
    }

    @Override
    public long countActive() {
        return jpaUserRepository.countByDeletedAtIsNull();
    }
}
