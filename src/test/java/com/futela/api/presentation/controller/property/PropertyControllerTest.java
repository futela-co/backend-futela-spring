package com.futela.api.presentation.controller.property;

import com.futela.api.application.dto.response.property.PropertyResponse;
import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.application.usecase.photo.PhotoUseCaseService;
import com.futela.api.application.usecase.property.PropertyUseCaseService;
import com.futela.api.domain.enums.ListingType;
import com.futela.api.domain.enums.PropertyStatus;
import com.futela.api.domain.enums.PropertyType;
import com.futela.api.domain.model.common.PageResult;
import com.futela.api.domain.port.out.property.PropertySearchCriteria;
import com.futela.api.infrastructure.security.CustomUserDetailsService;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PropertyController.class)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropertyUseCaseService propertyService;

    @MockitoBean
    private PhotoUseCaseService photoService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private EntityManager entityManager;

    @Test
    @DisplayName("Doit retourner la recherche de proprietes publique avec un statut 200")
    @WithMockUser
    void shouldReturnPublicSearchWithStatus200() throws Exception {
        var summary = new PropertySummaryResponse(
                UUID.randomUUID(), "Bel Appartement", PropertyType.APARTMENT,
                PropertyStatus.PUBLISHED, ListingType.RENT, BigDecimal.valueOf(50),
                "bel-appartement-abc", true, true, null, 0,
                null, "Lubumbashi, Katanga", 3, 2, 80, Instant.now()
        );
        var pageResult = new PageResult<>(List.of(summary), 0, 20, 1L);

        when(propertyService.searchProperties(any(PropertySearchCriteria.class))).thenReturn(pageResult);

        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Bel Appartement"));
    }

    @Test
    @DisplayName("Doit retourner une propriete par son slug avec un statut 200")
    @WithMockUser
    void shouldReturnPropertyBySlugWithStatus200() throws Exception {
        String slug = "bel-appartement-abc12345";
        var response = new PropertyResponse(
                UUID.randomUUID(), "Bel Appartement", "Description",
                PropertyType.APARTMENT, PropertyStatus.PUBLISHED, ListingType.RENT,
                BigDecimal.valueOf(50), BigDecimal.valueOf(500), null,
                slug, true, true, true, 10, 4.5, 3,
                UUID.randomUUID(), "John Doe", null, null, UUID.randomUUID(),
                "10 Avenue Lumumba, Lubumbashi",
                3, 2, 80, null, null, null, null, null, null,
                null, null, null, null, List.of(),
                Instant.now(), Instant.now()
        );

        when(propertyService.getPropertyBySlug(slug)).thenReturn(response);

        mockMvc.perform(get("/api/properties/{slug}", slug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Bel Appartement"))
                .andExpect(jsonPath("$.slug").value(slug));
    }

    @Test
    @DisplayName("Doit rejeter la creation d'une propriete sans authentification")
    void shouldRejectCreatePropertyWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/properties")
                        .contentType("application/json")
                        .param("ownerId", UUID.randomUUID().toString())
                        .param("companyId", UUID.randomUUID().toString())
                        .content("""
                                {
                                    "title": "Appartement",
                                    "type": "APARTMENT",
                                    "listingType": "RENT",
                                    "pricePerDay": 50,
                                    "addressId": "00000000-0000-0000-0000-000000000001"
                                }
                                """))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assertThat(statusCode).isIn(401, 403);
                });
    }

    @Test
    @DisplayName("Doit exiger l'authentification pour les proprietes de l'utilisateur")
    void shouldRequireAuthForMyProperties() throws Exception {
        mockMvc.perform(get("/api/properties/my")
                        .param("ownerId", UUID.randomUUID().toString()))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assertThat(statusCode).isIn(401, 403);
                });
    }

    @Test
    @DisplayName("Doit retourner les proprietes de l'utilisateur avec authentification")
    @WithMockUser
    void shouldReturnMyPropertiesWithAuth() throws Exception {
        UUID ownerId = UUID.randomUUID();
        var summary = new PropertySummaryResponse(
                UUID.randomUUID(), "Mon Appartement", PropertyType.APARTMENT,
                PropertyStatus.DRAFT, ListingType.RENT, BigDecimal.valueOf(50),
                "mon-appartement-abc", false, true, null, 0,
                null, "Lubumbashi", 2, 1, 60, Instant.now()
        );
        var pageResult = new PageResult<>(List.of(summary), 0, 20, 1L);

        when(propertyService.getPropertiesByOwner(any(UUID.class), any(int.class), any(int.class)))
                .thenReturn(pageResult);

        mockMvc.perform(get("/api/properties/my")
                        .param("ownerId", ownerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
