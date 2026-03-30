package com.marintoma.taskqueue.services;

import com.marintoma.taskqueue.dtos.TaskExecutionRequest;
import com.marintoma.taskqueue.dtos.TaskExecutionResponse;
import com.marintoma.taskqueue.entities.TaskDefinition;
import com.marintoma.taskqueue.entities.TaskExecution;
import com.marintoma.taskqueue.enums.ExecutionStatus;
import com.marintoma.taskqueue.repositories.TaskDefinitionRepository;
import com.marintoma.taskqueue.repositories.TaskExecutionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskExecutionServiceImpl implements TaskExecutionService {

    private final TaskExecutionRepository execRepo;
    private final TaskDefinitionRepository defRepo;

    @Override
    @Transactional
    public TaskExecutionResponse create(TaskExecutionRequest request) {
        TaskDefinition definition = getTaskDefinitionById(request.definitionId());

        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setDefinition(definition);
        taskExecution.setPayload(request.payload());
        taskExecution.setScheduledAt(
                request.scheduledAt() != null ? request.scheduledAt() : Instant.now()
        );
        taskExecution.setStatus(ExecutionStatus.PENDING);

        TaskExecution result = execRepo.save(taskExecution);

        return TaskExecutionResponse.from(result);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskExecutionResponse getById(UUID id) {
        return execRepo.findById(id)
                .map(TaskExecutionResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("TaskExecution with ID " + id + " not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskExecutionResponse> findByStatus(ExecutionStatus status, Pageable pageable) {
        return execRepo.findByStatus(status, pageable)
                .map(TaskExecutionResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskExecutionResponse> findByDefinition(UUID definitionId, Pageable pageable) {
        TaskDefinition definition = getTaskDefinitionById(definitionId);

        return execRepo.findByDefinition(definition, pageable)
                .map(TaskExecutionResponse::from);
    }

    @Override
    @Transactional
    public TaskExecutionResponse updateStatus(UUID id, ExecutionStatus status) {
        TaskExecution taskExecution = execRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskExecution with ID " + id + " not found!"));

        validateStatusTransition(taskExecution.getStatus(), status);

        taskExecution.setStatus(status);

        if (status == ExecutionStatus.RUNNING) {
            taskExecution.setStartedAt(Instant.now());
        } else if (status == ExecutionStatus.COMPLETED || status == ExecutionStatus.FAILED
                    || status == ExecutionStatus.DEAD) {
            taskExecution.setCompletedAt(Instant.now());
        }

        TaskExecution result = execRepo.save(taskExecution);

        return TaskExecutionResponse.from(result);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!execRepo.existsById(id)) {
            throw new EntityNotFoundException("TaskExecution with ID " + id + " not found!");
        }
        execRepo.deleteById(id);
    }

    private TaskDefinition getTaskDefinitionById(UUID id) {
        return defRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskDefinition with ID " + id + " not found!"));
    }

    /**
     * Validates the transition between states.
     * <p>Valid transitions are:
     * <ul>
     *   <li>{@code PENDING} → {@code RUNNING}</li>
     *   <li>{@code RUNNING} → {@code COMPLETED} or {@code FAILED}</li>
     *   <li>{@code FAILED} → {@code PENDING} (retry) or {@code DEAD}</li>
     * </ul>
     *
     *
     * @param current current state of the {@link TaskExecution}
     * @param next the potential next state of the {@link TaskExecution}
     * @throws IllegalArgumentException if the state transition is invalid
     */
    private void validateStatusTransition(ExecutionStatus current, ExecutionStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next.equals(ExecutionStatus.RUNNING);
            case RUNNING -> next.equals(ExecutionStatus.COMPLETED) || next.equals(ExecutionStatus.FAILED);
            case FAILED -> next.equals(ExecutionStatus.PENDING) || next.equals(ExecutionStatus.DEAD);
            case COMPLETED, DEAD -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid status transition: " + current + " -> " + next
            );
        }
    }
}
