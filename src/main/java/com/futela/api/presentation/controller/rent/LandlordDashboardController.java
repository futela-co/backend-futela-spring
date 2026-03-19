package com.futela.api.presentation.controller.rent;

import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.rent.*;
import com.futela.api.domain.port.in.rent.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/landlord")
public class LandlordDashboardController {

    private final GetLandlordDashboardUseCase dashboardUseCase;
    private final GetMonthlyIncomeReportUseCase monthlyIncomeUseCase;
    private final GetOverduePaymentsUseCase overdueUseCase;
    private final GetPendingPaymentsUseCase pendingUseCase;

    public LandlordDashboardController(GetLandlordDashboardUseCase dashboardUseCase,
                                       GetMonthlyIncomeReportUseCase monthlyIncomeUseCase,
                                       GetOverduePaymentsUseCase overdueUseCase,
                                       GetPendingPaymentsUseCase pendingUseCase) {
        this.dashboardUseCase = dashboardUseCase;
        this.monthlyIncomeUseCase = monthlyIncomeUseCase;
        this.overdueUseCase = overdueUseCase;
        this.pendingUseCase = pendingUseCase;
    }

    @GetMapping("/dashboard")
    public ApiResponse<LandlordDashboardResponse> getDashboard(@RequestParam UUID landlordId) {
        return ApiResponse.success(dashboardUseCase.execute(landlordId));
    }

    @GetMapping("/income/monthly")
    public ApiResponse<MonthlyIncomeResponse> getMonthlyIncome(@RequestParam UUID landlordId,
                                                                @RequestParam int year) {
        return ApiResponse.success(monthlyIncomeUseCase.execute(landlordId, year));
    }

    @GetMapping("/payments/overdue")
    public ApiResponse<List<RentInvoiceResponse>> getOverdue(@RequestParam UUID landlordId) {
        return ApiResponse.success(overdueUseCase.execute(landlordId));
    }

    @GetMapping("/payments/pending")
    public ApiResponse<List<RentInvoiceResponse>> getPending(@RequestParam UUID landlordId) {
        return ApiResponse.success(pendingUseCase.execute(landlordId));
    }
}
