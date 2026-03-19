package com.futela.api.application.dto.response.admin;

public record AdminDashboardResponse(
        long totalUsers,
        long totalProperties,
        long totalReservations,
        long totalReviews,
        long pendingReviews,
        long totalTransactions,
        long totalLeases
) {}
