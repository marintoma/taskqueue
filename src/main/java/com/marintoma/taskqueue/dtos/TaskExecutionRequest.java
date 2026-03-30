package com.marintoma.taskqueue.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record TaskExecutionRequest(

        @NotNull(message = "Definition ID is required")
        UUID definitionId,

        @Future(message = "Scheduled time must be in the future")
        Instant scheduledAt,

        Map<String, Object> payload
) {
}
