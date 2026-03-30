package com.marintoma.taskqueue.services;

import com.marintoma.taskqueue.dtos.TaskExecutionRequest;
import com.marintoma.taskqueue.dtos.TaskExecutionResponse;
import com.marintoma.taskqueue.entities.TaskExecution;
import com.marintoma.taskqueue.enums.ExecutionStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Describes what the task execution service should implement
 */
public interface TaskExecutionService {

    /**
     * Schedules a new task execution based on an existing task definition.
     * If no {@code scheduledAt} time is provided in the request, the execution
     * is scheduled immediately.
     *
     * @param request the execution request containing the definition ID,
     *                optional scheduled time, and optional payload
     * @return the newly created {@link TaskExecutionResponse} with status {@code PENDING}
     * @throws EntityNotFoundException if no task definition exists with the given ID
     */
    TaskExecutionResponse create(TaskExecutionRequest request);

    /**
     * Retrieves a task execution by its unique identifier.
     *
     * @param id the UUID of the task execution
     * @return the {@link TaskExecutionResponse} with the given ID
     * @throws EntityNotFoundException if no task execution exists with the given ID
     */
    TaskExecutionResponse getById(UUID id);

    /**
     * Returns a paginated list of task executions filtered by status.
     *
     * @param status   the execution status to filter by
     * @param pageable pagination and sorting parameters
     * @return a {@link Page} of {@link TaskExecutionResponse} objects matching the given status
     */
    Page<TaskExecutionResponse> findByStatus(ExecutionStatus status, Pageable pageable);

    /**
     * Returns a paginated list of task executions belonging to a specific task definition.
     *
     * @param definitionId the UUID of the task definition
     * @param pageable     pagination and sorting parameters
     * @return a {@link Page} of {@link TaskExecutionResponse} objects for the given definition
     * @throws EntityNotFoundException if no task definition exists with the given ID
     */
    Page<TaskExecutionResponse> findByDefinition(UUID definitionId, Pageable pageable);

    /**
     * Transitions a task execution to a new status, enforcing valid state machine rules.
     * Automatically sets {@code startedAt} when transitioning to {@code RUNNING},
     * and {@code completedAt} when transitioning to {@code COMPLETED}, {@code FAILED},
     * or {@code DEAD}.
     *
     * <p>Valid transitions are:
     * <ul>
     *   <li>{@code PENDING} → {@code RUNNING}</li>
     *   <li>{@code RUNNING} → {@code COMPLETED} or {@code FAILED}</li>
     *   <li>{@code FAILED} → {@code PENDING} (retry) or {@code DEAD}</li>
     * </ul>
     *
     * @param id     the UUID of the task execution
     * @param status the target status to transition to
     * @return an updated {@link TaskExecution} as a {@link TaskExecutionResponse}
     * @throws EntityNotFoundException  if no task execution exists with the given ID
     * @throws IllegalArgumentException if the status transition is not valid
     */
    TaskExecutionResponse updateStatus(UUID id, ExecutionStatus status);

    /**
     * Deletes a task execution by its unique identifier.
     *
     * @param id the UUID of the task execution to delete
     * @throws EntityNotFoundException if no task execution exists with the given ID
     */
    void delete(UUID id);
}
