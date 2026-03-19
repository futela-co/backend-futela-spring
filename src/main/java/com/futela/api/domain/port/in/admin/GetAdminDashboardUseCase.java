package com.futela.api.domain.port.in.admin;

import com.futela.api.application.dto.response.admin.AdminDashboardResponse;

public interface GetAdminDashboardUseCase {
    AdminDashboardResponse execute();
}
