package com.futela.api.presentation.controller.messaging;

import com.futela.api.application.usecase.messaging.*;
import com.futela.api.infrastructure.security.CorsProperties;
import com.futela.api.infrastructure.security.CustomUserDetailsService;
import com.futela.api.infrastructure.security.JwtAuthenticationFilter;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import com.futela.api.presentation.filter.TenantContextFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetUserNotificationsService getUserNotificationsService;

    @MockitoBean
    private GetUnreadNotificationsService getUnreadNotificationsService;

    @MockitoBean
    private GetUnreadNotificationsCountService getUnreadNotificationsCountService;

    @MockitoBean
    private MarkNotificationAsReadService markNotificationAsReadService;

    @MockitoBean
    private MarkAllNotificationsAsReadService markAllNotificationsAsReadService;

    @MockitoBean
    private DeleteNotificationService deleteNotificationService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CorsProperties corsProperties;

    @MockitoBean
    private TenantContextFilter tenantContextFilter;

    @Test
    @DisplayName("GET /api/notifications sans token retourne 401 Unauthorized")
    void shouldReturn401WhenGettingNotificationsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/notifications/mark-all-read sans token retourne 403 Forbidden")
    void shouldReturn403WhenMarkingAllAsReadWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/notifications/mark-all-read"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/notifications/unread-count sans token retourne 401 Unauthorized")
    void shouldReturn401WhenGettingUnreadCountWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/notifications/unread-count"))
                .andExpect(status().isUnauthorized());
    }
}
