CREATE TYPE task_type AS ENUM ('HTTP_WEBHOOK', 'EXECUTOR');

CREATE TABLE task_definitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    task_type task_type NOT NULL,
    max_retries INT NOT NULL DEFAULT 3,
    timeout_ms BIGINT NOT NULL DEFAULT 30000,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);