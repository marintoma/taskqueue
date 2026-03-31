package com.marintoma.taskqueue.entities;

import com.marintoma.taskqueue.dtos.ExecutorConfig;
import com.marintoma.taskqueue.dtos.WebhookConfig;
import com.marintoma.taskqueue.enums.TaskType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "task_definitions")
@Getter
@Setter
@NoArgsConstructor
public class TaskDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "task_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TaskType taskType;

    @Column(nullable = false)
    private int maxRetries = 3;

    @Column(nullable = false)
    private long timeoutMs = 30000;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private WebhookConfig webhookConfig;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private ExecutorConfig executorConfig;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
