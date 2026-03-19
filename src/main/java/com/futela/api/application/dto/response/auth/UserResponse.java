package com.futela.api.application.dto.response.auth;

import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.domain.model.auth.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String phone,
        String avatar,
        UserRole role,
        UserStatus status,
        boolean isVerified,
        boolean isAvailable,
        boolean profileCompleted,
        Instant emailVerifiedAt,
        Instant lastLoginAt,
        UUID companyId,
        String companyName,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.id(),
                user.email(),
                user.firstName(),
                user.lastName(),
                user.fullName(),
                user.phone(),
                user.avatar(),
                user.role(),
                user.status(),
                user.isVerified(),
                user.isAvailable(),
                user.profileCompleted(),
                user.emailVerifiedAt(),
                user.lastLoginAt(),
                user.companyId(),
                user.companyName(),
                user.createdAt(),
                user.updatedAt()
        );
    }
}
