package com.futela.api.domain.port.in.category;

import com.futela.api.application.dto.response.category.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface GetCategoriesUseCase {
    List<CategoryResponse> execute(UUID companyId);
}
