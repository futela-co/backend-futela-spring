package com.futela.api.domain.model.auth;

import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String email,
        String passwordHash,
        String firstName,
        String lastName,
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
        Instant updatedAt,
        Instant deletedAt
) {
    public String fullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
