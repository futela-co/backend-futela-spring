package com.futela.api.application.dto.response.rent;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyIncomeResponse(
        int year,
        BigDecimal totalIncome,
        List<MonthBreakdown> months
) {
    public record MonthBreakdown(
            int month,
            String monthName,
            BigDecimal income,
            long invoicesPaid,
            long invoicesOverdue
    ) {}
}
