package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.RentReminderResponse;

import java.util.List;
import java.util.UUID;

public interface GetReminderHistoryUseCase {
    List<RentReminderResponse> execute(UUID leaseId);
}
