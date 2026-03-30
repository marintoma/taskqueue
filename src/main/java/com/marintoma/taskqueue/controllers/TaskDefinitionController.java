package com.marintoma.taskqueue.controllers;

import com.marintoma.taskqueue.dtos.TaskDefinitionRequest;
import com.marintoma.taskqueue.dtos.TaskDefinitionResponse;
import com.marintoma.taskqueue.services.TaskDefinitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/definitions")
@RequiredArgsConstructor
public class TaskDefinitionController {

    private final TaskDefinitionService service;

    @PostMapping
    public ResponseEntity<TaskDefinitionResponse> create(
            @Valid @RequestBody TaskDefinitionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<TaskDefinitionResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDefinitionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDefinitionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody TaskDefinitionRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
