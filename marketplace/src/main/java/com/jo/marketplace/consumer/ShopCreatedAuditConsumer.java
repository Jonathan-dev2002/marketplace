package com.jo.marketplace.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo.marketplace.model.event.AuditEventMetadata;
import com.jo.marketplace.model.event.ShopCreatedEvent;
import com.jo.marketplace.service.interfaces.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.jo.marketplace.constant.EventConstants.HEADER_EVENT_ID;
import static com.jo.marketplace.constant.EventConstants.HEADER_EVENT_TYPE;
import static com.jo.marketplace.constant.EventConstants.SHOP_CREATED;
import static com.jo.marketplace.constant.EventConstants.SOURCE_KAFKA_CONSUMER;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopCreatedAuditConsumer {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${app.kafka.topics.shop-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(ConsumerRecord<String, String> record) {
        ShopCreatedEvent event = readEvent(record.value());
        AuditEventMetadata metadata = new AuditEventMetadata(
                readUuidHeader(record, HEADER_EVENT_ID),
                readStringHeader(record, HEADER_EVENT_TYPE, SHOP_CREATED),
                record.topic(),
                record.key(),
                SOURCE_KAFKA_CONSUMER,
                LocalDateTime.now()
        );

        auditLogService.recordShopCreated(event, metadata);
        log.info("Recorded ShopCreated audit log for shop {}", event.shopId());
    }

    private ShopCreatedEvent readEvent(String payload) {
        try {
            return objectMapper.readValue(payload, ShopCreatedEvent.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot deserialize ShopCreated event payload", e);
        }
    }

    private UUID readUuidHeader(ConsumerRecord<String, String> record, String name) {
        String value = readStringHeader(record, name, null);
        return value == null ? null : UUID.fromString(value);
    }

    private String readStringHeader(ConsumerRecord<String, String> record, String name, String defaultValue) {
        Header header = record.headers().lastHeader(name);
        if (header == null) {
            return defaultValue;
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}
