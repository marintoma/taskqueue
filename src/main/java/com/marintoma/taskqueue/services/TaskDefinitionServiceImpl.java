package com.marintoma.taskqueue.services;

import com.marintoma.taskqueue.dtos.TaskDefinitionRequest;
import com.marintoma.taskqueue.dtos.TaskDefinitionResponse;
import com.marintoma.taskqueue.entities.TaskDefinition;
import com.marintoma.taskqueue.repositories.TaskDefinitionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskDefinitionServiceImpl implements TaskDefinitionService {

    private final TaskDefinitionRepository repository;

    @Override
    @Transactional
    public TaskDefinitionResponse create(TaskDefinitionRequest request) {
        if (repository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("A task definition with the name "
                    + request.name() + " already exists");
        }

        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setName(request.name());
        taskDefinition.setDescription(request.description());
        taskDefinition.setTaskType(request.taskType());
        taskDefinition.setMaxRetries(request.maxRetries() != null ? request.maxRetries() : 3);
        taskDefinition.setTimeoutMs(request.timeoutMs() != null ? request.timeoutMs() : 30000);

        TaskDefinition result = repository.save(taskDefinition);

        return TaskDefinitionResponse.from(result);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDefinitionResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(TaskDefinitionResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDefinitionResponse getById(UUID id) {
        return TaskDefinitionResponse.from(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskDefinition not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskDefinitionResponse> getByName(String name) {
        return repository.findByName(name)
                .map(TaskDefinitionResponse::from);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("TaskDefinition not found: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public TaskDefinitionResponse update(UUID id, TaskDefinitionRequest request) {
        TaskDefinition existing = repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("TaskDefinition not found: " + id));

        existing.setName(request.name());
        existing.setDescription(request.description());
        existing.setTaskType(request.taskType());
        existing.setMaxRetries(request.maxRetries() != null ? request.maxRetries() : existing.getMaxRetries());
        existing.setTimeoutMs(request.timeoutMs() != null ? request.timeoutMs() : existing.getTimeoutMs());

        TaskDefinition result = repository.save(existing);

        return TaskDefinitionResponse.from(result);
    }
}
