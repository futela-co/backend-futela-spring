package com.futela.api.infrastructure.persistence.adapter.property;

import com.futela.api.domain.model.property.Listing;
import com.futela.api.domain.port.out.property.ListingRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.property.ListingEntity;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.mapper.property.ListingPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.property.JpaListingRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ListingRepositoryAdapter implements ListingRepositoryPort {

    private final JpaListingRepository jpaRepository;
    private final EntityManager entityManager;

    public ListingRepositoryAdapter(JpaListingRepository jpaRepository, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Listing save(Listing listing) {
        ListingEntity entity = new ListingEntity();
        entity.setUser(entityManager.getReference(UserEntity.class, listing.userId()));
        entity.setProperty(entityManager.getReference(PropertyEntity.class, listing.propertyId()));
        if (listing.companyId() != null) {
            entity.setCompany(entityManager.getReference(CompanyEntity.class, listing.companyId()));
        }
        return ListingPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteByUserIdAndPropertyId(UUID userId, UUID propertyId) {
        jpaRepository.deleteByUserIdAndPropertyId(userId, propertyId);
    }

    @Override
    public List<Listing> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId).stream()
                .map(ListingPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndPropertyId(UUID userId, UUID propertyId) {
        return jpaRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }

    @Override
    public Optional<Listing> findByUserIdAndPropertyId(UUID userId, UUID propertyId) {
        return jpaRepository.findByUserIdAndPropertyId(userId, propertyId)
                .map(ListingPersistenceMapper::toDomain);
    }
}
