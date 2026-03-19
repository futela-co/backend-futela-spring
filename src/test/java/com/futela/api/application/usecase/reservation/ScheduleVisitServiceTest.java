package com.futela.api.application.usecase.reservation;

import com.futela.api.application.dto.request.reservation.ScheduleVisitRequest;
import com.futela.api.domain.enums.VisitStatus;
import com.futela.api.domain.event.VisitScheduledEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.reservation.VisitEntity;
import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import com.futela.api.infrastructure.persistence.repository.reservation.JpaVisitRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleVisitServiceTest {

    @Mock
    private JpaVisitRepository visitRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ScheduleVisitService service;

    private UUID userId;
    private UUID propertyId;
    private PropertyEntity property;
    private UserEntity visitor;
    private CompanyEntity company;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        propertyId = UUID.randomUUID();

        company = new CompanyEntity();
        company.setName("Test Company");

        property = mock(PropertyEntity.class);
        lenient().when(property.getId()).thenReturn(propertyId);
        lenient().when(property.getCompany()).thenReturn(company);

        visitor = new UserEntity();
        visitor.setFirstName("Visitor");
        visitor.setLastName("Test");
    }

    @Test
    @DisplayName("Doit programmer une visite avec succès")
    void shouldScheduleVisitSuccessfully() {
        Instant futureDate = Instant.now().plus(3, ChronoUnit.DAYS);
        ScheduleVisitRequest request = new ScheduleVisitRequest(propertyId, futureDate, "Notes de visite");

        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(property);
        when(entityManager.find(UserEntity.class, userId)).thenReturn(visitor);
        when(visitRepository.save(any(VisitEntity.class))).thenAnswer(invocation -> {
            VisitEntity entity = invocation.getArgument(0);
            setEntityFields(entity);
            return entity;
        });

        service.execute(request, userId);

        ArgumentCaptor<VisitEntity> captor = ArgumentCaptor.forClass(VisitEntity.class);
        verify(visitRepository).save(captor.capture());

        VisitEntity savedEntity = captor.getValue();
        assertThat(savedEntity.getStatus()).isEqualTo(VisitStatus.SCHEDULED);
        assertThat(savedEntity.getScheduledAt()).isEqualTo(futureDate);
        assertThat(savedEntity.getNotes()).isEqualTo("Notes de visite");
        assertThat(savedEntity.getProperty()).isEqualTo(property);
        assertThat(savedEntity.getUser()).isEqualTo(visitor);
    }

    @Test
    @DisplayName("Doit rejeter quand la propriété n'existe pas")
    void shouldRejectWhenPropertyNotFound() {
        Instant futureDate = Instant.now().plus(3, ChronoUnit.DAYS);
        ScheduleVisitRequest request = new ScheduleVisitRequest(propertyId, futureDate, null);

        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.execute(request, userId));
    }

    @Test
    @DisplayName("Doit rejeter quand l'utilisateur n'existe pas")
    void shouldRejectWhenUserNotFound() {
        Instant futureDate = Instant.now().plus(3, ChronoUnit.DAYS);
        ScheduleVisitRequest request = new ScheduleVisitRequest(propertyId, futureDate, null);

        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(property);
        when(entityManager.find(UserEntity.class, userId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.execute(request, userId));
    }

    @Test
    @DisplayName("Doit publier un événement VisitScheduledEvent après la programmation")
    void shouldEmitVisitScheduledEvent() {
        Instant futureDate = Instant.now().plus(3, ChronoUnit.DAYS);
        ScheduleVisitRequest request = new ScheduleVisitRequest(propertyId, futureDate, null);

        when(entityManager.find(PropertyEntity.class, propertyId)).thenReturn(property);
        when(entityManager.find(UserEntity.class, userId)).thenReturn(visitor);
        when(visitRepository.save(any(VisitEntity.class))).thenAnswer(invocation -> {
            VisitEntity entity = invocation.getArgument(0);
            setEntityFields(entity);
            return entity;
        });

        service.execute(request, userId);

        ArgumentCaptor<VisitScheduledEvent> eventCaptor = ArgumentCaptor.forClass(VisitScheduledEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        VisitScheduledEvent event = eventCaptor.getValue();
        assertThat(event.propertyId()).isEqualTo(propertyId);
        assertThat(event.scheduledAt()).isEqualTo(futureDate);
    }

    private void setEntityFields(VisitEntity entity) {
        try {
            var idField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, UUID.randomUUID());
            var createdAtField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(entity, Instant.now());
            var updatedAtField = entity.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(entity, Instant.now());
        } catch (Exception ignored) {}
    }
}
