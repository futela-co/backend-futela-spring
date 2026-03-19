package com.futela.api.infrastructure.persistence.entity.property;

import com.futela.api.domain.enums.FuelType;
import com.futela.api.domain.enums.PropertyType;
import com.futela.api.domain.enums.Transmission;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("CAR")
@Getter
@Setter
public class CarEntity extends PropertyEntity {

    @Column
    private String brand;

    @Column
    private String model;

    @Column
    private Integer year;

    @Column
    private Integer mileage;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column
    private Transmission transmission;

    @Override
    public PropertyType getPropertyType() {
        return PropertyType.CAR;
    }
}
