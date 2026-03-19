package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.MonthlyIncomeResponse;

import java.util.UUID;

public interface GetMonthlyIncomeReportUseCase {
    MonthlyIncomeResponse execute(UUID landlordId, int year);
}
