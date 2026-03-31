package com.marintoma.taskqueue.repositories;

import com.marintoma.taskqueue.entities.TaskDefinition;
import com.marintoma.taskqueue.entities.TaskExecution;
import com.marintoma.taskqueue.enums.ExecutionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, UUID> {

    Page<TaskExecution> findByStatus(ExecutionStatus status, Pageable pageable);

    Page<TaskExecution> findByDefinition(TaskDefinition definition, Pageable pageable);

    Boolean existsByDefinitionIdAndStatusIn(UUID definitionId, List<ExecutionStatus> status);
}
