package com.futela.api.infrastructure.persistence.mapper.auth;

import com.futela.api.domain.model.auth.User;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {}

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        CompanyEntity company = entity.getCompany();
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhone(),
                entity.getAvatar(),
                entity.getRole(),
                entity.getStatus(),
                entity.isVerified(),
                entity.isAvailable(),
                entity.isProfileCompleted(),
                entity.getEmailVerifiedAt(),
                entity.getLastLoginAt(),
                company != null ? company.getId() : null,
                company != null ? company.getName() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public static UserEntity toEntity(User domain, CompanyEntity companyEntity) {
        if (domain == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(domain.id());
        entity.setEmail(domain.email());
        entity.setPasswordHash(domain.passwordHash());
        entity.setFirstName(domain.firstName());
        entity.setLastName(domain.lastName());
        entity.setPhone(domain.phone());
        entity.setAvatar(domain.avatar());
        entity.setRole(domain.role());
        entity.setStatus(domain.status());
        entity.setVerified(domain.isVerified());
        entity.setAvailable(domain.isAvailable());
        entity.setProfileCompleted(domain.profileCompleted());
        entity.setEmailVerifiedAt(domain.emailVerifiedAt());
        entity.setLastLoginAt(domain.lastLoginAt());
        entity.setCompany(companyEntity);
        return entity;
    }
}
