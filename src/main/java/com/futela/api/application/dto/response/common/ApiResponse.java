package com.futela.api.application.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        ErrorDetail error,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, null, new ErrorDetail(code, message, null), Instant.now());
    }

    public static <T> ApiResponse<T> error(String code, String message, List<FieldError> details) {
        return new ApiResponse<>(false, null, null, new ErrorDetail(code, message, details), Instant.now());
    }

    public record ErrorDetail(
            String code,
            String message,
            List<FieldError> details
    ) {}

    public record FieldError(
            String field,
            String message
    ) {}
}
