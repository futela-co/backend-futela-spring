package com.futela.api.presentation.controller.payment;

import com.futela.api.application.dto.request.payment.CreateCurrencyRequest;
import com.futela.api.application.dto.request.payment.UpdateCurrencyRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.payment.ConvertCurrencyResponse;
import com.futela.api.application.dto.response.payment.CurrencyResponse;
import com.futela.api.domain.port.in.payment.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    private final GetCurrenciesUseCase getCurrenciesUseCase;
    private final GetActiveCurrenciesUseCase getActiveCurrenciesUseCase;
    private final CreateCurrencyUseCase createCurrencyUseCase;
    private final UpdateCurrencyUseCase updateCurrencyUseCase;
    private final ConvertCurrencyUseCase convertCurrencyUseCase;

    public CurrencyController(GetCurrenciesUseCase getCurrenciesUseCase,
                              GetActiveCurrenciesUseCase getActiveCurrenciesUseCase,
                              CreateCurrencyUseCase createCurrencyUseCase,
                              UpdateCurrencyUseCase updateCurrencyUseCase,
                              ConvertCurrencyUseCase convertCurrencyUseCase) {
        this.getCurrenciesUseCase = getCurrenciesUseCase;
        this.getActiveCurrenciesUseCase = getActiveCurrenciesUseCase;
        this.createCurrencyUseCase = createCurrencyUseCase;
        this.updateCurrencyUseCase = updateCurrencyUseCase;
        this.convertCurrencyUseCase = convertCurrencyUseCase;
    }

    @GetMapping("/currencies")
    public ApiResponse<List<CurrencyResponse>> getAll() {
        return ApiResponse.success(getCurrenciesUseCase.execute());
    }

    @GetMapping("/currencies/active")
    public ApiResponse<List<CurrencyResponse>> getActive() {
        return ApiResponse.success(getActiveCurrenciesUseCase.execute());
    }

    @PostMapping("/admin/currencies")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CurrencyResponse> create(@Valid @RequestBody CreateCurrencyRequest request) {
        return ApiResponse.success(createCurrencyUseCase.execute(request), "Devise créée avec succès");
    }

    @PutMapping("/admin/currencies/{id}")
    public ApiResponse<CurrencyResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateCurrencyRequest request) {
        return ApiResponse.success(updateCurrencyUseCase.execute(id, request), "Devise mise à jour avec succès");
    }

    @GetMapping("/currencies/convert")
    public ApiResponse<ConvertCurrencyResponse> convert(@RequestParam String from,
                                                         @RequestParam String to,
                                                         @RequestParam BigDecimal amount) {
        return ApiResponse.success(convertCurrencyUseCase.execute(from, to, amount));
    }
}
