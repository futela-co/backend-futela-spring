package com.futela.api.application.dto.response.rent;

import java.math.BigDecimal;

public record LandlordDashboardResponse(
        long totalProperties,
        long propertiesRented,
        BigDecimal monthlyIncome,
        BigDecimal yearlyIncome,
        long overduePayments,
        long pendingPayments,
        double occupancyRate
) {}
