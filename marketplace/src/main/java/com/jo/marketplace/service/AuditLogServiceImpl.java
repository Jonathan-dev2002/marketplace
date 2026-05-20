package com.jo.marketplace.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo.marketplace.entity.AuditLogEntity;
import com.jo.marketplace.model.event.AuditEventMetadata;
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
    public void recordShopCreated(ShopCreatedEvent event, AuditEventMetadata metadata) {
        if (metadata.eventId() != null && auditLogRepository.existsByEventId(metadata.eventId())) {
            return;
        }

        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setUserId(event.ownerId());
        auditLog.setAction(SHOP_CREATED_ACTION);
        auditLog.setResourceName(SHOP_RESOURCE_NAME);
        auditLog.setResourceId(event.shopId().toString());
        auditLog.setOldValue(null);
        auditLog.setNewValue(objectMapper.valueToTree(event));
        auditLog.setIpAddress(null);
        auditLog.setEventType(metadata.eventType() == null ? SHOP_CREATED : metadata.eventType());
        auditLog.setEventId(metadata.eventId());
        auditLog.setTopic(metadata.topic());
        auditLog.setMessageKey(metadata.messageKey());
        auditLog.setSource(metadata.source());
        auditLog.setProcessedAt(metadata.processedAt());
        auditLog.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }
}
