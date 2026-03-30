CREATE TYPE execution_status AS ENUM (
    'PENDING',
    'RUNNING',
    'COMPLETED',
    'FAILED',
    'DEAD'
);

CREATE TABLE task_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    definition_id UUID NOT NULL REFERENCES task_definitions(id),
    status execution_status NOT NULL DEFAULT 'PENDING',
    payload JSONB,
    scheduled_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    attempt INT NOT NULL DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_executions_status
    ON task_executions(status);

CREATE INDEX idx_executions_scheduled_at
    ON task_executions(scheduled_at)
    WHERE status = 'PENDING';

CREATE INDEX idx_executions_definition_id
    ON task_executions(definition_id)