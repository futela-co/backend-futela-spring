package com.futela.api.application.dto.response.common;

import java.util.List;

/**
 * Paginated response matching Symfony API Platform format.
 * Format: {member: [...], totalItems: N, page: N, itemsPerPage: N, totalPages: N}
 *
 * The frontend normalizePaginatedResponse() handles this format natively.
 */
public record PagedResponse<T>(
        List<T> member,
        int totalItems,
        int page,
        int itemsPerPage,
        int totalPages
) {
    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this(content, (int) totalElements, page + 1, size, totalPages);
    }

    public static <T> PagedResponse<T> of(org.springframework.data.domain.Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                (int) page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages()
        );
    }
}
