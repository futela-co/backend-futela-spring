package com.futela.api.presentation.controller.rent;

import com.futela.api.application.dto.request.rent.PayRentInvoiceRequest;
import com.futela.api.application.dto.request.rent.RecordRentPaymentRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.rent.RentPaymentResponse;
import com.futela.api.domain.port.in.rent.GetRentPaymentsByLeaseUseCase;
import com.futela.api.domain.port.in.rent.PayRentInvoiceUseCase;
import com.futela.api.domain.port.in.rent.RecordRentPaymentUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RentPaymentController {

    private final PayRentInvoiceUseCase payInvoiceUseCase;
    private final RecordRentPaymentUseCase recordPaymentUseCase;
    private final GetRentPaymentsByLeaseUseCase getPaymentsByLeaseUseCase;

    public RentPaymentController(PayRentInvoiceUseCase payInvoiceUseCase,
                                 RecordRentPaymentUseCase recordPaymentUseCase,
                                 GetRentPaymentsByLeaseUseCase getPaymentsByLeaseUseCase) {
        this.payInvoiceUseCase = payInvoiceUseCase;
        this.recordPaymentUseCase = recordPaymentUseCase;
        this.getPaymentsByLeaseUseCase = getPaymentsByLeaseUseCase;
    }

    // Symfony: POST /rent-invoices/{id}/pay
    @PostMapping("/rent-invoices/{invoiceId}/pay")
    public ApiResponse<RentPaymentResponse> pay(@PathVariable UUID invoiceId,
                                                 @Valid @RequestBody PayRentInvoiceRequest request) {
        return ApiResponse.success(payInvoiceUseCase.execute(invoiceId, request), "Paiement enregistré avec succès");
    }

    // Symfony: POST /rent-payments (record a payment)
    @PostMapping("/rent-payments")
    public ApiResponse<RentPaymentResponse> record(@Valid @RequestBody RecordRentPaymentRequest request) {
        // leaseId should be in the request body for Symfony compat
        return ApiResponse.success(recordPaymentUseCase.execute(null, request), "Paiement enregistré avec succès");
    }

    // Keep legacy route
    @PostMapping("/leases/{leaseId}/payments")
    public ApiResponse<RentPaymentResponse> recordLegacy(@PathVariable UUID leaseId,
                                                    @Valid @RequestBody RecordRentPaymentRequest request) {
        return ApiResponse.success(recordPaymentUseCase.execute(leaseId, request), "Paiement enregistré avec succès");
    }

    // Symfony: GET /leases/{leaseId}/payments
    @GetMapping("/leases/{leaseId}/payments")
    public ApiResponse<List<RentPaymentResponse>> getByLease(@PathVariable UUID leaseId) {
        return ApiResponse.success(getPaymentsByLeaseUseCase.execute(leaseId));
    }

    // Symfony: GET /rent-payments (admin)
    @GetMapping("/rent-payments")
    public ApiResponse<List<RentPaymentResponse>> getAll() {
        return ApiResponse.success(getPaymentsByLeaseUseCase.execute(null));
    }
}
