package com.futela.api.infrastructure.scheduler;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.event.rent.RentPaymentOverdueEvent;
import com.futela.api.domain.model.rent.RentInvoice;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class OverduePaymentScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverduePaymentScheduler.class);

    private final RentInvoiceRepositoryPort invoiceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OverduePaymentScheduler(RentInvoiceRepositoryPort invoiceRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.invoiceRepository = invoiceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "${scheduler.overdue-payment:0 0 1 * * *}")
    @Transactional
    public void detectOverduePayments() {
        log.info("[Scheduler] Detecting overdue payments...");
        var overdueInvoices = invoiceRepository.findOverdue();
        int count = 0;

        for (RentInvoice invoice : overdueInvoices) {
            try {
                if (invoice.status() == PaymentStatus.PENDING || invoice.status() == PaymentStatus.PARTIAL) {
                    RentInvoice updated = new RentInvoice(
                            invoice.id(), invoice.leaseId(), invoice.invoiceNumber(),
                            invoice.amount(), invoice.paidAmount(),
                            PaymentStatus.OVERDUE,
                            invoice.dueDate(), invoice.periodStart(), invoice.periodEnd(),
                            invoice.lateFee(), invoice.companyId(),
                            invoice.createdAt(), invoice.updatedAt()
                    );
                    invoiceRepository.save(updated);

                    long daysOverdue = ChronoUnit.DAYS.between(invoice.dueDate(), LocalDate.now());
                    eventPublisher.publishEvent(new RentPaymentOverdueEvent(invoice.id(), daysOverdue));
                    count++;
                }
            } catch (Exception e) {
                log.error("[Scheduler] Error marking invoice {} as overdue: {}", invoice.id(), e.getMessage());
            }
        }

        log.info("[Scheduler] Overdue detection complete: {} invoices marked", count);
    }
}
