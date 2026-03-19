package com.futela.api.infrastructure.persistence.repository.auth;

import com.futela.api.infrastructure.persistence.entity.auth.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);

    @Query("SELECT u FROM UserEntity u WHERE (u.email = :identifier OR u.phone = :identifier) AND u.deletedAt IS NULL")
    Optional<UserEntity> findByEmailOrPhone(@Param("identifier") String identifier);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByPhoneAndDeletedAtIsNull(String phone);

    @Modifying
    @Query("UPDATE UserEntity u SET u.lastLoginAt = :now, u.updatedAt = :now WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") UUID userId, @Param("now") Instant now);

    long countByDeletedAtIsNull();
}
