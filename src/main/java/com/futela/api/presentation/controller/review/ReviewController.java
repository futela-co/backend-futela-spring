package com.futela.api.presentation.controller.review;

import com.futela.api.application.dto.request.review.CreateReviewRequest;
import com.futela.api.application.dto.request.review.FlagReviewRequest;
import com.futela.api.application.dto.request.review.RespondToReviewRequest;
import com.futela.api.application.dto.request.review.UpdateReviewRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.review.ReviewResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.domain.port.in.review.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Avis", description = "Gestion des avis et notes sur les propriétés")
public class ReviewController {

    private final CreateReviewUseCase createReviewUseCase;
    private final GetReviewByIdUseCase getReviewByIdUseCase;
    private final GetPropertyReviewsUseCase getPropertyReviewsUseCase;
    private final GetApprovedReviewsUseCase getApprovedReviewsUseCase;
    private final GetPendingReviewsUseCase getPendingReviewsUseCase;
    private final UpdateReviewUseCase updateReviewUseCase;
    private final DeleteReviewUseCase deleteReviewUseCase;
    private final ApproveReviewUseCase approveReviewUseCase;
    private final RejectReviewUseCase rejectReviewUseCase;
    private final FlagReviewUseCase flagReviewUseCase;
    private final RespondToReviewUseCase respondToReviewUseCase;
    private final SecurityService securityService;

    @GetMapping("/api/reviews")
    @Operation(summary = "Liste des avis approuvés (paginée)")
    public ApiResponse<Page<ReviewResponse>> getApprovedReviews(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(getApprovedReviewsUseCase.execute(pageable));
    }

    @GetMapping("/api/reviews/pending")
    @Operation(summary = "Liste des avis en attente de modération (paginée)")
    public ApiResponse<Page<ReviewResponse>> getPendingReviews(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(getPendingReviewsUseCase.execute(pageable));
    }

    @PostMapping("/api/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un avis")
    public ApiResponse<ReviewResponse> create(@Valid @RequestBody CreateReviewRequest request) {
        UUID currentUserId = securityService.getCurrentUserId();
        ReviewResponse response = createReviewUseCase.execute(request, currentUserId);
        return ApiResponse.success(response, "Avis créé avec succès");
    }

    @GetMapping("/api/reviews/{id}")
    @Operation(summary = "Détail d'un avis")
    public ApiResponse<ReviewResponse> getById(@PathVariable UUID id) {
        ReviewResponse response = getReviewByIdUseCase.execute(id);
        return ApiResponse.success(response);
    }

    @GetMapping("/api/properties/{propertyId}/reviews")
    @Operation(summary = "Avis d'une propriété (approuvés uniquement)")
    public ApiResponse<List<ReviewResponse>> getPropertyReviews(@PathVariable UUID propertyId) {
        List<ReviewResponse> responses = getPropertyReviewsUseCase.execute(propertyId);
        return ApiResponse.success(responses);
    }

    @PutMapping("/api/reviews/{id}")
    @Operation(summary = "Modifier un avis (auteur uniquement)")
    public ApiResponse<ReviewResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReviewRequest request) {
        UUID currentUserId = securityService.getCurrentUserId();
        ReviewResponse response = updateReviewUseCase.execute(id, request, currentUserId);
        return ApiResponse.success(response, "Avis modifié avec succès");
    }

    @DeleteMapping("/api/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un avis")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        deleteReviewUseCase.execute(id);
        return ApiResponse.success(null, "Avis supprimé avec succès");
    }

    @PostMapping("/api/reviews/{id}/approve")
    @Operation(summary = "Approuver un avis (modération)")
    public ApiResponse<ReviewResponse> approve(@PathVariable UUID id) {
        ReviewResponse response = approveReviewUseCase.execute(id);
        return ApiResponse.success(response, "Avis approuvé avec succès");
    }

    @PostMapping("/api/reviews/{id}/reject")
    @Operation(summary = "Rejeter un avis (modération)")
    public ApiResponse<ReviewResponse> reject(@PathVariable UUID id) {
        ReviewResponse response = rejectReviewUseCase.execute(id);
        return ApiResponse.success(response, "Avis rejeté avec succès");
    }

    @PostMapping("/api/reviews/{id}/flag")
    @Operation(summary = "Signaler un avis")
    public ApiResponse<ReviewResponse> flag(
            @PathVariable UUID id,
            @Valid @RequestBody FlagReviewRequest request
    ) {
        ReviewResponse response = flagReviewUseCase.execute(id, request.reason());
        return ApiResponse.success(response, "Avis signalé avec succès");
    }

    @PostMapping("/api/reviews/{id}/respond")
    @Operation(summary = "Répondre à un avis (propriétaire uniquement)")
    public ApiResponse<ReviewResponse> respond(
            @PathVariable UUID id,
            @Valid @RequestBody RespondToReviewRequest request) {
        UUID currentUserId = securityService.getCurrentUserId();
        ReviewResponse response = respondToReviewUseCase.execute(id, request.response(), currentUserId);
        return ApiResponse.success(response, "Réponse ajoutée avec succès");
    }
}
