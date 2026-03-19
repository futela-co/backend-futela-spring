package com.futela.api.infrastructure.persistence.repository.address;

import com.futela.api.infrastructure.persistence.entity.address.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaProvinceRepository extends JpaRepository<ProvinceEntity, UUID> {
    List<ProvinceEntity> findByCountryIdAndDeletedAtIsNullOrderByNameAsc(UUID countryId);
}
