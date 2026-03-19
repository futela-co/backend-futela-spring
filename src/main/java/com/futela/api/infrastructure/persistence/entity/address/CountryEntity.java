package com.futela.api.infrastructure.persistence.entity.address;

import com.futela.api.infrastructure.persistence.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class CountryEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 2, nullable = false, unique = true)
    private String code;

    @Column(length = 10)
    private String phoneCode;

    @Column(nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<ProvinceEntity> provinces = new ArrayList<>();
}
