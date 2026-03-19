package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.response.reservation.VisitResponse;
import com.futela.api.application.mapper.reservation.VisitResponseMapper;
import com.futela.api.domain.enums.VisitStatus;
import com.futela.api.domain.event.VisitConfirmedEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.reservation.ConfirmVisitUseCase;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaVisitRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class ConfirmVisitService implements ConfirmVisitUseCase {

    private final JpaVisitRepository visitRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ConfirmVisitService(
            JpaVisitRepository visitRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.visitRepository = visitRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public VisitResponse execute(UUID visitId) {
        VisitEntity entity = visitRepository.findByIdAndDeletedAtIsNull(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visite", visitId.toString()));

        if (entity.getStatus() != VisitStatus.SCHEDULED) {
            throw new InvalidOperationException("Seules les visites planifiées peuvent être confirmées");
        }

        entity.setStatus(VisitStatus.CONFIRMED);
        entity.setConfirmedAt(Instant.now());

        VisitEntity saved = visitRepository.save(entity);

        eventPublisher.publishEvent(new VisitConfirmedEvent(
                saved.getId(), saved.getProperty().getId(), saved.getUser().getId()
        ));

        return VisitResponseMapper.fromEntity(saved);
    }
}
