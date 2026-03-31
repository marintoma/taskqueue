package com.marintoma.taskqueue.dtos;

import com.marintoma.taskqueue.enums.TaskType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record TaskDefinitionRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must be under 255 characters")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Name must be lowercase alphanumeric with hyphens only")
        String name,

        @Size(max = 1000, message = "Description must be under 1000 characters")
        String description,

        @NotNull(message = "Task type is required")
        TaskType taskType,

        @Min(value = 0, message = "Max retries can't be negative")
        @Max(value = 10, message = "Max retries can't exceed 10")
        Integer maxRetries,

        @Min(value = 1000, message = "Timeout must be at least 1000ms")
        @Max(value = 300000, message = "Timeout cannot exceed 300000ms")
        Long timeoutMs,

        @Valid
        WebhookConfig webhookConfig,

        @Valid
        ExecutorConfig executorConfig
) {
}
