package com.futela.api.infrastructure.persistence.entity.core;

import com.futela.api.infrastructure.persistence.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column
    private String email;

    @Column(length = 50)
    private String phone;

    @Column
    private String logo;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
