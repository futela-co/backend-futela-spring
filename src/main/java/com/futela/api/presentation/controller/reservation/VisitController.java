package com.futela.api.presentation.controller.reservation;

import com.futela.api.application.dto.request.reservation.ScheduleVisitRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.reservation.VisitResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.domain.port.in.reservation.CancelVisitUseCase;
import com.futela.api.domain.port.in.reservation.ConfirmVisitUseCase;
import com.futela.api.domain.port.in.reservation.ScheduleVisitUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@Tag(name = "Visites", description = "Gestion des visites de propriétés")
public class VisitController {

    private final ScheduleVisitUseCase scheduleVisitUseCase;
    private final ConfirmVisitUseCase confirmVisitUseCase;
    private final CancelVisitUseCase cancelVisitUseCase;
    private final SecurityService securityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Programmer une visite")
    public ApiResponse<VisitResponse> schedule(@Valid @RequestBody ScheduleVisitRequest request) {
        UUID currentUserId = securityService.getCurrentUserId();
        VisitResponse response = scheduleVisitUseCase.execute(request, currentUserId);
        return ApiResponse.success(response, "Visite programmée avec succès");
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirmer une visite")
    public ApiResponse<VisitResponse> confirm(@PathVariable UUID id) {
        VisitResponse response = confirmVisitUseCase.execute(id);
        return ApiResponse.success(response, "Visite confirmée avec succès");
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler une visite")
    public ApiResponse<VisitResponse> cancel(@PathVariable UUID id) {
        VisitResponse response = cancelVisitUseCase.execute(id);
        return ApiResponse.success(response, "Visite annulée avec succès");
    }
}
