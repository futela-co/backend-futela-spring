package com.futela.api.presentation.controller.reservation;

import com.futela.api.application.dto.request.reservation.CancelReservationRequest;
import com.futela.api.application.dto.request.reservation.CreateReservationRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.domain.port.in.reservation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Gestion des réservations courte durée")
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final GetReservationByIdUseCase getReservationByIdUseCase;
    private final ConfirmReservationUseCase confirmReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;
    private final CompleteReservationUseCase completeReservationUseCase;
    private final SecurityService securityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une réservation")
    public ApiResponse<ReservationResponse> create(@Valid @RequestBody CreateReservationRequest request) {
        UUID currentUserId = securityService.getCurrentUserId();
        ReservationResponse response = createReservationUseCase.execute(request, currentUserId);
        return ApiResponse.success(response, "Réservation créée avec succès");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une réservation")
    public ApiResponse<ReservationResponse> getById(@PathVariable UUID id) {
        ReservationResponse response = getReservationByIdUseCase.execute(id);
        return ApiResponse.success(response);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirmer une réservation")
    public ApiResponse<ReservationResponse> confirm(@PathVariable UUID id) {
        ReservationResponse response = confirmReservationUseCase.execute(id);
        return ApiResponse.success(response, "Réservation confirmée avec succès");
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler une réservation")
    public ApiResponse<ReservationResponse> cancel(
            @PathVariable UUID id,
            @RequestBody(required = false) CancelReservationRequest request
    ) {
        ReservationResponse response = cancelReservationUseCase.execute(id, request);
        return ApiResponse.success(response, "Réservation annulée avec succès");
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Terminer une réservation")
    public ApiResponse<ReservationResponse> complete(@PathVariable UUID id) {
        ReservationResponse response = completeReservationUseCase.execute(id);
        return ApiResponse.success(response, "Réservation terminée avec succès");
    }
}
