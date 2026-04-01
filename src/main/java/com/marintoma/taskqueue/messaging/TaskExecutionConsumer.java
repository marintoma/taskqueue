package com.marintoma.taskqueue.messaging;

import com.marintoma.taskqueue.workers.TaskExecutionWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExecutionConsumer {

    private final TaskExecutionWorker worker;

    @KafkaListener(
            topics = "${taskqueue.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String, String> rec) {
        UUID executionId = UUID.fromString(rec.value());
        log.info("Received execution {} from partition {}", executionId, rec.partition());

        worker.process(executionId);
    }
}
