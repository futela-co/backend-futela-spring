package com.futela.api.domain.port.out.common;

import com.futela.api.domain.model.core.Company;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepositoryPort {

    Optional<Company> findById(UUID id);

    Optional<Company> findBySlug(String slug);

    Company findOrCreateDefault();

    Company save(Company company);

    boolean slugExists(String slug);
}
