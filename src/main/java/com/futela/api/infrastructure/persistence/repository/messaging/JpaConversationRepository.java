package com.futela.api.infrastructure.persistence.repository.messaging;

import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaConversationRepository extends JpaRepository<ConversationEntity, UUID> {

    @Query("SELECT DISTINCT c FROM ConversationEntity c LEFT JOIN FETCH c.participants LEFT JOIN FETCH c.property JOIN c.participants p WHERE p.id = :userId AND c.deletedAt IS NULL ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<ConversationEntity> findByParticipantId(@Param("userId") UUID userId);

    @Query("""
        SELECT c FROM ConversationEntity c
        LEFT JOIN FETCH c.participants
        LEFT JOIN FETCH c.property
        JOIN c.participants p1
        JOIN c.participants p2
        WHERE p1.id = :user1Id AND p2.id = :user2Id
        AND c.deletedAt IS NULL
    """)
    Optional<ConversationEntity> findByParticipants(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);

    @Query("""
        SELECT c FROM ConversationEntity c
        LEFT JOIN FETCH c.participants
        LEFT JOIN FETCH c.property
        JOIN c.participants p1
        JOIN c.participants p2
        WHERE p1.id = :user1Id AND p2.id = :user2Id
        AND c.property.id = :propertyId
        AND c.deletedAt IS NULL
    """)
    Optional<ConversationEntity> findByParticipantsAndProperty(
            @Param("user1Id") UUID user1Id,
            @Param("user2Id") UUID user2Id,
            @Param("propertyId") UUID propertyId
    );

    @Query("""
        SELECT DISTINCT c FROM ConversationEntity c
        LEFT JOIN FETCH c.participants
        LEFT JOIN FETCH c.property
        JOIN c.participants p
        WHERE p.id = :userId AND c.deletedAt IS NULL
        AND (:query IS NULL OR LOWER(c.subject) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:propertyId IS NULL OR c.property.id = :propertyId)
        AND (:includeArchived = true OR c.isArchived = false)
        ORDER BY c.lastMessageAt DESC NULLS LAST
    """)
    List<ConversationEntity> searchByUserId(
            @Param("userId") UUID userId,
            @Param("query") String query,
            @Param("propertyId") UUID propertyId,
            @Param("includeArchived") boolean includeArchived
    );
}
