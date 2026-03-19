package com.futela.api.domain.port.in.reservation;

import com.futela.api.application.dto.request.reservation.ScheduleVisitRequest;
import com.futela.api.application.dto.response.reservation.VisitResponse;

import java.util.UUID;

public interface ScheduleVisitUseCase {
    VisitResponse execute(ScheduleVisitRequest request, UUID currentUserId);
}
