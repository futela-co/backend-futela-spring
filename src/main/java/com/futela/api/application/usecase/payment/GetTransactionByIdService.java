package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.payment.GetTransactionByIdUseCase;
import com.futela.api.domain.port.out.payment.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetTransactionByIdService implements GetTransactionByIdUseCase {
    private final TransactionRepositoryPort transactionRepository;

    public GetTransactionByIdService(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionResponse execute(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId.toString()));
    }
}
