package com.futela.api.infrastructure.persistence.entity.property;

import com.futela.api.domain.enums.PropertyType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("APARTMENT")
@Getter
@Setter
public class ApartmentEntity extends PropertyEntity {

    @Override
    public PropertyType getPropertyType() {
        return PropertyType.APARTMENT;
    }
}
