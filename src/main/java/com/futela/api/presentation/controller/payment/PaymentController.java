package com.futela.api.presentation.controller.payment;

import com.futela.api.application.dto.request.payment.InitiatePaymentRequest;
import com.futela.api.application.dto.request.payment.RefundPaymentRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.port.in.payment.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final InitiatePaymentUseCase initiatePaymentUseCase;
    private final GetTransactionByIdUseCase getTransactionByIdUseCase;
    private final GetUserTransactionsUseCase getUserTransactionsUseCase;
    private final GetPendingTransactionsUseCase getPendingTransactionsUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;
    private final RefundPaymentUseCase refundPaymentUseCase;

    public PaymentController(InitiatePaymentUseCase initiatePaymentUseCase,
                             GetTransactionByIdUseCase getTransactionByIdUseCase,
                             GetUserTransactionsUseCase getUserTransactionsUseCase,
                             GetPendingTransactionsUseCase getPendingTransactionsUseCase,
                             CancelPaymentUseCase cancelPaymentUseCase,
                             RefundPaymentUseCase refundPaymentUseCase) {
        this.initiatePaymentUseCase = initiatePaymentUseCase;
        this.getTransactionByIdUseCase = getTransactionByIdUseCase;
        this.getUserTransactionsUseCase = getUserTransactionsUseCase;
        this.getPendingTransactionsUseCase = getPendingTransactionsUseCase;
        this.cancelPaymentUseCase = cancelPaymentUseCase;
        this.refundPaymentUseCase = refundPaymentUseCase;
    }

    @PostMapping("/payments/initiate")
    public ApiResponse<TransactionResponse> initiate(@Valid @RequestBody InitiatePaymentRequest request) {
        return ApiResponse.success(initiatePaymentUseCase.execute(request), "Paiement initié avec succès");
    }

    @GetMapping("/transactions/{id}")
    public ApiResponse<TransactionResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(getTransactionByIdUseCase.execute(id));
    }

    @GetMapping("/transactions/my")
    public ApiResponse<List<TransactionResponse>> getMyTransactions(@RequestParam UUID userId) {
        return ApiResponse.success(getUserTransactionsUseCase.execute(userId));
    }

    @GetMapping("/transactions/pending")
    public ApiResponse<List<TransactionResponse>> getPending() {
        return ApiResponse.success(getPendingTransactionsUseCase.execute());
    }

    @PatchMapping("/transactions/{id}/cancel")
    public ApiResponse<TransactionResponse> cancel(@PathVariable UUID id) {
        return ApiResponse.success(cancelPaymentUseCase.execute(id), "Transaction annulée avec succès");
    }

    @PostMapping("/payments/refund")
    public ApiResponse<TransactionResponse> refund(@Valid @RequestBody RefundPaymentRequest request) {
        return ApiResponse.success(refundPaymentUseCase.execute(request), "Remboursement initié avec succès");
    }
}
