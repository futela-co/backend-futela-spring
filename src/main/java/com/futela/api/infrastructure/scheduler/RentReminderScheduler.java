package com.futela.api.infrastructure.scheduler;

import com.futela.api.domain.enums.PaymentStatus;
import com.futela.api.domain.enums.ReminderType;
import com.futela.api.domain.model.rent.RentInvoice;
import com.futela.api.domain.model.rent.RentReminder;
import com.futela.api.domain.port.out.rent.RentInvoiceRepositoryPort;
import com.futela.api.domain.port.out.rent.RentReminderRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;

@Component
public class RentReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(RentReminderScheduler.class);

    private final RentInvoiceRepositoryPort invoiceRepository;
    private final RentReminderRepositoryPort reminderRepository;

    public RentReminderScheduler(RentInvoiceRepositoryPort invoiceRepository,
                                 RentReminderRepositoryPort reminderRepository) {
        this.invoiceRepository = invoiceRepository;
        this.reminderRepository = reminderRepository;
    }

    @Scheduled(cron = "${scheduler.rent-reminder:0 0 8 * * *}")
    public void sendReminders() {
        log.info("[Scheduler] Sending rent reminders...");
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        // BEFORE_DUE: 3 days before due date
        var pendingInvoices = invoiceRepository.findPendingBeforeDueDate(threeDaysLater);
        for (var invoice : pendingInvoices) {
            if (!invoice.dueDate().isBefore(today) && !reminderRepository.existsByInvoiceIdAndType(invoice.id(), ReminderType.BEFORE_DUE)) {
                sendReminder(invoice, ReminderType.BEFORE_DUE);
            }
        }

        // ON_DUE: due today
        var dueToday = invoiceRepository.findByStatus(PaymentStatus.PENDING).stream()
                .filter(i -> i.dueDate().equals(today))
                .toList();
        for (var invoice : dueToday) {
            if (!reminderRepository.existsByInvoiceIdAndType(invoice.id(), ReminderType.ON_DUE)) {
                sendReminder(invoice, ReminderType.ON_DUE);
            }
        }

        // AFTER_DUE: overdue
        var overdueInvoices = invoiceRepository.findOverdue();
        for (var invoice : overdueInvoices) {
            if (!reminderRepository.existsByInvoiceIdAndType(invoice.id(), ReminderType.AFTER_DUE)) {
                sendReminder(invoice, ReminderType.AFTER_DUE);
            }
        }

        log.info("[Scheduler] Rent reminders processed");
    }

    private void sendReminder(RentInvoice invoice, ReminderType type) {
        try {
            RentReminder reminder = new RentReminder(
                    null, invoice.id(), invoice.leaseId(),
                    type, Instant.now(), "push",
                    invoice.companyId(), null
            );
            reminderRepository.save(reminder);
            log.debug("[Scheduler] Reminder {} sent for invoice {}", type, invoice.id());
        } catch (Exception e) {
            log.error("[Scheduler] Failed to send reminder for invoice {}: {}", invoice.id(), e.getMessage());
        }
    }
}
