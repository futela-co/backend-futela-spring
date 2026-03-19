package com.futela.api.infrastructure.persistence.repository.core;

import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCompanyRepository extends JpaRepository<CompanyEntity, UUID> {

    Optional<CompanyEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Optional<CompanyEntity> findFirstByIsActiveTrue();
}
