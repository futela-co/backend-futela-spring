package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.response.reservation.VisitResponse;
import com.futela.api.application.mapper.reservation.VisitResponseMapper;
import com.futela.api.domain.enums.VisitStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.reservation.CompleteVisitUseCase;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaVisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class CompleteVisitService implements CompleteVisitUseCase {

    private final JpaVisitRepository visitRepository;

    public CompleteVisitService(JpaVisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    public VisitResponse execute(UUID visitId) {
        VisitEntity entity = visitRepository.findByIdAndDeletedAtIsNull(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visite", visitId.toString()));

        if (entity.getStatus() != VisitStatus.CONFIRMED) {
            throw new InvalidOperationException("Seules les visites confirmées peuvent être marquées comme effectuées");
        }

        entity.setStatus(VisitStatus.COMPLETED);
        entity.setCompletedAt(Instant.now());

        VisitEntity saved = visitRepository.save(entity);
        return VisitResponseMapper.fromEntity(saved);
    }
}
