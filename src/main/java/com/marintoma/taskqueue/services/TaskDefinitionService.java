package com.marintoma.taskqueue.services;

import com.marintoma.taskqueue.dtos.TaskDefinitionRequest;
import com.marintoma.taskqueue.dtos.TaskDefinitionResponse;
import com.marintoma.taskqueue.entities.TaskDefinition;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface describing what the task definition service provides.
 */
public interface TaskDefinitionService {

    /**
     * Create a new task definition.
     *
     * @param request the task definition request
     * @return the newly created task definition.
     * @throws IllegalArgumentException if the task definition name exists in the DB.
     */
    TaskDefinitionResponse create(TaskDefinitionRequest request);

    /**
     * Retrieve all task definitions defined.
     *
     * @param pageable object with page metadata
     * @return a page of the task definitions.
     */
    Page<TaskDefinitionResponse> findAll(Pageable pageable);

    /**
     * Get the task definition by the specified ID.
     *
     * @param id ID of the task definition.
     * @return the task definition.
     * @throws EntityNotFoundException if the entity with the given ID was not found.
     */
    TaskDefinitionResponse getById(UUID id);

    /**
     * Retrieve the task definition by its name.
     *
     * @param name name of the task definition.
     * @return the task definition optional.
     */
    Optional<TaskDefinitionResponse> getByName(String name);

    /**
     * Update a given task definition.
     *
     * @param id id of the task definition
     * @param request the new data to be updated
     * @return the newly updated task
     * @throws EntityNotFoundException if entity not found.
     */
    TaskDefinitionResponse update(UUID id, TaskDefinitionRequest request);

    /**
     * Delete the task definition with the given ID.
     *
     * @param id of the task definition.
     * @throws EntityNotFoundException if the entity wasn't found.
     */
    void delete(UUID id);
}
