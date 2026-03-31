ALTER TABLE task_definitions
    ADD COLUMN webhook_config  JSONB,
    ADD COLUMN executor_config JSONB;