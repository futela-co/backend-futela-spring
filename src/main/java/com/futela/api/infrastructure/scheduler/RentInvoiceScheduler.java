package com.futela.api.infrastructure.scheduler;

import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.domain.port.in.rent.GenerateRentInvoiceUseCase;
import com.futela.api.domain.port.out.rent.LeaseRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RentInvoiceScheduler {

    private static final Logger log = LoggerFactory.getLogger(RentInvoiceScheduler.class);

    private final LeaseRepositoryPort leaseRepository;
    private final GenerateRentInvoiceUseCase generateInvoiceUseCase;

    public RentInvoiceScheduler(LeaseRepositoryPort leaseRepository,
                                GenerateRentInvoiceUseCase generateInvoiceUseCase) {
        this.leaseRepository = leaseRepository;
        this.generateInvoiceUseCase = generateInvoiceUseCase;
    }

    @Scheduled(cron = "${scheduler.rent-invoice:0 0 0 1 * *}")
    public void generateMonthlyInvoices() {
        log.info("[Scheduler] Generating monthly rent invoices...");
        var now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        var activeLeases = leaseRepository.findByStatus(LeaseStatus.ACTIVE);
        int success = 0;
        int errors = 0;

        for (var lease : activeLeases) {
            try {
                generateInvoiceUseCase.execute(lease.id(), month, year);
                success++;
            } catch (Exception e) {
                errors++;
                log.error("[Scheduler] Failed to generate invoice for lease {}: {}", lease.id(), e.getMessage());
            }
        }

        log.info("[Scheduler] Monthly invoices generated: {} success, {} errors", success, errors);
    }
}
