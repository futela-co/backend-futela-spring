package com.futela.api.infrastructure.persistence.repository.address;

import com.futela.api.infrastructure.persistence.entity.address.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaDistrictRepository extends JpaRepository<DistrictEntity, UUID> {
    List<DistrictEntity> findByTownIdAndDeletedAtIsNullOrderByNameAsc(UUID townId);
    List<DistrictEntity> findByCityIdAndDeletedAtIsNullOrderByNameAsc(UUID cityId);
}
