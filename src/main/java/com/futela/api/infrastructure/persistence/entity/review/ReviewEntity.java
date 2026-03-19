package com.futela.api.infrastructure.persistence.entity.review;

import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_review_property", columnList = "property_id"),
        @Index(name = "idx_review_user", columnList = "user_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_review_user_property", columnNames = {"user_id", "property_id"})
})
@Getter
@Setter
public class ReviewEntity extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "is_approved", nullable = false)
    private boolean isApproved = false;

    @Column(name = "is_flagged", nullable = false)
    private boolean isFlagged = false;

    @Column(name = "flag_reason", columnDefinition = "TEXT")
    private String flagReason;
}
