package com.futela.api.presentation.controller.messaging;

import com.futela.api.application.dto.response.messaging.ContactResponse;
import com.futela.api.application.usecase.messaging.GetContactByIdService;
import com.futela.api.application.usecase.messaging.GetContactSubmissionsService;
import com.futela.api.application.usecase.messaging.RespondToContactService;
import com.futela.api.application.usecase.messaging.SubmitContactFormService;
import com.futela.api.domain.enums.ContactStatus;
import com.futela.api.infrastructure.security.CorsProperties;
import com.futela.api.infrastructure.security.CustomUserDetailsService;
import com.futela.api.infrastructure.security.JwtAuthenticationFilter;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import com.futela.api.presentation.filter.TenantContextFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubmitContactFormService submitContactFormService;

    @MockitoBean
    private GetContactSubmissionsService getContactSubmissionsService;

    @MockitoBean
    private GetContactByIdService getContactByIdService;

    @MockitoBean
    private RespondToContactService respondToContactService;

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
    @DisplayName("POST /api/contact avec un body valide retourne 201")
    void shouldReturn201WhenSubmittingValidContactForm() throws Exception {
        ContactResponse mockResponse = new ContactResponse(
                UUID.randomUUID(), "Jean", "Dupont", "jean@test.com", null,
                "Question", "Mon message de test pour le formulaire.",
                ContactStatus.NEW, "Nouveau", "blue", null, null, null, Instant.now()
        );

        when(submitContactFormService.execute(any())).thenReturn(mockResponse);

        String jsonBody = """
                {
                    "firstName": "Jean",
                    "lastName": "Dupont",
                    "email": "jean@test.com",
                    "subject": "Question",
                    "message": "Mon message de test pour le formulaire."
                }
                """;

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jean"));
    }

    @Test
    @DisplayName("POST /api/contact avec champs manquants retourne 400")
    void shouldReturn400WhenMissingRequiredFields() throws Exception {
        String jsonBody = """
                {
                    "firstName": "Jean"
                }
                """;

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/contact avec email invalide retourne 400")
    void shouldReturn400WhenEmailInvalid() throws Exception {
        String jsonBody = """
                {
                    "firstName": "Jean",
                    "lastName": "Dupont",
                    "email": "not-an-email",
                    "subject": "Question",
                    "message": "Mon message de test pour le formulaire."
                }
                """;

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }
}
