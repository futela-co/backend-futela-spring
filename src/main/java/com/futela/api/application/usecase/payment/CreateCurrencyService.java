package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.request.payment.CreateCurrencyRequest;
import com.futela.api.application.dto.response.payment.CurrencyResponse;
import com.futela.api.domain.exception.DuplicateResourceException;
import com.futela.api.domain.model.payment.Currency;
import com.futela.api.domain.port.in.payment.CreateCurrencyUseCase;
import com.futela.api.domain.port.out.payment.CurrencyRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateCurrencyService implements CreateCurrencyUseCase {
    private final CurrencyRepositoryPort currencyRepository;

    public CreateCurrencyService(CurrencyRepositoryPort currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public CurrencyResponse execute(CreateCurrencyRequest request) {
        if (currencyRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("La devise " + request.code() + " existe déjà");
        }

        Currency currency = new Currency(
                null, request.code().toUpperCase(), request.name(), request.symbol(),
                request.exchangeRate(), true, null, null
        );

        return CurrencyResponse.from(currencyRepository.save(currency));
    }
}
