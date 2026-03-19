package com.futela.api.domain.port.in.category;

import com.futela.api.application.dto.request.category.CreateCategoryRequest;
import com.futela.api.application.dto.response.category.CategoryResponse;

import java.util.UUID;

public interface CreateCategoryUseCase {
    CategoryResponse execute(CreateCategoryRequest request, UUID companyId);
}
