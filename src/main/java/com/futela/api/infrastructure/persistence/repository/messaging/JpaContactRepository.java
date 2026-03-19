package com.futela.api.infrastructure.persistence.repository.messaging;

import com.futela.api.domain.enums.ContactStatus;
import com.futela.api.infrastructure.persistence.entity.messaging.ContactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaContactRepository extends JpaRepository<ContactEntity, UUID> {

    @Query("SELECT c FROM ContactEntity c WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<ContactEntity> findAllActive(Pageable pageable);

    @Query("SELECT c FROM ContactEntity c WHERE c.status = :status AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<ContactEntity> findByStatus(@Param("status") ContactStatus status, Pageable pageable);
}
