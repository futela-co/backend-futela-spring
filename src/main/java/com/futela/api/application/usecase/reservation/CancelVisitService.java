package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.response.reservation.VisitResponse;
import com.futela.api.application.mapper.reservation.VisitResponseMapper;
import com.futela.api.domain.enums.VisitStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.reservation.CancelVisitUseCase;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaVisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CancelVisitService implements CancelVisitUseCase {

    private final JpaVisitRepository visitRepository;

    public CancelVisitService(JpaVisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    public VisitResponse execute(UUID visitId) {
        VisitEntity entity = visitRepository.findByIdAndDeletedAtIsNull(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visite", visitId.toString()));

        if (entity.getStatus() == VisitStatus.CANCELLED) {
            throw new InvalidOperationException("Cette visite est déjà annulée");
        }

        if (entity.getStatus() == VisitStatus.COMPLETED) {
            throw new InvalidOperationException("Impossible d'annuler une visite terminée");
        }

        entity.setStatus(VisitStatus.CANCELLED);

        VisitEntity saved = visitRepository.save(entity);
        return VisitResponseMapper.fromEntity(saved);
    }
}
