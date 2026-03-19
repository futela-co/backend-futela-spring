package com.futela.api.infrastructure.persistence.repository.address;

import com.futela.api.infrastructure.persistence.entity.address.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCityRepository extends JpaRepository<CityEntity, UUID> {
    List<CityEntity> findByProvinceIdAndDeletedAtIsNullOrderByNameAsc(UUID provinceId);
}
