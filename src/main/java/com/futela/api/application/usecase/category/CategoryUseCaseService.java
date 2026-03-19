package com.futela.api.application.usecase.category;

import com.futela.api.application.dto.request.category.CreateCategoryRequest;
import com.futela.api.application.dto.request.category.UpdateCategoryRequest;
import com.futela.api.application.dto.response.category.CategoryResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.property.Category;
import com.futela.api.domain.port.out.property.CategoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryUseCaseService {

    private final CategoryRepositoryPort categoryRepository;

    public CategoryUseCaseService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CreateCategoryRequest request, UUID companyId) {
        String slug = generateSlug(request.name());
        var category = new Category(null, request.name(), slug,
                request.description(), request.icon(), true,
                companyId, Instant.now(), Instant.now());
        return CategoryResponse.fromDomain(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(UUID companyId) {
        return categoryRepository.findAllByCompanyId(companyId).stream()
                .map(CategoryResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(CategoryResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id.toString()));
    }

    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        var existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id.toString()));
        var updated = new Category(existing.id(), request.name(), existing.slug(),
                request.description(), request.icon(), existing.isActive(),
                existing.companyId(), existing.createdAt(), Instant.now());
        return CategoryResponse.fromDomain(categoryRepository.save(updated));
    }

    public void deleteCategory(UUID id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id.toString()));
        categoryRepository.softDelete(id);
    }

    private String generateSlug(String text) {
        String base = text.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        return base + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
