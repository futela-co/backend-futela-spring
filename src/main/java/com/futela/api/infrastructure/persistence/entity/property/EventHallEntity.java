package com.futela.api.infrastructure.persistence.entity.property;

import com.futela.api.domain.enums.PropertyType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("EVENT_HALL")
@Getter
@Setter
public class EventHallEntity extends PropertyEntity {

    @Column
    private Integer capacity;

    @Override
    public PropertyType getPropertyType() {
        return PropertyType.EVENT_HALL;
    }
}
