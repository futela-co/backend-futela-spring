package com.futela.api.domain.port.in.category;

import com.futela.api.application.dto.request.category.UpdateCategoryRequest;
import com.futela.api.application.dto.response.category.CategoryResponse;

import java.util.UUID;

public interface UpdateCategoryUseCase {
    CategoryResponse execute(UUID id, UpdateCategoryRequest request);
}
