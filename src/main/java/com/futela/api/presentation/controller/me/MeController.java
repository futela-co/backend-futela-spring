package com.futela.api.presentation.controller.me;

import com.futela.api.application.dto.request.auth.ChangePasswordRequest;
import com.futela.api.application.dto.request.auth.UpdateProfileRequest;
import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.common.PagedResponse;
import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.application.dto.response.rent.RentInvoiceResponse;
import com.futela.api.application.dto.response.rent.RentPaymentResponse;
import com.futela.api.application.dto.response.reservation.ReservationResponse;
import com.futela.api.application.dto.response.reservation.VisitResponse;
import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.mapper.reservation.ReservationResponseMapper;
import com.futela.api.application.mapper.reservation.VisitResponseMapper;
import com.futela.api.application.service.SecurityService;
import com.futela.api.application.usecase.property.PropertyUseCaseService;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.auth.ChangePasswordUseCase;
import com.futela.api.domain.port.in.auth.GetCurrentUserUseCase;
import com.futela.api.domain.port.in.auth.UpdateProfileUseCase;
import com.futela.api.domain.port.in.payment.GetUserTransactionsUseCase;
import com.futela.api.domain.port.in.rent.GetTenantLeasesUseCase;
import com.futela.api.domain.port.in.rent.GetTenantRentInvoicesUseCase;
import com.futela.api.domain.port.in.rent.GetTenantRentPaymentsUseCase;
import com.futela.api.domain.port.in.review.GetUserReviewsUseCase;
import com.futela.api.infrastructure.persistence.repository.auth.JpaUserRepository;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaReservationRepository;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaVisitRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.domain.enums.VisitStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles all /api/me/* routes to match Symfony API Platform endpoints.
 * The frontend calls /api/me/properties, /api/me/leases, etc.
 */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "Mon compte", description = "Endpoints du profil et des données de l'utilisateur connecté")
public class MeController {

    private final PropertyUseCaseService propertyService;
    private final SecurityService securityService;
    private final JpaReservationRepository reservationRepository;
    private final JpaVisitRepository visitRepository;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final JpaUserRepository userRepository;
    private final GetUserTransactionsUseCase getUserTransactionsUseCase;
    private final GetTenantLeasesUseCase getTenantLeasesUseCase;
    private final GetTenantRentInvoicesUseCase getTenantRentInvoicesUseCase;
    private final GetTenantRentPaymentsUseCase getTenantRentPaymentsUseCase;
    private final GetUserReviewsUseCase getUserReviewsUseCase;

    // ─── Profile ───────────────────────────────────────────────────────

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Mon profil", description = "Récupérer les informations de l'utilisateur connecté")
    public ApiResponse<UserResponse> getProfile() {
        UUID userId = securityService.getCurrentUserId();
        UserResponse response = getCurrentUserUseCase.execute(userId);
        return ApiResponse.success(response);
    }

    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Modifier mon profil", description = "Mettre à jour les informations du profil")
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UUID userId = securityService.getCurrentUserId();
        UserResponse response = updateProfileUseCase.execute(userId, request);
        return ApiResponse.success(response, "Profil mis à jour avec succès");
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Changer mon mot de passe", description = "Modifier le mot de passe de l'utilisateur connecté")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UUID userId = securityService.getCurrentUserId();
        changePasswordUseCase.execute(userId, request);
        return ApiResponse.success(null, "Mot de passe modifié avec succès");
    }

    @PutMapping("/availability")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    @Operation(summary = "Basculer ma disponibilité", description = "Activer ou désactiver la disponibilité de l'utilisateur")
    public ApiResponse<Map<String, Boolean>> toggleAvailability() {
        UUID userId = securityService.getCurrentUserId();
        var user = userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId.toString()));
        user.setAvailable(!user.isAvailable());
        userRepository.save(user);
        return ApiResponse.success(
                Map.of("isAvailable", user.isAvailable()),
                "Disponibilité mise à jour avec succès"
        );
    }

    // ─── Properties ────────────────────────────────────────────────────

    @GetMapping("/properties")
    public ApiResponse<PagedResponse<PropertySummaryResponse>> getMyProperties(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        UUID ownerId = securityService.getCurrentUserId();
        var result = propertyService.getPropertiesByOwner(ownerId, page, size);
        var paged = new PagedResponse<>(result.content(), result.page(), result.size(),
                result.totalElements(), result.totalPages());
        return ApiResponse.success(paged);
    }

    @GetMapping("/properties/{id}")
    public ApiResponse<?> getMyProperty(@PathVariable UUID id) {
        return ApiResponse.success(propertyService.getPropertyById(id));
    }

    // ─── Reservations & Visits ─────────────────────────────────────────

    @GetMapping("/reservations")
    @Transactional(readOnly = true)
    public ApiResponse<List<ReservationResponse>> getMyReservations(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        UUID userId = securityService.getCurrentUserId();
        var reservations = reservationRepository.findByUserIdAndDeletedAtIsNull(userId);
        var responses = reservations.stream()
                .limit(limit)
                .map(ReservationResponseMapper::fromEntity)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/bookings")
    @Transactional(readOnly = true)
    @Operation(summary = "Mes réservations reçues", description = "Réservations dont l'utilisateur connecté est le propriétaire")
    public ApiResponse<PagedResponse<ReservationResponse>> getMyBookings(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        UUID userId = securityService.getCurrentUserId();
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = reservationRepository.findByHostIdAndDeletedAtIsNull(userId, pageable);
        var paged = PagedResponse.of(result.map(ReservationResponseMapper::fromEntity));
        return ApiResponse.success(paged);
    }

    @GetMapping("/reservations/incoming")
    @Transactional(readOnly = true)
    @Operation(summary = "Mes réservations en cours", description = "Réservations en attente ou confirmées")
    public ApiResponse<PagedResponse<ReservationResponse>> getMyIncomingReservations(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        UUID userId = securityService.getCurrentUserId();
        var statuses = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = reservationRepository.findByUserIdAndStatusInAndDeletedAtIsNull(userId, statuses, pageable);
        var paged = PagedResponse.of(result.map(ReservationResponseMapper::fromEntity));
        return ApiResponse.success(paged);
    }

    @GetMapping("/reservations/past")
    @Transactional(readOnly = true)
    @Operation(summary = "Mes réservations passées", description = "Réservations terminées ou annulées")
    public ApiResponse<PagedResponse<ReservationResponse>> getMyPastReservations(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        UUID userId = securityService.getCurrentUserId();
        var statuses = List.of(ReservationStatus.COMPLETED, ReservationStatus.CANCELLED);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = reservationRepository.findByUserIdAndStatusInAndDeletedAtIsNull(userId, statuses, pageable);
        var paged = PagedResponse.of(result.map(ReservationResponseMapper::fromEntity));
        return ApiResponse.success(paged);
    }

    @GetMapping("/visits/upcoming")
    @Transactional(readOnly = true)
    public ApiResponse<List<?>> getMyUpcomingVisits() {
        UUID userId = securityService.getCurrentUserId();
        var visits = visitRepository.findByUserIdAndDeletedAtIsNull(userId);
        var responses = visits.stream()
                .map(VisitResponseMapper::fromEntity)
                .toList();
        return ApiResponse.success(responses);
    }

    @GetMapping("/visits/past")
    @Transactional(readOnly = true)
    @Operation(summary = "Mes visites passées", description = "Visites effectuées ou annulées")
    public ApiResponse<PagedResponse<VisitResponse>> getMyPastVisits(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        UUID userId = securityService.getCurrentUserId();
        var statuses = List.of(VisitStatus.COMPLETED, VisitStatus.CANCELLED);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = visitRepository.findByUserIdAndStatusInAndDeletedAtIsNull(userId, statuses, pageable);
        var paged = PagedResponse.of(result.map(VisitResponseMapper::fromEntity));
        return ApiResponse.success(paged);
    }

    // ─── Transactions ─────────────────────────────────────────────────

    @GetMapping("/transactions")
    @Operation(summary = "Mes transactions", description = "Récupérer les transactions de l'utilisateur connecté")
    public ApiResponse<List<TransactionResponse>> getMyTransactions() {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(getUserTransactionsUseCase.execute(userId));
    }

    // ─── Leases ───────────────────────────────────────────────────────

    @GetMapping("/leases")
    @Operation(summary = "Mes baux", description = "Récupérer les baux du locataire connecté")
    public ApiResponse<List<LeaseResponse>> getMyLeases() {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(getTenantLeasesUseCase.execute(userId));
    }

    // ─── Rent Invoices ────────────────────────────────────────────────

    @GetMapping("/rent-invoices")
    @Operation(summary = "Mes factures de loyer", description = "Récupérer les factures de loyer du locataire connecté")
    public ApiResponse<List<RentInvoiceResponse>> getMyRentInvoices() {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(getTenantRentInvoicesUseCase.execute(userId));
    }

    // ─── Rent Payments ────────────────────────────────────────────────

    @GetMapping("/rent-payments")
    @Operation(summary = "Mes paiements de loyer", description = "Récupérer les paiements de loyer du locataire connecté")
    public ApiResponse<List<RentPaymentResponse>> getMyRentPayments() {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(getTenantRentPaymentsUseCase.execute(userId));
    }

    // ─── Reviews ──────────────────────────────────────────────────────

    @GetMapping("/reviews")
    @Operation(summary = "Mes avis", description = "Récupérer les avis laissés par l'utilisateur connecté")
    public ApiResponse<List<ReviewResponse>> getMyReviews() {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(getUserReviewsUseCase.execute(userId));
    }
}
