package com.jo.marketplace.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo.marketplace.entity.AuditLogEntity;
import com.jo.marketplace.model.event.AuditEventMetadata;
import com.jo.marketplace.model.event.AuditLogRecord;
import com.jo.marketplace.model.event.ShopCreatedEvent;
import com.jo.marketplace.repository.interfaces.AuditLogRepository;
import com.jo.marketplace.service.interfaces.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.jo.marketplace.constant.EventConstants.SHOP_CREATED;
import static com.jo.marketplace.constant.EventConstants.SHOP_CREATED_ACTION;
import static com.jo.marketplace.constant.EventConstants.SHOP_RESOURCE_NAME;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordAuditLog(AuditLogRecord record) {
        AuditEventMetadata metadata = record.metadata();
        if (metadata != null && metadata.eventId() != null && auditLogRepository.existsByEventId(metadata.eventId())) {
            return;
        }

        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setUserId(record.userId());
        auditLog.setAction(record.action());
        auditLog.setResourceName(record.resourceName());
        auditLog.setResourceId(record.resourceId());
        auditLog.setOldValue(record.oldValue() == null ? null : objectMapper.valueToTree(record.oldValue()));
        auditLog.setNewValue(record.newValue() == null ? null : objectMapper.valueToTree(record.newValue()));
        auditLog.setIpAddress(record.ipAddress());
        auditLog.setEventType(metadata == null ? null : metadata.eventType());
        auditLog.setEventId(metadata == null ? null : metadata.eventId());
        auditLog.setTopic(metadata == null ? null : metadata.topic());
        auditLog.setMessageKey(metadata == null ? null : metadata.messageKey());
        auditLog.setSource(metadata == null ? null : metadata.source());
        auditLog.setProcessedAt(metadata == null ? null : metadata.processedAt());
        auditLog.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordShopCreated(ShopCreatedEvent event, AuditEventMetadata metadata) {
        AuditEventMetadata resolvedMetadata = resolveShopCreatedMetadata(metadata);
        recordAuditLog(new AuditLogRecord(
                event.ownerId(),
                SHOP_CREATED_ACTION,
                SHOP_RESOURCE_NAME,
                event.shopId().toString(),
                null,
                event,
                null,
                resolvedMetadata
        ));
    }

    private AuditEventMetadata resolveShopCreatedMetadata(AuditEventMetadata metadata) {
        if (metadata == null || metadata.eventType() != null) {
            return metadata;
        }

        return new AuditEventMetadata(
                metadata.eventId(),
                SHOP_CREATED,
                metadata.topic(),
                metadata.messageKey(),
                metadata.source(),
                metadata.processedAt()
        );
    }
}
