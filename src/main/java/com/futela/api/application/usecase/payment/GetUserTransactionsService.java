package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.port.in.payment.GetUserTransactionsUseCase;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetUserTransactionsService implements GetUserTransactionsUseCase {
    private final TransactionRepositoryPort transactionRepository;

    public GetUserTransactionsService(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<TransactionResponse> execute(UUID userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(TransactionResponse::from).toList();
    }
}
