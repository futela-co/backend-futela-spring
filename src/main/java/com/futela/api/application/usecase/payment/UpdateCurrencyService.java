package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.request.payment.UpdateCurrencyRequest;
import com.futela.api.application.dto.response.payment.CurrencyResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.payment.Currency;
import com.futela.api.domain.port.in.payment.UpdateCurrencyUseCase;
import com.futela.api.domain.port.out.payment.CurrencyRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UpdateCurrencyService implements UpdateCurrencyUseCase {
    private final CurrencyRepositoryPort currencyRepository;

    public UpdateCurrencyService(CurrencyRepositoryPort currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public CurrencyResponse execute(UUID currencyId, UpdateCurrencyRequest request) {
        Currency existing = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", currencyId.toString()));

        Currency updated = new Currency(
                existing.id(),
                existing.code(),
                request.name() != null ? request.name() : existing.name(),
                request.symbol() != null ? request.symbol() : existing.symbol(),
                request.exchangeRate() != null ? request.exchangeRate() : existing.exchangeRate(),
                request.isActive() != null ? request.isActive() : existing.isActive(),
                existing.createdAt(),
                existing.updatedAt()
        );

        return CurrencyResponse.from(currencyRepository.save(updated));
    }
}
