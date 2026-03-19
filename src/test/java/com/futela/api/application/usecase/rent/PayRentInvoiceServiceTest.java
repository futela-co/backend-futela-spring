package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.request.rent.PayRentInvoiceRequest;
import com.futela.api.application.dto.response.rent.RentPaymentResponse;
import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.event.rent.RentPaymentReceivedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.model.rent.RentInvoice;
import com.futela.api.domain.model.rent.RentPayment;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import com.futela.api.domain.port.out.rent.RentPaymentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayRentInvoiceServiceTest {

    @Mock
    private RentInvoiceRepositoryPort invoiceRepository;

    @Mock
    private RentPaymentRepositoryPort paymentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PayRentInvoiceService payRentInvoiceService;

    private UUID invoiceId;
    private UUID leaseId;
    private UUID companyId;

    @BeforeEach
    void setUp() {
        invoiceId = UUID.randomUUID();
        leaseId = UUID.randomUUID();
        companyId = UUID.randomUUID();
    }

    private RentInvoice createPendingInvoice(BigDecimal amount, BigDecimal paidAmount, PaymentStatus status) {
        return new RentInvoice(
                invoiceId, leaseId, "FUT-2026-0301-ABCD",
                amount, paidAmount, status,
                LocalDate.of(2026, 3, 5),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31),
                BigDecimal.ZERO, companyId, Instant.now(), Instant.now()
        );
    }

    @Test
    @DisplayName("Doit marquer la facture comme PAID quand le montant payé couvre le total")
    void shouldMarkInvoiceAsPaidWhenFullPayment() {
        RentInvoice invoice = createPendingInvoice(new BigDecimal("500.00"), BigDecimal.ZERO, PaymentStatus.PENDING);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(RentPayment.class))).thenAnswer(invocation -> {
            RentPayment p = invocation.getArgument(0);
            return new RentPayment(UUID.randomUUID(), p.invoiceId(), p.leaseId(), p.amount(),
                    p.paymentDate(), p.paymentMethod(), p.reference(), p.notes(), p.companyId(), Instant.now());
        });
        when(invoiceRepository.save(any(RentInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayRentInvoiceRequest request = new PayRentInvoiceRequest(
                new BigDecimal("500.00"), LocalDate.of(2026, 3, 4), "MOBILE_MONEY", "REF-001", null
        );

        payRentInvoiceService.execute(invoiceId, request);

        ArgumentCaptor<RentInvoice> captor = ArgumentCaptor.forClass(RentInvoice.class);
        verify(invoiceRepository).save(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    @DisplayName("Doit marquer la facture comme PARTIAL quand le paiement est partiel")
    void shouldMarkInvoiceAsPartialWhenPartialPayment() {
        RentInvoice invoice = createPendingInvoice(new BigDecimal("500.00"), BigDecimal.ZERO, PaymentStatus.PENDING);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(RentPayment.class))).thenAnswer(invocation -> {
            RentPayment p = invocation.getArgument(0);
            return new RentPayment(UUID.randomUUID(), p.invoiceId(), p.leaseId(), p.amount(),
                    p.paymentDate(), p.paymentMethod(), p.reference(), p.notes(), p.companyId(), Instant.now());
        });
        when(invoiceRepository.save(any(RentInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayRentInvoiceRequest request = new PayRentInvoiceRequest(
                new BigDecimal("200.00"), LocalDate.of(2026, 3, 4), "MOBILE_MONEY", "REF-002", null
        );

        payRentInvoiceService.execute(invoiceId, request);

        ArgumentCaptor<RentInvoice> captor = ArgumentCaptor.forClass(RentInvoice.class);
        verify(invoiceRepository).save(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(PaymentStatus.PARTIAL);
        assertThat(captor.getValue().paidAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Doit lever InvalidOperationException pour une facture déjà payée")
    void shouldThrowWhenInvoiceAlreadyPaid() {
        RentInvoice paidInvoice = createPendingInvoice(new BigDecimal("500.00"), new BigDecimal("500.00"), PaymentStatus.PAID);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(paidInvoice));

        PayRentInvoiceRequest request = new PayRentInvoiceRequest(
                new BigDecimal("100.00"), LocalDate.of(2026, 3, 10), "MOBILE_MONEY", null, null
        );

        assertThatThrownBy(() -> payRentInvoiceService.execute(invoiceId, request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("ne peut pas être payée");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit publier un RentPaymentReceivedEvent après le paiement")
    void shouldPublishRentPaymentReceivedEvent() {
        RentInvoice invoice = createPendingInvoice(new BigDecimal("500.00"), BigDecimal.ZERO, PaymentStatus.PENDING);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(RentPayment.class))).thenAnswer(invocation -> {
            RentPayment p = invocation.getArgument(0);
            return new RentPayment(UUID.randomUUID(), p.invoiceId(), p.leaseId(), p.amount(),
                    p.paymentDate(), p.paymentMethod(), p.reference(), p.notes(), p.companyId(), Instant.now());
        });
        when(invoiceRepository.save(any(RentInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayRentInvoiceRequest request = new PayRentInvoiceRequest(
                new BigDecimal("500.00"), LocalDate.of(2026, 3, 4), "CASH", null, null
        );

        payRentInvoiceService.execute(invoiceId, request);

        ArgumentCaptor<RentPaymentReceivedEvent> eventCaptor = ArgumentCaptor.forClass(RentPaymentReceivedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        RentPaymentReceivedEvent event = eventCaptor.getValue();
        assertThat(event.invoiceId()).isEqualTo(invoiceId);
        assertThat(event.amount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Doit calculer les pénalités de retard de 5% après 7 jours de retard")
    void shouldCalculateLateFeeAfter7DaysOverdue() {
        RentInvoice invoice = createPendingInvoice(new BigDecimal("500.00"), BigDecimal.ZERO, PaymentStatus.OVERDUE);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(RentPayment.class))).thenAnswer(invocation -> {
            RentPayment p = invocation.getArgument(0);
            return new RentPayment(UUID.randomUUID(), p.invoiceId(), p.leaseId(), p.amount(),
                    p.paymentDate(), p.paymentMethod(), p.reference(), p.notes(), p.companyId(), Instant.now());
        });

        ArgumentCaptor<RentInvoice> captor = ArgumentCaptor.forClass(RentInvoice.class);
        when(invoiceRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Payment 10 days after due date (2026-03-05 + 10 = 2026-03-15)
        PayRentInvoiceRequest request = new PayRentInvoiceRequest(
                new BigDecimal("500.00"), LocalDate.of(2026, 3, 15), "MOBILE_MONEY", null, null
        );

        payRentInvoiceService.execute(invoiceId, request);

        RentInvoice updated = captor.getValue();
        assertThat(updated.lateFee()).isEqualByComparingTo(new BigDecimal("25.00")); // 500 * 0.05
    }

    @Test
    @DisplayName("Doit accepter un paiement sur une facture OVERDUE")
    void shouldAllowPaymentOnOverdueInvoice() {
        RentInvoice overdueInvoice = createPendingInvoice(new BigDecimal("500.00"), BigDecimal.ZERO, PaymentStatus.OVERDUE);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(overdueInvoice));
        when(paymentRepository.save(any(RentPayment.class))).thenAnswer(invocation -> {
            RentPayment p = invocation.getArgument(0);
            return new RentPayment(UUID.randomUUID(), p.invoiceId(), p.leaseId(), p.amount(),
                    p.paymentDate(), p.paymentMethod(), p.reference(), p.notes(), p.companyId(), Instant.now());
        });
        when(invoiceRepository.save(any(RentInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayRentInvoiceRequest request = new PayRentInvoiceRequest(
                new BigDecimal("500.00"), LocalDate.of(2026, 3, 20), "CASH", null, null
        );

        RentPaymentResponse response = payRentInvoiceService.execute(invoiceId, request);
        assertThat(response).isNotNull();
    }
}
