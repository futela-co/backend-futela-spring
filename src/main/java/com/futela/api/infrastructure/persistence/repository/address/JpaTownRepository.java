package com.futela.api.infrastructure.persistence.repository.address;

import com.futela.api.infrastructure.persistence.entity.address.TownEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTownRepository extends JpaRepository<TownEntity, UUID> {
    List<TownEntity> findByCityIdAndDeletedAtIsNullOrderByNameAsc(UUID cityId);
}
