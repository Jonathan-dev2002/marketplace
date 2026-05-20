package com.jo.marketplace.service.interfaces;

import java.util.UUID;

public interface OutboxEventService {

    void saveEvent(
            String topic,
            String messageKey,
            String eventType,
            String aggregateType,
            UUID aggregateId,
            Object payload
    );
}
