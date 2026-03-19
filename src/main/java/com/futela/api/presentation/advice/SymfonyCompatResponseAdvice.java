package com.futela.api.presentation.advice;

import com.futela.api.application.dto.response.common.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Unwraps ApiResponse to return data directly, matching Symfony API Platform behavior.
 * The frontend expects raw DTOs, not wrapped in {success, data, message, ...}.
 */
@RestControllerAdvice(basePackages = "com.futela.api.presentation.controller")
public class SymfonyCompatResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                   Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                   ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResponse<?> apiResponse) {
            // For error responses, return Symfony-compatible error format
            if (!apiResponse.success() && apiResponse.error() != null) {
                return new SymfonyErrorResponse(
                        apiResponse.error().message(),
                        apiResponse.error().code(),
                        apiResponse.error().details() != null
                                ? apiResponse.error().details().stream()
                                .collect(java.util.stream.Collectors.toMap(
                                        ApiResponse.FieldError::field,
                                        e -> java.util.List.of(e.message())
                                ))
                                : null
                );
            }
            // For success responses, return data directly (like Symfony API Platform)
            return apiResponse.data();
        }
        return body;
    }

    /**
     * Symfony-compatible error response format.
     * Frontend parseError() handles: message, error, detail, errors
     */
    public record SymfonyErrorResponse(
            String message,
            String code,
            java.util.Map<String, java.util.List<String>> errors
    ) {
    }
}
