package com.marintoma.taskqueue.validators;

import com.marintoma.taskqueue.dtos.ExecutorConfig;
import com.marintoma.taskqueue.dtos.TaskDefinitionRequest;
import com.marintoma.taskqueue.dtos.WebhookConfig;
import com.marintoma.taskqueue.enums.TaskType;
import com.marintoma.taskqueue.repositories.TaskDefinitionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskDefinitionValidator {

    private final TaskDefinitionRepository repository;

    public void validateForCreate(TaskDefinitionRequest request) {
        validateNoDuplicateName(request);
        validateConfigPresence(request);
        validateNoConflictingConfigs(request);
        validateConfigDetails(request);
    }

    public void validateForUpdate(TaskDefinitionRequest request) {
        validateConfigPresence(request);
        validateNoConflictingConfigs(request);
        validateConfigDetails(request);
    }

    private void validateNoDuplicateName(TaskDefinitionRequest request) {
        if (repository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException(
                    "A task definition with the name '" + request.name() + "' already exists");
        }
    }

    private void validateConfigPresence(TaskDefinitionRequest request) {
        switch (request.taskType()) {
            case HTTP_WEBHOOK -> {
                if (request.webhookConfig() == null) {
                    throw new IllegalArgumentException(
                            "webhookConfig is required for HTTP_WEBHOOK tasks");
                }
            }
            case EXECUTOR -> {
                if (request.executorConfig() == null) {
                    throw new IllegalArgumentException(
                            "executorConfig is required for EXECUTOR tasks");
                }
            }
        }
    }

    private void validateNoConflictingConfigs(TaskDefinitionRequest request) {
        if (request.taskType() == TaskType.HTTP_WEBHOOK && request.executorConfig() != null) {
            throw new IllegalArgumentException(
                    "executorConfig should not be provided for HTTP_WEBHOOK tasks");
        }
        if (request.taskType() == TaskType.EXECUTOR && request.webhookConfig() != null) {
            throw new IllegalArgumentException(
                    "webhookConfig should not be provided for EXECUTOR tasks");
        }
    }

    private void validateConfigDetails(TaskDefinitionRequest request) {
        if (request.taskType() == TaskType.HTTP_WEBHOOK) {
            validateWebhookConfig(request.webhookConfig());
        }
        if (request.taskType() == TaskType.EXECUTOR) {
            validateExecutorConfig(request.executorConfig());
        }
    }

    private void validateWebhookConfig(WebhookConfig config) {
        if (config.successStatusCodes() != null) {
            boolean invalidCodes = config.successStatusCodes().stream()
                    .anyMatch(code -> code < 100 || code > 599);
            if (invalidCodes) {
                throw new IllegalArgumentException(
                        "successStatusCodes must be valid HTTP status codes between 100 and 599");
            }
        }
    }

    private void validateExecutorConfig(ExecutorConfig config) {
        try {
            Class.forName(config.handlerClass());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(
                    "Handler class not found on classpath: " + config.handlerClass());
        }
    }
}
