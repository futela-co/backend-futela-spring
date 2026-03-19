package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.request.reservation.ScheduleVisitRequest;
import com.futela.api.application.dto.response.reservation.VisitResponse;
import com.futela.api.application.mapper.reservation.VisitResponseMapper;
import com.futela.api.domain.enums.VisitStatus;
import com.futela.api.domain.event.VisitScheduledEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.reservation.ScheduleVisitUseCase;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaVisitRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ScheduleVisitService implements ScheduleVisitUseCase {

    private final JpaVisitRepository visitRepository;
    private final jakarta.persistence.EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    public ScheduleVisitService(
            JpaVisitRepository visitRepository,
            jakarta.persistence.EntityManager entityManager,
            ApplicationEventPublisher eventPublisher
    ) {
        this.visitRepository = visitRepository;
        this.entityManager = entityManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public VisitResponse execute(ScheduleVisitRequest request, UUID currentUserId) {
        // Load property
        PropertyEntity property = entityManager.find(PropertyEntity.class, request.propertyId());
        if (property == null) {
            throw new ResourceNotFoundException("Propriété", request.propertyId().toString());
        }

        // Load visitor
        UserEntity visitor = entityManager.find(UserEntity.class, currentUserId);
        if (visitor == null) {
            throw new ResourceNotFoundException("Utilisateur", currentUserId.toString());
        }

        // Create visit
        VisitEntity entity = new VisitEntity();
        entity.setProperty(property);
        entity.setUser(visitor);
        entity.setCompany(property.getCompany());
        entity.setStatus(VisitStatus.SCHEDULED);
        entity.setScheduledAt(request.scheduledAt());
        entity.setNotes(request.notes());

        VisitEntity saved = visitRepository.save(entity);

        // Publish event
        eventPublisher.publishEvent(new VisitScheduledEvent(
                saved.getId(), property.getId(), visitor.getId(), request.scheduledAt()
        ));

        return VisitResponseMapper.fromEntity(saved);
    }
}
