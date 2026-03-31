package com.marintoma.taskqueue.dtos;

import com.marintoma.taskqueue.entities.TaskDefinition;
import com.marintoma.taskqueue.enums.TaskType;

import java.time.Instant;
import java.util.UUID;

public record TaskDefinitionResponse(
        UUID id,
        String name,
        String description,
        TaskType taskType,
        int maxRetries,
        long timeoutMs,
        WebhookConfig webhookConfig,
        ExecutorConfig executorConfig,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskDefinitionResponse from(TaskDefinition taskDefinition) {
        return new TaskDefinitionResponse(
                taskDefinition.getId(),
                taskDefinition.getName(),
                taskDefinition.getDescription(),
                taskDefinition.getTaskType(),
                taskDefinition.getMaxRetries(),
                taskDefinition.getTimeoutMs(),
                taskDefinition.getWebhookConfig(),
                taskDefinition.getExecutorConfig(),
                taskDefinition.getCreatedAt(),
                taskDefinition.getUpdatedAt()
        );
    }
}
