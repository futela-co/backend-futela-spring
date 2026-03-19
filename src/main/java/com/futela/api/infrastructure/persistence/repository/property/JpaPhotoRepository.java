package com.futela.api.infrastructure.persistence.repository.property;

import com.futela.api.infrastructure.persistence.entity.property.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaPhotoRepository extends JpaRepository<PhotoEntity, UUID> {
    List<PhotoEntity> findByPropertyIdOrderByDisplayOrderAsc(UUID propertyId);
    int countByPropertyId(UUID propertyId);

    @Modifying
    @Query("UPDATE PhotoEntity p SET p.isPrimary = false WHERE p.property.id = :propertyId")
    void unsetPrimaryByPropertyId(@Param("propertyId") UUID propertyId);
}
