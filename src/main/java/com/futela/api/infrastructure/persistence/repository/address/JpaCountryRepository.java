package com.futela.api.infrastructure.persistence.repository.address;

import com.futela.api.infrastructure.persistence.entity.address.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCountryRepository extends JpaRepository<CountryEntity, UUID> {
    boolean existsByCode(String code);
    List<CountryEntity> findAllByDeletedAtIsNullOrderByNameAsc();
}
