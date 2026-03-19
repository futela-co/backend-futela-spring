package com.futela.api.infrastructure.persistence.entity.property;

import com.futela.api.domain.enums.LandType;
import com.futela.api.domain.enums.PropertyType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("LAND")
@Getter
@Setter
public class LandEntity extends PropertyEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "land_type")
    private LandType landType;

    @Column(name = "surface_area")
    private Integer surfaceArea;

    @Override
    public PropertyType getPropertyType() {
        return PropertyType.LAND;
    }
}
