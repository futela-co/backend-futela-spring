package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.response.reservation.VisitResponse;
import com.futela.api.application.mapper.reservation.VisitResponseMapper;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.reservation.GetVisitByIdUseCase;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaVisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetVisitByIdService implements GetVisitByIdUseCase {

    private final JpaVisitRepository visitRepository;

    public GetVisitByIdService(JpaVisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    public VisitResponse execute(UUID visitId) {
        VisitEntity entity = visitRepository.findByIdAndDeletedAtIsNull(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visite", visitId.toString()));

        return VisitResponseMapper.fromEntity(entity);
    }
}
