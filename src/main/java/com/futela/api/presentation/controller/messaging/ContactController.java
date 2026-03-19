package com.futela.api.presentation.controller.messaging;

import com.futela.api.application.dto.request.messaging.RespondToContactRequest;
import com.futela.api.application.dto.request.messaging.SubmitContactRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.application.usecase.messaging.GetContactByIdService;
import com.futela.api.application.usecase.messaging.GetContactSubmissionsService;
import com.futela.api.application.usecase.messaging.RespondToContactService;
import com.futela.api.application.service.SecurityService;
import com.futela.api.application.usecase.messaging.SubmitContactFormService;
import com.futela.api.domain.enums.ContactStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ContactController {

    private final SecurityService securityService;
    private final SubmitContactFormService submitContactFormService;
    private final GetContactSubmissionsService getContactSubmissionsService;
    private final GetContactByIdService getContactByIdService;
    private final RespondToContactService respondToContactService;

    // Public endpoint - no authentication required
    @PostMapping("/api/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContactResponse> submitContactForm(
            @Valid @RequestBody SubmitContactRequest request) {
        return ApiResponse.success(submitContactFormService.execute(request), "Formulaire de contact soumis avec succès");
    }

    // Admin endpoints
    @GetMapping("/api/admin/contacts")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<ContactResponse>> getContactSubmissions(
            @RequestParam(required = false) ContactStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(getContactSubmissionsService.execute(status, pageable));
    }

    @GetMapping("/api/admin/contacts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ContactResponse> getContactById(@PathVariable UUID id) {
        return ApiResponse.success(getContactByIdService.execute(id));
    }

    @PostMapping("/api/admin/contacts/{id}/respond")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ContactResponse> respondToContact(
            @PathVariable UUID id,
            @Valid @RequestBody RespondToContactRequest request) {
        UUID adminId = securityService.getCurrentUserId();
        return ApiResponse.success(respondToContactService.execute(id, request, adminId), "Réponse envoyée");
    }
}
