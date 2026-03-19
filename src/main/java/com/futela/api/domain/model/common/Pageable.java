package com.futela.api.domain.model.common;

public record Pageable(
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
    public Pageable {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 500) size = 500;
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "DESC";
    }
}
