package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.CurrencyResponse;
import com.futela.api.domain.port.in.payment.GetActiveCurrenciesUseCase;
import com.futela.api.domain.port.out.payment.CurrencyRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetActiveCurrenciesService implements GetActiveCurrenciesUseCase {
    private final CurrencyRepositoryPort currencyRepository;

    public GetActiveCurrenciesService(CurrencyRepositoryPort currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<CurrencyResponse> execute() {
        return currencyRepository.findActive().stream().map(CurrencyResponse::from).toList();
    }
}
