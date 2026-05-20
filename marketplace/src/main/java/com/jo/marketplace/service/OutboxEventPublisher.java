package com.jo.marketplace.service;

import com.jo.marketplace.entity.OutboxEventEntity;
import com.jo.marketplace.model.enums.OutboxEventStatusEnum;
import com.jo.marketplace.repository.interfaces.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.outbox.publisher", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.outbox.publisher.batch-size:50}")
    private int batchSize;

    @Value("${app.outbox.publisher.max-retry-count:5}")
    private int maxRetryCount;

    @Value("${app.outbox.publisher.send-timeout-ms:10000}")
    private long sendTimeoutMs;

    @Scheduled(fixedDelayString = "${app.outbox.publisher.publish-interval-ms:5000}")
    @Transactional(rollbackFor = Exception.class)
    public void publishPendingEvents() {
        List<OutboxEventEntity> events = outboxEventRepository.findPublishableEvents(
                List.of(OutboxEventStatusEnum.PENDING, OutboxEventStatusEnum.FAILED),
                maxRetryCount,
                PageRequest.of(0, batchSize)
        );

        for (OutboxEventEntity event : events) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEventEntity event) {
        try {
            kafkaTemplate.send(event.getTopic(), event.getMessageKey(), event.getPayload())
                    .get(sendTimeoutMs, TimeUnit.MILLISECONDS);

            event.setStatus(OutboxEventStatusEnum.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());
            event.setLastError(null);
            outboxEventRepository.save(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            markFailed(event, e);
        } catch (Exception e) {
            markFailed(event, e);
        }
    }

    private void markFailed(OutboxEventEntity event, Exception exception) {
        event.setStatus(OutboxEventStatusEnum.FAILED);
        event.setRetryCount(event.getRetryCount() + 1);
        event.setLastError(trimError(exception.getMessage()));
        outboxEventRepository.save(event);

        log.warn(
                "Failed to publish outbox event {} to topic {}. Retry count: {}",
                event.getId(),
                event.getTopic(),
                event.getRetryCount(),
                exception
        );
    }

    private String trimError(String message) {
        if (message == null) {
            return null;
        }
        int maxLength = 2000;
        return message.length() <= maxLength ? message : message.substring(0, maxLength);
    }
}
