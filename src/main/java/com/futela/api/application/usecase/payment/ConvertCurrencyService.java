package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.ConvertCurrencyResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.payment.Currency;
import com.futela.api.domain.port.in.payment.ConvertCurrencyUseCase;
import com.futela.api.domain.port.out.payment.CurrencyRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional(readOnly = true)
public class ConvertCurrencyService implements ConvertCurrencyUseCase {
    private final CurrencyRepositoryPort currencyRepository;

    public ConvertCurrencyService(CurrencyRepositoryPort currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public ConvertCurrencyResponse execute(String from, String to, BigDecimal amount) {
        Currency fromCurrency = currencyRepository.findByCode(from)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", from));

        Currency toCurrency = currencyRepository.findByCode(to)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", to));

        // Convert: amount / fromRate * toRate
        BigDecimal rate = toCurrency.exchangeRate()
                .divide(fromCurrency.exchangeRate(), 6, RoundingMode.HALF_UP);

        BigDecimal converted = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        return new ConvertCurrencyResponse(from, to, amount, converted, rate);
    }
}
