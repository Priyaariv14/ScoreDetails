package com.cricket.details.publisher;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.scheduling.annotation.Scheduled;

import com.cricket.details.model.OutboxEvent;
import com.cricket.details.repository.OutboxRepository;

import jakarta.transaction.Transactional;

@Component
public class OutboxPublisher {

    private final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String mainTopic;
    private final String dlqTopic;
    private final int maxRetries;
    private final int batchSize;

    public OutboxPublisher(OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.mainTopic: user-scores.v1}") String mainTopic,
            @Value("${app.kafka.dlqTopic: user-scores.dlq}") String dlqTopic,
            @Value("${app.outbox.maxRetries: 5}") int maxRetries,
            @Value("${app.outbox.batchSize: 20}") int batchSize) {
        this.batchSize = batchSize;
        this.dlqTopic = dlqTopic;
        this.mainTopic = mainTopic;
        this.kafkaTemplate = kafkaTemplate;
        this.maxRetries = maxRetries;
        this.outboxRepository = outboxRepository;

    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:2000}")
    @Transactional
    public void poll() {
        Pageable page = PageRequest.of(0, batchSize);
        List<OutboxEvent> outboxEventList = outboxRepository.fetchPendingEvents(page);
        for (OutboxEvent e : outboxEventList) {
            tryPublish(e);
        }
    }

    @Transactional
    public void tryPublish(OutboxEvent event) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(mainTopic, event.getAggregateId(),
                event.getPayload());

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully published event with id={} to topic={}",
                        event.getAggregateId(), mainTopic);
                updateStatus(event);
            } else {
                log.error("Failed to publish event with id={} to topic={}. Error: {}",
                        event.getAggregateId(), mainTopic, ex.getMessage(), ex);
                handleFailures(event);
            }
        });
    }

    @Transactional
    public void updateStatus(OutboxEvent event) {
        var record = outboxRepository.findById(event.getId()).orElseThrow();
        record.setStatus("SENT");
        record.setSentAt(Instant.now());
        outboxRepository.save(record);
    }

    @Transactional
    public void handleFailures(OutboxEvent event) {
        var record = outboxRepository.findById(event.getId()).orElseThrow();
        record.setLastAttemptAt(Instant.now());
        record.setRetryCount(record.getRetryCount() + 1);

        if (record.getRetryCount() > maxRetries) {
            record.setStatus("FAILED");
            outboxRepository.save(record);

            // Try sending to DLQ OUTSIDE transaction
            sendToDLQ(record);
        } else {
            // Retry later by marking it as pending again
            record.setStatus("PENDING");
            outboxRepository.save(record);
        }
    }

    private void sendToDLQ(OutboxEvent record) {
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(mainTopic,
                    record.getAggregateId(),
                    record.getPayload());

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published event with id={} to topic={}",
                            record.getAggregateId(), dlqTopic);
                } else {
                    log.error("Failed to publish event with id={} to topic={}. Error: {}",
                            record.getAggregateId(), dlqTopic, ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            System.err.println("DLQ send threw exception for record: " + record.getId());
        }
    }
}
