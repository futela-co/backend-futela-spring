package com.futela.api.infrastructure.persistence.entity.messaging;

import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
public class ConversationEntity extends TenantAwareEntity {

    @Column(nullable = false)
    private String subject;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "conversation_participants",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> participants = new HashSet<>();

    @Column(name = "property_id")
    private UUID propertyId;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    @Column(name = "is_archived", nullable = false)
    private boolean isArchived = false;
}
