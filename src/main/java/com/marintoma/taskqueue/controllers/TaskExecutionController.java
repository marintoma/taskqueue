package com.marintoma.taskqueue.controllers;

import com.marintoma.taskqueue.dtos.TaskExecutionRequest;
import com.marintoma.taskqueue.dtos.TaskExecutionResponse;
import com.marintoma.taskqueue.enums.ExecutionStatus;
import com.marintoma.taskqueue.services.TaskExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/executions")
@RequiredArgsConstructor
public class TaskExecutionController {

    private final TaskExecutionService service;

    @PostMapping
    public ResponseEntity<TaskExecutionResponse> create(
            @Valid @RequestBody TaskExecutionRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskExecutionResponse> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TaskExecutionResponse>> findByStatus(
            @PathVariable ExecutionStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findByStatus(status, pageable));
    }

    @GetMapping("/definition/{definitionId}")
    public ResponseEntity<Page<TaskExecutionResponse>> findByDefinition(
            @PathVariable UUID definitionId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findByDefinition(definitionId, pageable));
    }
}
