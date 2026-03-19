package com.futela.api.infrastructure.persistence.entity.payment;

import com.futela.api.infrastructure.persistence.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "currencies")
@Getter
@Setter
public class CurrencyEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 3)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Column(nullable = false)
    private boolean isActive = true;
}
