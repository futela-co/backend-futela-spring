package com.futela.api.application.dto.response.rent;

import java.math.BigDecimal;

public record YearlyIncomeResponse(
        int year,
        BigDecimal totalIncome
) {}
