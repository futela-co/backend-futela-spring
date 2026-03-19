package com.futela.api.infrastructure.persistence.entity.review;

import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_review_property", columnList = "property_id"),
        @Index(name = "idx_review_user", columnList = "user_id"),
        @Index(name = "idx_review_reviewee", columnList = "reviewee_id"),
        @Index(name = "idx_review_reservation", columnList = "reservation_id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private UserEntity reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private ReservationEntity reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderated_by")
    private UserEntity moderatedBy;

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

    @Column(name = "owner_response", columnDefinition = "TEXT")
    private String ownerResponse;
}
