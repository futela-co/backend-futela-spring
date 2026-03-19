package com.futela.api.application.usecase.rent;

import com.futela.api.application.dto.response.rent.RentReminderResponse;
import com.futela.api.domain.port.in.rent.GetReminderHistoryUseCase;
import com.futela.api.domain.port.out.rent.RentReminderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetReminderHistoryService implements GetReminderHistoryUseCase {
    private final RentReminderRepositoryPort reminderRepository;

    public GetReminderHistoryService(RentReminderRepositoryPort reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @Override
    public List<RentReminderResponse> execute(UUID leaseId) {
        return reminderRepository.findByLeaseId(leaseId).stream()
                .map(RentReminderResponse::from).toList();
    }
}
