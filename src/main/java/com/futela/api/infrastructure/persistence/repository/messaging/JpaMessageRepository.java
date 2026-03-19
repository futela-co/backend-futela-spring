package com.futela.api.infrastructure.persistence.repository.messaging;

import com.futela.api.infrastructure.persistence.entity.messaging.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaMessageRepository extends JpaRepository<MessageEntity, UUID> {

    @Query("SELECT m FROM MessageEntity m WHERE m.conversation.id = :conversationId AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    Page<MessageEntity> findByConversationId(@Param("conversationId") UUID conversationId, Pageable pageable);

    @Query("""
        SELECT COUNT(m) FROM MessageEntity m
        JOIN m.conversation c
        JOIN c.participants p
        WHERE p.id = :userId
        AND m.sender.id != :userId
        AND m.isRead = false
        AND m.deletedAt IS NULL
    """)
    long countUnreadByUserId(@Param("userId") UUID userId);
}
