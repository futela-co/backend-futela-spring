package com.futela.api.domain.port.out.property;

import com.futela.api.domain.enums.ListingType;
import com.futela.api.domain.enums.PropertyType;

import java.math.BigDecimal;
import java.util.UUID;

public record PropertySearchCriteria(
        PropertyType type,
        ListingType listingType,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        UUID cityId,
        UUID districtId,
        Integer bedrooms,
        Boolean furnished,
        String sort,
        int page,
        int size
) {
    public PropertySearchCriteria {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 50) size = 50;
    }
}
