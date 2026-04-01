package com.marintoma.taskqueue.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExecutionProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${taskqueue.kafka.topic}")
    private String topic;

    public void publish(UUID executionId, UUID definitionId) {
        kafkaTemplate.send(topic, definitionId.toString(), executionId.toString())
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish execution {} to Kafka: {}",
                                executionId, ex.getMessage());
                    } else {
                        log.info("Published execution {} to topic {} partition {}",
                                executionId,
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition());
                    }

                });
    }
}
