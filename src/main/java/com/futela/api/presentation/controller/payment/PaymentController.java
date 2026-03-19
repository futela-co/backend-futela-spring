package com.futela.api.presentation.controller.payment;

import com.futela.api.application.dto.request.payment.InitiatePaymentRequest;
import com.futela.api.application.dto.request.payment.RefundPaymentRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.common.PagedResponse;
import com.futela.api.application.dto.response.payment.TransactionResponse;
import com.futela.api.domain.port.in.payment.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.futela.api.application.service.SecurityService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final InitiatePaymentUseCase initiatePaymentUseCase;
    private final GetTransactionByIdUseCase getTransactionByIdUseCase;
    private final GetUserTransactionsUseCase getUserTransactionsUseCase;
    private final GetPendingTransactionsUseCase getPendingTransactionsUseCase;
    private final GetAllTransactionsUseCase getAllTransactionsUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;
    private final RefundPaymentUseCase refundPaymentUseCase;
    private final SecurityService securityService;

    public PaymentController(InitiatePaymentUseCase initiatePaymentUseCase,
                             GetTransactionByIdUseCase getTransactionByIdUseCase,
                             GetUserTransactionsUseCase getUserTransactionsUseCase,
                             GetPendingTransactionsUseCase getPendingTransactionsUseCase,
                             GetAllTransactionsUseCase getAllTransactionsUseCase,
                             CancelPaymentUseCase cancelPaymentUseCase,
                             RefundPaymentUseCase refundPaymentUseCase,
                             SecurityService securityService) {
        this.initiatePaymentUseCase = initiatePaymentUseCase;
        this.getTransactionByIdUseCase = getTransactionByIdUseCase;
        this.getUserTransactionsUseCase = getUserTransactionsUseCase;
        this.getPendingTransactionsUseCase = getPendingTransactionsUseCase;
        this.getAllTransactionsUseCase = getAllTransactionsUseCase;
        this.cancelPaymentUseCase = cancelPaymentUseCase;
        this.refundPaymentUseCase = refundPaymentUseCase;
        this.securityService = securityService;
    }

    @PostMapping("/payments/initiate")
    public ApiResponse<TransactionResponse> initiate(@Valid @RequestBody InitiatePaymentRequest request) {
        return ApiResponse.success(initiatePaymentUseCase.execute(request), "Paiement initié avec succès");
    }

    @GetMapping("/transactions")
    public ApiResponse<PagedResponse<TransactionResponse>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return ApiResponse.success(getAllTransactionsUseCase.execute(page, size));
    }

    @GetMapping("/transactions/{id}")
    public ApiResponse<TransactionResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(getTransactionByIdUseCase.execute(id));
    }

    @GetMapping("/transactions/my")
    public ApiResponse<List<TransactionResponse>> getMyTransactions() {
        return ApiResponse.success(getUserTransactionsUseCase.execute(securityService.getCurrentUserId()));
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
