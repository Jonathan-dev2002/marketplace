package com.jo.marketplace.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo.marketplace.entity.OutboxEventEntity;
import com.jo.marketplace.model.enums.OutboxEventStatusEnum;
import com.jo.marketplace.repository.interfaces.OutboxEventRepository;
import com.jo.marketplace.service.interfaces.OutboxEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventServiceImpl implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public void saveEvent(
            String topic,
            String messageKey,
            String eventType,
            String aggregateType,
            UUID aggregateId,
            Object payload
    ) {
        OutboxEventEntity event = new OutboxEventEntity();
        event.setTopic(topic);
        event.setMessageKey(messageKey);
        event.setEventType(eventType);
        event.setAggregateType(aggregateType);
        event.setAggregateId(aggregateId);
        event.setPayload(toJson(payload));
        event.setStatus(OutboxEventStatusEnum.PENDING);
        event.setRetryCount(0);

        outboxEventRepository.save(event);
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize outbox event payload", e);
        }
    }
}
