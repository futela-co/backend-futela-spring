package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.enums.TransactionStatus;
import com.futela.api.domain.port.in.payment.GetPendingTransactionsUseCase;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetPendingTransactionsService implements GetPendingTransactionsUseCase {
    private final TransactionRepositoryPort transactionRepository;

    public GetPendingTransactionsService(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<TransactionResponse> execute() {
        return transactionRepository.findByStatus(TransactionStatus.PENDING).stream()
                .map(TransactionResponse::from).toList();
    }
}
