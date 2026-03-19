package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.ConvertCurrencyResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.payment.Currency;
import com.futela.api.domain.port.out.payment.CurrencyRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConvertCurrencyServiceTest {

    @Mock
    private CurrencyRepositoryPort currencyRepository;

    @InjectMocks
    private ConvertCurrencyService convertCurrencyService;

    @Test
    @DisplayName("Doit convertir CDF en USD correctement")
    void shouldConvertCDFToUSD() {
        Currency cdf = new Currency(UUID.randomUUID(), "CDF", "Franc Congolais", "FC",
                new BigDecimal("2800.00"), true, Instant.now(), Instant.now());
        Currency usd = new Currency(UUID.randomUUID(), "USD", "Dollar Américain", "$",
                new BigDecimal("1.00"), true, Instant.now(), Instant.now());

        when(currencyRepository.findByCode("CDF")).thenReturn(Optional.of(cdf));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usd));

        ConvertCurrencyResponse response = convertCurrencyService.execute("CDF", "USD", new BigDecimal("2800.00"));

        assertThat(response.from()).isEqualTo("CDF");
        assertThat(response.to()).isEqualTo("USD");
        assertThat(response.convertedAmount()).isEqualByComparingTo(new BigDecimal("1.00"));
    }

    @Test
    @DisplayName("Doit convertir USD en CDF correctement")
    void shouldConvertUSDToCDF() {
        Currency usd = new Currency(UUID.randomUUID(), "USD", "Dollar Américain", "$",
                new BigDecimal("1.00"), true, Instant.now(), Instant.now());
        Currency cdf = new Currency(UUID.randomUUID(), "CDF", "Franc Congolais", "FC",
                new BigDecimal("2800.00"), true, Instant.now(), Instant.now());

        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usd));
        when(currencyRepository.findByCode("CDF")).thenReturn(Optional.of(cdf));

        ConvertCurrencyResponse response = convertCurrencyService.execute("USD", "CDF", new BigDecimal("1.00"));

        assertThat(response.convertedAmount()).isEqualByComparingTo(new BigDecimal("2800.00"));
    }

    @Test
    @DisplayName("Doit retourner le même montant pour une conversion dans la même devise")
    void shouldReturnSameAmountForSameCurrency() {
        Currency usd = new Currency(UUID.randomUUID(), "USD", "Dollar Américain", "$",
                new BigDecimal("1.00"), true, Instant.now(), Instant.now());

        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usd));

        ConvertCurrencyResponse response = convertCurrencyService.execute("USD", "USD", new BigDecimal("50.00"));

        assertThat(response.convertedAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(response.rate()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Doit lever ResourceNotFoundException pour une devise source inexistante")
    void shouldThrowWhenSourceCurrencyNotFound() {
        when(currencyRepository.findByCode("XYZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> convertCurrencyService.execute("XYZ", "USD", new BigDecimal("100.00")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Doit lever ResourceNotFoundException pour une devise cible inexistante")
    void shouldThrowWhenTargetCurrencyNotFound() {
        Currency usd = new Currency(UUID.randomUUID(), "USD", "Dollar", "$",
                new BigDecimal("1.00"), true, Instant.now(), Instant.now());

        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usd));
        when(currencyRepository.findByCode("XYZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> convertCurrencyService.execute("USD", "XYZ", new BigDecimal("100.00")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
