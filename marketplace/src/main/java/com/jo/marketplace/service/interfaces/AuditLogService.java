package com.jo.marketplace.service.interfaces;

import com.jo.marketplace.model.event.AuditEventMetadata;
import com.jo.marketplace.model.event.AuditLogRecord;
import com.jo.marketplace.model.event.ShopCreatedEvent;

public interface AuditLogService {

    void recordAuditLog(AuditLogRecord record);

    void recordShopCreated(ShopCreatedEvent event, AuditEventMetadata metadata);
}
