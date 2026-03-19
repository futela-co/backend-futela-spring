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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateConversationService createConversationService;

    @MockitoBean
    private GetUserConversationsService getUserConversationsService;

    @MockitoBean
    private GetConversationByIdService getConversationByIdService;

    @MockitoBean
    private GetConversationMessagesService getConversationMessagesService;

    @MockitoBean
    private SendMessageService sendMessageService;

    @MockitoBean
    private MarkMessageAsReadService markMessageAsReadService;

    @MockitoBean
    private DeleteMessageService deleteMessageService;

    @MockitoBean
    private DeleteConversationService deleteConversationService;

    @MockitoBean
    private ArchiveConversationService archiveConversationService;

    @MockitoBean
    private SearchConversationsService searchConversationsService;

    @MockitoBean
    private GetUnreadMessagesCountService getUnreadMessagesCountService;

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
    @DisplayName("POST /api/conversations sans token retourne une erreur d'accès")
    void shouldDenyAccessWhenCreatingConversationWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/conversations")
                        .contentType("application/json")
                        .content("{\"participant2Id\":\"" + java.util.UUID.randomUUID() + "\",\"subject\":\"Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/conversations sans token retourne 401 Unauthorized")
    void shouldReturn401WhenGettingConversationsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/conversations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/messages/unread/count sans token retourne 401 Unauthorized")
    void shouldReturn401WhenGettingUnreadCountWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/messages/unread/count"))
                .andExpect(status().isUnauthorized());
    }
}
