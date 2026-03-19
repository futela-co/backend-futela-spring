package com.futela.api.infrastructure.persistence.repository.address;

import com.futela.api.infrastructure.persistence.entity.address.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaAddressRepository extends JpaRepository<AddressEntity, UUID> {

    @Query("SELECT a FROM AddressEntity a " +
            "LEFT JOIN a.district d " +
            "JOIN a.town t " +
            "JOIN a.city c " +
            "WHERE a.deletedAt IS NULL " +
            "AND (LOWER(a.street) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY a.createdAt DESC")
    List<AddressEntity> search(@Param("query") String query);
}
