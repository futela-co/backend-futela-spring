package com.futela.api.infrastructure.persistence.entity.address;

import com.futela.api.infrastructure.persistence.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "provinces")
@Getter
@Setter
public class ProvinceEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 50)
    private String code;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @OneToMany(mappedBy = "province", fetch = FetchType.LAZY)
    private List<CityEntity> cities = new ArrayList<>();
}
