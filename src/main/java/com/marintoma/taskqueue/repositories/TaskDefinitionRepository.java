package com.marintoma.taskqueue.repositories;

import com.marintoma.taskqueue.entities.TaskDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, UUID> {

    Optional<TaskDefinition> findByName(String name);
}
