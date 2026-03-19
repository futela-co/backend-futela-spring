package com.futela.api.application.usecase.payment;

import com.futela.api.application.dto.response.common.PagedResponse;
import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.port.in.payment.GetAllTransactionsUseCase;
import com.futela.api.infrastructure.persistence.mapper.payment.TransactionMapper;
import com.futela.api.infrastructure.persistence.repository.payment.JpaTransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetAllTransactionsService implements GetAllTransactionsUseCase {

    private final JpaTransactionRepository transactionRepository;

    public GetAllTransactionsService(JpaTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public PagedResponse<TransactionResponse> execute(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = transactionRepository.findByDeletedAtIsNull(pageable);
        var responses = result.map(entity -> TransactionResponse.from(TransactionMapper.toDomain(entity)));
        return PagedResponse.of(responses);
    }
}
