package com.marintoma.taskqueue.entities;

import com.marintoma.taskqueue.enums.ExecutionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "task_executions")
@Getter
@Setter
@NoArgsConstructor
public class TaskExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private TaskDefinition definition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "execution_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ExecutionStatus status = ExecutionStatus.PENDING;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(nullable = false)
    private Instant scheduledAt = Instant.now();

    private Instant startedAt;

    private Instant completedAt;

    @Column(nullable = false)
    private int attempt = 0;

    @Column(columnDefinition = "text")
    private String lastError;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    // automatically update this field on update
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // automatically init these field before persisting (safe)
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
