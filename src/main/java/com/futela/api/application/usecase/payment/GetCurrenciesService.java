package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.CurrencyResponse;
import com.futela.api.domain.port.in.payment.GetCurrenciesUseCase;
import com.futela.api.domain.port.out.payment.CurrencyRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetCurrenciesService implements GetCurrenciesUseCase {
    private final CurrencyRepositoryPort currencyRepository;

    public GetCurrenciesService(CurrencyRepositoryPort currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<CurrencyResponse> execute() {
        return currencyRepository.findAll().stream().map(CurrencyResponse::from).toList();
    }
}
