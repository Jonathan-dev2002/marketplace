package com.jo.marketplace.model.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShopCreatedEvent(
        UUID shopId,
        UUID ownerId,
        String name,
        String slug,
        String description,
        String logoUrl,
        LocalDateTime occurredAt
) {
}
