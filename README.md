# TaskQueue

A distributed task scheduling system built with Java 21 and Spring Boot, inspired by production-grade job queues used at companies like Uber and Airbnb.

The system is designed around a clear separation of concerns, where each infrastructure component owns exactly one responsibility, and the architecture 
is built incrementally across versioned releases.

## Architecture

```
Client
  │
  ▼
REST API (Spring Boot)
  │
  ├──► PostgreSQL         — task definitions + execution state (source of truth)
  │
  ├──► Redis (ZSET)       — scheduling buffer, score = epoch execution time
  │         │
  │         ▼
  │    Scheduler Nodes    — poll Redis every second, Lua script atomic pop
  │         │
  │         ▼
  ├──► Kafka              — reliable delivery to workers, tenant-based partitioning
  │         │
  │         ▼
  │    Worker Nodes       — claim task, execute, ACK, update state
  │         │
  │         ▼
  └──► Cassandra          — append-only execution logs, keyed by execution ID
```

**Each store, one responsibility:**

| Store | Role |
|-------|------|
| PostgreSQL | Structured state — task definitions and execution records |
| Redis | Scheduling — ZSET where score = Unix epoch timestamp |
| Kafka | Delivery — reliable, partitioned message queue for workers |
| Cassandra | History — append-only execution logs, queried by execution ID |

## Tech Stack

- **Java 25** — virtual threads for worker concurrency
- **Spring Boot 4.0.5** — web, JPA, Kafka, Redis, Cassandra
- **PostgreSQL 16** — primary data store
- **Apache Kafka** — message broker
- **Redis** — scheduling buffer (sorted sets)
- **Apache Cassandra** — execution log store
- **Flyway** — schema migrations
- **Docker Compose** — local infrastructure

## Roadmap

### v0.1 — Foundation ✅
Task Management Service with PostgreSQL. REST API for creating task definitions and scheduling executions. Full data model with Flyway migrations, input validation, global error handling, and DTO mapping.

### v0.2 — Basic Execution
Kafka integration and Worker nodes. End-to-end job execution — submit a task, watch it run. PostgreSQL status updates via the worker.

### v0.3 — Scheduling
Redis ZSET scheduling buffer and Relay nodes. Tasks can be scheduled for future execution. Lua script for atomic pop from Redis to Kafka — zero race conditions between scheduler nodes.

### v0.4 — Reliability
Visibility timeout, optimistic locking on task executions, and retry logic with exponential backoff. The system survives worker crashes and concurrent execution races.

### v0.5 — Exactness
Idempotency keys on execution. Safe to call external HTTP webhooks — at-least-once delivery with exactly-once semantics at the target system.

### v0.6 — Observability
Async Cassandra log flushing. Full execution history queryable by execution ID. Workers fire-and-forget logs without blocking on Cassandra writes.

### v0.7 — Multi-tenancy
Tenant-based Kafka partition routing. Noisy neighbor protection — one tenant's high job volume cannot starve other tenants.

## Running Locally

**Prerequisites:** Docker, Java 21, Maven

```bash
# start infrastructure
docker compose up -d

# run the app
./mvnw spring-boot:run
```

App starts on `http://localhost:8080`.
