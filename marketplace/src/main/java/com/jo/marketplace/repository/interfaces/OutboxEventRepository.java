package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.OutboxEventEntity;
import com.jo.marketplace.model.enums.OutboxEventStatusEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select event
            from OutboxEventEntity event
            where event.status in :statuses
              and event.retryCount < :maxRetryCount
            order by event.createdDate asc
            """)
    List<OutboxEventEntity> findPublishableEvents(
            @Param("statuses") Collection<OutboxEventStatusEnum> statuses,
            @Param("maxRetryCount") int maxRetryCount,
            Pageable pageable
    );
}
