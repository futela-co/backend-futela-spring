package com.futela.api.infrastructure.scheduler;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.event.rent.RentPaymentOverdueEvent;
import com.futela.api.domain.model.rent.RentInvoice;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OverduePaymentSchedulerTest {

    @Mock
    private RentInvoiceRepositoryPort invoiceRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OverduePaymentScheduler overduePaymentScheduler;

    @Test
    @DisplayName("Doit marquer les factures PENDING en retard comme OVERDUE")
    void shouldMarkPendingInvoicesPastDueDateAsOverdue() {
        UUID invoiceId = UUID.randomUUID();
        RentInvoice pendingOverdue = new RentInvoice(
                invoiceId, UUID.randomUUID(), "FUT-2026-0301-ABCD",
                new BigDecimal("500.00"), BigDecimal.ZERO, PaymentStatus.PENDING,
                LocalDate.now().minusDays(5), // past due
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31),
                BigDecimal.ZERO, UUID.randomUUID(), Instant.now(), Instant.now()
        );

        when(invoiceRepository.findOverdue()).thenReturn(List.of(pendingOverdue));

        ArgumentCaptor<RentInvoice> invoiceCaptor = ArgumentCaptor.forClass(RentInvoice.class);
        when(invoiceRepository.save(invoiceCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        overduePaymentScheduler.detectOverduePayments();

        RentInvoice saved = invoiceCaptor.getValue();
        assertThat(saved.status()).isEqualTo(PaymentStatus.OVERDUE);
        verify(eventPublisher).publishEvent(any(RentPaymentOverdueEvent.class));
    }

    @Test
    @DisplayName("Doit marquer les factures PARTIAL en retard comme OVERDUE")
    void shouldMarkPartialInvoicesPastDueDateAsOverdue() {
        RentInvoice partialOverdue = new RentInvoice(
                UUID.randomUUID(), UUID.randomUUID(), "FUT-2026-0301-EFGH",
                new BigDecimal("500.00"), new BigDecimal("200.00"), PaymentStatus.PARTIAL,
                LocalDate.now().minusDays(3),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31),
                BigDecimal.ZERO, UUID.randomUUID(), Instant.now(), Instant.now()
        );

        when(invoiceRepository.findOverdue()).thenReturn(List.of(partialOverdue));
        when(invoiceRepository.save(any(RentInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        overduePaymentScheduler.detectOverduePayments();

        verify(invoiceRepository).save(any(RentInvoice.class));
        verify(eventPublisher).publishEvent(any(RentPaymentOverdueEvent.class));
    }

    @Test
    @DisplayName("Doit ne rien faire quand aucune facture en retard")
    void shouldDoNothingWhenNoOverdueInvoices() {
        when(invoiceRepository.findOverdue()).thenReturn(List.of());

        overduePaymentScheduler.detectOverduePayments();

        verify(invoiceRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Doit continuer même si le traitement d'une facture échoue")
    void shouldContinueOnError() {
        RentInvoice invoice1 = new RentInvoice(
                UUID.randomUUID(), UUID.randomUUID(), "FUT-001",
                new BigDecimal("500.00"), BigDecimal.ZERO, PaymentStatus.PENDING,
                LocalDate.now().minusDays(5),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31),
                BigDecimal.ZERO, UUID.randomUUID(), Instant.now(), Instant.now()
        );
        RentInvoice invoice2 = new RentInvoice(
                UUID.randomUUID(), UUID.randomUUID(), "FUT-002",
                new BigDecimal("300.00"), BigDecimal.ZERO, PaymentStatus.PENDING,
                LocalDate.now().minusDays(3),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31),
                BigDecimal.ZERO, UUID.randomUUID(), Instant.now(), Instant.now()
        );

        when(invoiceRepository.findOverdue()).thenReturn(List.of(invoice1, invoice2));
        when(invoiceRepository.save(any(RentInvoice.class)))
                .thenThrow(new RuntimeException("DB error"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Should not throw
        overduePaymentScheduler.detectOverduePayments();
    }
}
