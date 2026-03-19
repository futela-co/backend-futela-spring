package com.futela.api.presentation.controller.address;

import com.futela.api.application.dto.request.category.CreateCategoryRequest;
import com.futela.api.application.dto.request.category.UpdateCategoryRequest;
import com.futela.api.application.dto.response.category.CategoryResponse;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.usecase.category.CategoryUseCaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryUseCaseService categoryService;

    public CategoryController(CategoryUseCaseService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> getCategories(
            @RequestParam(required = false) UUID companyId) {
        return ApiResponse.success(categoryService.getCategories(companyId));
    }

    @GetMapping("/categories/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        return ApiResponse.success(categoryService.getCategoryById(id));
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            @RequestParam UUID companyId) {
        return ApiResponse.success(categoryService.createCategory(request, companyId), "Catégorie créée avec succès");
    }

    @PutMapping("/admin/categories/{id}")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return ApiResponse.success(categoryService.updateCategory(id, request), "Catégorie modifiée avec succès");
    }

    @DeleteMapping("/admin/categories/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null, "Catégorie supprimée avec succès");
    }
}
