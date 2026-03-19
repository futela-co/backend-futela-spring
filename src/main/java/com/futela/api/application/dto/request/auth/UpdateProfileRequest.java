package com.futela.api.application.dto.request.auth;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String phone,
        String avatar
) {}
