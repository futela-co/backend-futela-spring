package com.futela.api.domain.port.in.category;

import com.futela.api.application.dto.response.category.CategoryResponse;

import java.util.UUID;

public interface GetCategoryByIdUseCase {
    CategoryResponse execute(UUID id);
}
