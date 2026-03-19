package com.futela.api.presentation.controller.rent;

import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.rent.RentInvoiceResponse;
import com.futela.api.domain.port.in.rent.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RentInvoiceController {

    private final GenerateRentInvoiceUseCase generateInvoiceUseCase;
    private final GetRentInvoiceByIdUseCase getInvoiceByIdUseCase;
    private final GetRentInvoicesByLeaseUseCase getInvoicesByLeaseUseCase;
    private final GetUnpaidInvoicesUseCase getUnpaidInvoicesUseCase;

    public RentInvoiceController(GenerateRentInvoiceUseCase generateInvoiceUseCase,
                                 GetRentInvoiceByIdUseCase getInvoiceByIdUseCase,
                                 GetRentInvoicesByLeaseUseCase getInvoicesByLeaseUseCase,
                                 GetUnpaidInvoicesUseCase getUnpaidInvoicesUseCase) {
        this.generateInvoiceUseCase = generateInvoiceUseCase;
        this.getInvoiceByIdUseCase = getInvoiceByIdUseCase;
        this.getInvoicesByLeaseUseCase = getInvoicesByLeaseUseCase;
        this.getUnpaidInvoicesUseCase = getUnpaidInvoicesUseCase;
    }

    // Symfony: POST /leases/{leaseId}/invoices (generate invoice)
    @PostMapping("/leases/{leaseId}/invoices")
    public ApiResponse<RentInvoiceResponse> generate(@PathVariable UUID leaseId,
                                                      @RequestParam int month,
                                                      @RequestParam int year) {
        return ApiResponse.success(generateInvoiceUseCase.execute(leaseId, month, year), "Facture générée avec succès");
    }

    // Symfony: GET /leases/{leaseId}/invoices
    @GetMapping("/leases/{leaseId}/invoices")
    public ApiResponse<List<RentInvoiceResponse>> getByLease(@PathVariable UUID leaseId) {
        return ApiResponse.success(getInvoicesByLeaseUseCase.execute(leaseId));
    }

    // Symfony: GET /rent-invoices/{id}
    @GetMapping("/rent-invoices/{id}")
    public ApiResponse<RentInvoiceResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(getInvoiceByIdUseCase.execute(id));
    }

    // Symfony: GET /rent-invoices (admin list)
    @GetMapping("/rent-invoices")
    public ApiResponse<List<RentInvoiceResponse>> getAll() {
        // Admin endpoint - returns all invoices
        return ApiResponse.success(getUnpaidInvoicesUseCase.execute(null));
    }
}
