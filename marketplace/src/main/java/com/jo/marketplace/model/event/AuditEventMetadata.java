package com.jo.marketplace.model.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditEventMetadata(
        UUID eventId,
        String eventType,
        String topic,
        String messageKey,
        String source,
        LocalDateTime processedAt
) {
}
