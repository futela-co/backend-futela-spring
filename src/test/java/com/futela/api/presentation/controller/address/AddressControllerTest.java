package com.futela.api.presentation.controller.address;

import com.futela.api.application.dto.response.address.CountryResponse;
import com.futela.api.application.dto.response.address.ProvinceResponse;
import com.futela.api.application.usecase.address.AddressUseCaseService;
import com.futela.api.application.service.SecurityService;
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

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressUseCaseService addressService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private EntityManager entityManager;

    @Test
    @DisplayName("Doit retourner la liste des pays avec un statut 200")
    @WithMockUser
    void shouldReturnCountriesWithStatus200() throws Exception {
        UUID countryId = UUID.randomUUID();
        var country = new CountryResponse(countryId, "Congo", "CD", "+243", true, Instant.now());

        when(addressService.getCountries()).thenReturn(List.of(country));

        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Congo"))
                .andExpect(jsonPath("$.data[0].code").value("CD"));
    }

    @Test
    @DisplayName("Doit rejeter la creation d'un pays sans authentification")
    void shouldRejectCreateCountryWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/admin/countries")
                        .contentType("application/json")
                        .content("""
                                {"name": "Congo", "code": "CD", "phoneCode": "+243"}
                                """))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assertThat(statusCode).isIn(401, 403);
                });
    }

    @Test
    @DisplayName("Doit retourner les provinces d'un pays avec un statut 200")
    @WithMockUser
    void shouldReturnProvincesByCountryWithStatus200() throws Exception {
        UUID countryId = UUID.randomUUID();
        UUID provinceId = UUID.randomUUID();
        var province = new ProvinceResponse(provinceId, "Kinshasa", "KIN", true,
                countryId, "Congo", Instant.now());

        when(addressService.getProvincesByCountry(any(UUID.class))).thenReturn(List.of(province));

        mockMvc.perform(get("/api/countries/{id}/provinces", countryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Kinshasa"));
    }
}
