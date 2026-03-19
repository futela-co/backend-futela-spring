package com.futela.api.infrastructure.persistence.entity.address;

import com.futela.api.infrastructure.persistence.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "towns")
@Getter
@Setter
public class TownEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 20)
    private String zipCode;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;

    @OneToMany(mappedBy = "town", fetch = FetchType.LAZY)
    private List<DistrictEntity> districts = new ArrayList<>();
}
