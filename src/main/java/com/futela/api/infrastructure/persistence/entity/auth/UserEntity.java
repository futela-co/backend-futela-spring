package com.futela.api.infrastructure.persistence.entity.auth;

import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends TenantAwareEntity {

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Column
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(nullable = false)
    @Builder.Default
    private boolean isVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAvailable = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean profileCompleted = false;

    @Column
    private Instant emailVerifiedAt;

    @Column
    private Instant lastLoginAt;
}
