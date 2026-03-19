package com.futela.api.infrastructure.persistence.repository.payment;

import com.futela.api.infrastructure.persistence.entity.payment.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCurrencyRepository extends JpaRepository<CurrencyEntity, UUID> {
    Optional<CurrencyEntity> findByCode(String code);
    List<CurrencyEntity> findByIsActiveTrue();
    boolean existsByCode(String code);
}
