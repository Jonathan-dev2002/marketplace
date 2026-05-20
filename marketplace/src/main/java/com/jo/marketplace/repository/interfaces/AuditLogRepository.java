package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    boolean existsByEventId(UUID eventId);
}
