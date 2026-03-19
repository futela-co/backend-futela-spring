package com.futela.api.presentation.controller.messaging;

import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.messaging.NotificationResponse;
import com.futela.api.application.dto.response.messaging.UnreadCountResponse;
import com.futela.api.application.usecase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GetUserNotificationsService getUserNotificationsService;
    private final GetUnreadNotificationsService getUnreadNotificationsService;
    private final GetUnreadNotificationsCountService getUnreadNotificationsCountService;
    private final MarkNotificationAsReadService markNotificationAsReadService;
    private final MarkAllNotificationsAsReadService markAllNotificationsAsReadService;
    private final DeleteNotificationService deleteNotificationService;

    @GetMapping
    public ApiResponse<Page<NotificationResponse>> getUserNotifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ApiResponse.success(getUserNotificationsService.execute(userId, pageable));
    }

    @GetMapping("/unread")
    public ApiResponse<List<NotificationResponse>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ApiResponse.success(getUnreadNotificationsService.execute(userId));
    }

    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ApiResponse.success(getUnreadNotificationsCountService.execute(userId));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ApiResponse.success(markNotificationAsReadService.execute(id, userId));
    }

    @PostMapping("/mark-all-read")
    public ApiResponse<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        markAllNotificationsAsReadService.execute(userId);
        return ApiResponse.success(null, "Toutes les notifications marquées comme lues");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        deleteNotificationService.execute(id, userId);
        return ApiResponse.success(null, "Notification supprimée");
    }
}
