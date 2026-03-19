package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.domain.model.common.PageResult;
import com.futela.api.domain.port.out.property.PropertySearchCriteria;

public interface SearchPropertiesUseCase {
    PageResult<PropertySummaryResponse> execute(PropertySearchCriteria criteria);
}
