package com.futela.api.infrastructure.persistence.entity.auth;

import com.futela.api.infrastructure.persistence.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_hash", columnList = "tokenHash", unique = true),
        @Index(name = "idx_refresh_token_expires", columnList = "expiresAt")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_session_id", nullable = false)
    private DeviceSessionEntity deviceSession;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column
    private Instant usedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean isRevoked = false;
}
