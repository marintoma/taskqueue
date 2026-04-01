package com.marintoma.taskqueue.workers;

import com.marintoma.taskqueue.entities.TaskExecution;
import com.marintoma.taskqueue.enums.ExecutionStatus;
import com.marintoma.taskqueue.exceptions.TaskHandlerException;
import com.marintoma.taskqueue.repositories.TaskExecutionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExecutionWorker {

    private final TaskExecutionRepository execRepo;
    private final TaskHandlerRegistry handlerRegistry;

    @Transactional
    public void process(UUID executionId) {
        TaskExecution exec = execRepo.findById(executionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "TaskExecution with id " + executionId + " not found"
                ));

        markRunning(exec);

        try {
            TaskHandler handler = handlerRegistry.getHandler(
                    exec.getDefinition().getTaskType()
            );
            handler.handle(exec);
            markCompleted(exec);
        } catch (TaskHandlerException e) {
            log.error("Execution {} failed with handler error: {}",
                    executionId, e.getMessage());
            markFailed(exec, e.getMessage());
        } catch (Exception e) {
            log.error("Execution {} failed: {}", executionId, e.getMessage());
            markFailed(exec, e.getMessage());
        }

    }

    private void markRunning(TaskExecution execution) {
        execution.setStatus(ExecutionStatus.RUNNING);
        execution.setStartedAt(Instant.now());
        execution.setAttempt(execution.getAttempt() + 1);
        execRepo.save(execution);
    }

    private void markCompleted(TaskExecution execution) {
        execution.setStatus(ExecutionStatus.COMPLETED);
        execution.setCompletedAt(Instant.now());
        execRepo.save(execution);
        log.info("Execution {} completed successfully", execution.getId());
    }

    private void markFailed(TaskExecution execution, String error) {
        execution.setStatus(ExecutionStatus.FAILED);
        execution.setCompletedAt(Instant.now());
        execution.setLastError(error);
        execRepo.save(execution);
    }
}
