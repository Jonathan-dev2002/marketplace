package com.jo.marketplace.model.event;

import java.util.UUID;

public record AuditLogRecord(
        UUID userId,
        String action,
        String resourceName,
        String resourceId,
        Object oldValue,
        Object newValue,
        String ipAddress,
        AuditEventMetadata metadata
) {
}
