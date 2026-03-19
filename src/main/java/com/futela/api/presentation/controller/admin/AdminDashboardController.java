package com.futela.api.presentation.controller.admin;

import com.futela.api.application.dto.response.admin.AdminDashboardResponse;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.domain.port.in.admin.GetAdminDashboardUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final GetAdminDashboardUseCase dashboardUseCase;

    public AdminDashboardController(GetAdminDashboardUseCase dashboardUseCase) {
        this.dashboardUseCase = dashboardUseCase;
    }

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardResponse> getDashboard() {
        return ApiResponse.success(dashboardUseCase.execute());
    }
}
