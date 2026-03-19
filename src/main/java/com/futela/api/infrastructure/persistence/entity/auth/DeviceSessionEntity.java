package com.futela.api.infrastructure.persistence.entity.auth;

import com.futela.api.infrastructure.persistence.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "device_sessions", indexes = {
        @Index(name = "idx_device_session_user_active", columnList = "user_id, isActive"),
        @Index(name = "idx_device_fingerprint", columnList = "deviceFingerprint")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSessionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(length = 255)
    private String deviceName;

    @Column(nullable = false, length = 64)
    private String deviceFingerprint;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(length = 100)
    private String location;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean isTrusted = false;

    @Column(nullable = false)
    private Instant lastActiveAt;

    @Column
    private Instant revokedAt;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (lastActiveAt == null) {
            lastActiveAt = Instant.now();
        }
    }
}
