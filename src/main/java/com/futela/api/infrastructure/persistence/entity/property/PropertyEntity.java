package com.futela.api.infrastructure.persistence.entity.property;

import com.futela.api.domain.enums.*;
import com.futela.api.infrastructure.persistence.entity.address.AddressEntity;
import com.futela.api.infrastructure.persistence.entity.common.TenantAwareEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "properties")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class PropertyEntity extends TenantAwareEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status = PropertyStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false)
    private ListingType listingType = ListingType.RENT;

    @Column(name = "price_per_day", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Column(name = "price_per_month", precision = 10, scale = 2)
    private BigDecimal pricePerMonth;

    @Column(name = "sale_price", precision = 15, scale = 2)
    private BigDecimal salePrice;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished = false;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(precision = 3, scale = 2)
    private Double rating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount = 0;

    @Column(name = "deletion_reason", length = 500)
    private String deletionReason;

    // Shared columns for Apartment/House
    @Column
    private Integer bedrooms;

    @Column
    private Integer bathrooms;

    @Column(name = "square_meters")
    private Integer squareMeters;

    // JSON attributes for type-specific non-filtered fields
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private Map<String, Object> attributes = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private AddressEntity address;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<PhotoEntity> photos = new ArrayList<>();

    @Transient
    public abstract PropertyType getPropertyType();
}
