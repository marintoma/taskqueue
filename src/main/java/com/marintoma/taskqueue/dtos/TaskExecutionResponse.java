package com.marintoma.taskqueue.dtos;

import com.marintoma.taskqueue.entities.TaskExecution;
import com.marintoma.taskqueue.enums.ExecutionStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record TaskExecutionResponse(
        UUID id,
        UUID definitionId,
        String definitionName,
        ExecutionStatus status,
        Map<String, Object> payload,
        Instant scheduledAt,
        Instant startedAt,
        Instant completedAt,
        int attempt,
        String lastError,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskExecutionResponse from(TaskExecution execution) {
        return new TaskExecutionResponse(
                execution.getId(),
                execution.getDefinition().getId(),
                execution.getDefinition().getName(),
                execution.getStatus(),
                execution.getPayload(),
                execution.getScheduledAt(),
                execution.getStartedAt(),
                execution.getCompletedAt(),
                execution.getAttempt(),
                execution.getLastError(),
                execution.getCreatedAt(),
                execution.getUpdatedAt()
        );
    }
}
