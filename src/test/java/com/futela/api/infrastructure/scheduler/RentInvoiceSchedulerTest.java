package com.futela.api.infrastructure.scheduler;

import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.model.rent.Lease;
import com.futela.api.domain.port.in.rent.GenerateRentInvoiceUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentInvoiceSchedulerTest {

    @Mock
    private LeaseRepositoryPort leaseRepository;

    @Mock
    private GenerateRentInvoiceUseCase generateInvoiceUseCase;

    @InjectMocks
    private RentInvoiceScheduler rentInvoiceScheduler;

    private Lease createActiveLease(UUID id) {
        return new Lease(
                id, UUID.randomUUID(), null, UUID.randomUUID(), null, UUID.randomUUID(), null,
                LeaseStatus.ACTIVE, new BigDecimal("500.00"), "USD", BigDecimal.ZERO,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 5, null,
                null, null, UUID.randomUUID(), Instant.now(), Instant.now(), null
        );
    }

    @Test
    @DisplayName("Doit générer des factures pour tous les baux actifs")
    void shouldGenerateInvoicesForAllActiveLeases() {
        UUID leaseId1 = UUID.randomUUID();
        UUID leaseId2 = UUID.randomUUID();

        List<Lease> activeLeases = List.of(
                createActiveLease(leaseId1),
                createActiveLease(leaseId2)
        );

        when(leaseRepository.findByStatus(LeaseStatus.ACTIVE)).thenReturn(activeLeases);

        rentInvoiceScheduler.generateMonthlyInvoices();

        verify(generateInvoiceUseCase).execute(eq(leaseId1), anyInt(), anyInt());
        verify(generateInvoiceUseCase).execute(eq(leaseId2), anyInt(), anyInt());
        verify(generateInvoiceUseCase, times(2)).execute(any(UUID.class), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Doit ignorer les baux qui ont déjà une facture pour le mois en cours")
    void shouldSkipLeasesWithExistingInvoiceForCurrentMonth() {
        UUID leaseId1 = UUID.randomUUID();
        UUID leaseId2 = UUID.randomUUID();

        List<Lease> activeLeases = List.of(
                createActiveLease(leaseId1),
                createActiveLease(leaseId2)
        );

        when(leaseRepository.findByStatus(LeaseStatus.ACTIVE)).thenReturn(activeLeases);
        // First lease generates OK, second throws (already has invoice)
        when(generateInvoiceUseCase.execute(eq(leaseId1), anyInt(), anyInt())).thenReturn(null);
        when(generateInvoiceUseCase.execute(eq(leaseId2), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Une facture existe déjà pour cette période"));

        // Should not throw - scheduler catches exceptions
        rentInvoiceScheduler.generateMonthlyInvoices();

        verify(generateInvoiceUseCase, times(2)).execute(any(UUID.class), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Doit ne rien faire quand aucun bail actif n'existe")
    void shouldDoNothingWhenNoActiveLeases() {
        when(leaseRepository.findByStatus(LeaseStatus.ACTIVE)).thenReturn(List.of());

        rentInvoiceScheduler.generateMonthlyInvoices();

        verify(generateInvoiceUseCase, never()).execute(any(UUID.class), anyInt(), anyInt());
    }
}
