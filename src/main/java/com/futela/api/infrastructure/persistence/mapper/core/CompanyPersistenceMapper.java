package com.futela.api.infrastructure.persistence.mapper.core;

import com.futela.api.domain.model.core.Company;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;

public final class CompanyPersistenceMapper {

    private CompanyPersistenceMapper() {}

    public static Company toDomain(CompanyEntity entity) {
        if (entity == null) return null;
        return new Company(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getLogo(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static CompanyEntity toEntity(Company domain) {
        if (domain == null) return null;
        CompanyEntity entity = new CompanyEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setSlug(domain.slug());
        entity.setEmail(domain.email());
        entity.setPhone(domain.phone());
        entity.setLogo(domain.logo());
        entity.setActive(domain.isActive());
        return entity;
    }
}
