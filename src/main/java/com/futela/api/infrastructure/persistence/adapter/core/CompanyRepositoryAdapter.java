package com.futela.api.infrastructure.persistence.adapter.core;

import com.futela.api.domain.model.core.Company;
import com.futela.api.domain.port.out.common.CompanyRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.mapper.core.CompanyPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.core.JpaCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepositoryPort {

    private final JpaCompanyRepository jpaRepository;

    @Override
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(CompanyPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Company> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug)
                .map(CompanyPersistenceMapper::toDomain);
    }

    @Override
    public Company findOrCreateDefault() {
        return jpaRepository.findFirstByIsActiveTrue()
                .map(CompanyPersistenceMapper::toDomain)
                .orElseGet(() -> {
                    CompanyEntity entity = CompanyEntity.builder()
                            .name("Futela")
                            .slug("futela")
                            .isActive(true)
                            .build();
                    return CompanyPersistenceMapper.toDomain(jpaRepository.save(entity));
                });
    }

    @Override
    public Company save(Company company) {
        CompanyEntity entity = CompanyPersistenceMapper.toEntity(company);
        return CompanyPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean slugExists(String slug) {
        return jpaRepository.existsBySlug(slug);
    }
}
