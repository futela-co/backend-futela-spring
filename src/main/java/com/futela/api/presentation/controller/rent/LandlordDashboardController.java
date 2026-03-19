package com.futela.api.presentation.controller.rent;

import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.rent.*;
import com.futela.api.application.service.SecurityService;
import com.futela.api.domain.port.in.rent.*;
import com.futela.api.domain.port.out.rent.RentPaymentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/landlord")
@RequiredArgsConstructor
public class LandlordDashboardController {

    private final SecurityService securityService;
    private final GetLandlordDashboardUseCase dashboardUseCase;
    private final GetMonthlyIncomeReportUseCase monthlyIncomeUseCase;
    private final GetOverduePaymentsUseCase overdueUseCase;
    private final GetPendingPaymentsUseCase pendingUseCase;
    private final GetLandlordLeasesUseCase leasesUseCase;
    private final GetLandlordTenantsUseCase tenantsUseCase;
    private final RentPaymentRepositoryPort paymentRepository;

    @GetMapping("/dashboard")
    public ApiResponse<LandlordDashboardResponse> getDashboard() {
        return ApiResponse.success(dashboardUseCase.execute(securityService.getCurrentUserId()));
    }

    @GetMapping("/income/monthly")
    public ApiResponse<MonthlyIncomeResponse> getMonthlyIncome(@RequestParam int year) {
        return ApiResponse.success(monthlyIncomeUseCase.execute(securityService.getCurrentUserId(), year));
    }

    @GetMapping("/payments/overdue")
    public ApiResponse<List<RentInvoiceResponse>> getOverdue() {
        return ApiResponse.success(overdueUseCase.execute(securityService.getCurrentUserId()));
    }

    @GetMapping("/payments/pending")
    public ApiResponse<List<RentInvoiceResponse>> getPending() {
        return ApiResponse.success(pendingUseCase.execute(securityService.getCurrentUserId()));
    }

    @GetMapping("/leases")
    public ApiResponse<List<LeaseResponse>> getLeases() {
        return ApiResponse.success(leasesUseCase.execute(securityService.getCurrentUserId()));
    }

    @GetMapping("/tenants")
    public ApiResponse<List<TenantSummaryResponse>> getTenants() {
        return ApiResponse.success(tenantsUseCase.execute(securityService.getCurrentUserId()));
    }

    @GetMapping("/income/yearly")
    public ApiResponse<YearlyIncomeResponse> getYearlyIncome(@RequestParam int year) {
        BigDecimal totalIncome = paymentRepository.sumByLandlordIdAndYear(
                securityService.getCurrentUserId(), year);
        return ApiResponse.success(new YearlyIncomeResponse(year, totalIncome));
    }
}
