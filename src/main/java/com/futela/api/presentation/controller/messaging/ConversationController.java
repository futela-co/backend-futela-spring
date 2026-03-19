package com.futela.api.presentation.controller.messaging;

import com.futela.api.application.dto.request.messaging.CreateConversationRequest;
import com.futela.api.application.dto.request.messaging.SendMessageRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.application.dto.response.messaging.MessageResponse;
import com.futela.api.application.dto.response.messaging.UnreadCountResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.application.usecase.messaging.*;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConversationController {

    private final SecurityService securityService;
    private final CreateConversationService createConversationService;
    private final GetUserConversationsService getUserConversationsService;
    private final GetConversationByIdService getConversationByIdService;
    private final GetConversationMessagesService getConversationMessagesService;
    private final SendMessageService sendMessageService;
    private final MarkMessageAsReadService markMessageAsReadService;
    private final DeleteMessageService deleteMessageService;
    private final DeleteConversationService deleteConversationService;
    private final ArchiveConversationService archiveConversationService;
    private final SearchConversationsService searchConversationsService;
    private final GetUnreadMessagesCountService getUnreadMessagesCountService;

    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ConversationResponse> createConversation(
            @Valid @RequestBody CreateConversationRequest request) {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(createConversationService.execute(request, userId), "Conversation créée");
    }

    @GetMapping("/conversations")
    public ApiResponse<List<ConversationResponse>> getUserConversations(
            @RequestParam(defaultValue = "false") boolean includeArchived) {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(getUserConversationsService.execute(userId, includeArchived));
    }

    @GetMapping("/conversations/{id}")
    public ApiResponse<ConversationResponse> getConversationById(
            @PathVariable UUID id) {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(getConversationByIdService.execute(id, userId));
    }

    @GetMapping("/conversations/{id}/messages")
    public ApiResponse<Page<MessageResponse>> getConversationMessages(
            @PathVariable UUID id,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(getConversationMessagesService.execute(id, userId, pageable));
    }

    @PostMapping("/conversations/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MessageResponse> sendMessage(
            @PathVariable UUID id,
            @Valid @RequestBody SendMessageRequest request) {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(sendMessageService.execute(id, request, userId), "Message envoyé");
    }

    @PostMapping("/messages/{id}/read")
    public ApiResponse<MessageResponse> markMessageAsRead(
            @PathVariable UUID id) {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(markMessageAsReadService.execute(id, userId));
    }

    @DeleteMapping("/messages/{id}")
    public ApiResponse<Void> deleteMessage(
            @PathVariable UUID id) {
        UUID userId = securityService.getCurrentUserId();
        deleteMessageService.execute(id, userId);
        return ApiResponse.success(null, "Message supprimé");
    }

    @DeleteMapping("/conversations/{id}")
    public ApiResponse<Void> deleteConversation(
            @PathVariable UUID id) {
        UUID userId = securityService.getCurrentUserId();
        deleteConversationService.execute(id, userId);
        return ApiResponse.success(null, "Conversation supprimée");
    }

    @PostMapping("/conversations/{id}/archive")
    public ApiResponse<ConversationResponse> archiveConversation(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "true") boolean archive) {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(archiveConversationService.execute(id, userId, archive));
    }

    @GetMapping("/me/messages/unread")
    public ApiResponse<UnreadCountResponse> getUnreadMessagesCount() {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(getUnreadMessagesCountService.execute(userId));
    }
}
