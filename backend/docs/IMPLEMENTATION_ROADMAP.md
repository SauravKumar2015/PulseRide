# Implementation Roadmap

## Phase 0 — Foundation

- [ ] Multi-module Maven structure
- [ ] Docker Compose
- [ ] Kafka KRaft
- [ ] PostgreSQL
- [ ] Redis
- [ ] Gateway
- [ ] common error response
- [ ] correlation/trace ID

## Phase 1 — Identity and RBAC

- [ ] User registration/login
- [ ] BCrypt/Argon2 password hashing
- [ ] JWT access/refresh flow
- [ ] roles
- [ ] permissions
- [ ] ownership checks
- [ ] audit logs

## Phase 2 — Tracking

- [ ] Driver WebSocket connection
- [ ] Redis GEOADD
- [ ] driver availability
- [ ] stale-location TTL
- [ ] location Kafka events
- [ ] simulator

## Phase 3 — Ride and matching

- [ ] Ride state machine
- [ ] ride request event
- [ ] candidate search
- [ ] driver scoring
- [ ] distributed locking
- [ ] idempotent assignment
- [ ] assignment event

## Phase 4 — Pricing

- [ ] fare quote
- [ ] spatial surge zones
- [ ] supply/demand calculation
- [ ] bounded surge multiplier
- [ ] pricing snapshot attached to ride

## Phase 5 — Payment

- [ ] Payment Service
- [ ] provider adapter
- [ ] create order
- [ ] frontend checkout
- [ ] signature verification
- [ ] webhook handling
- [ ] idempotency
- [ ] refund flow
- [ ] payment reconciliation
- [ ] payment Kafka events

## Phase 6 — Reliability

- [ ] transactional outbox
- [ ] retry policy
- [ ] DLQ
- [ ] circuit breakers
- [ ] rate limiting
- [ ] timeout policies

## Phase 7 — Admin

- [ ] admin dashboard
- [ ] live map
- [ ] user management
- [ ] driver management
- [ ] ride operations
- [ ] payment operations
- [ ] surge management
- [ ] audit logs

## Phase 8 — Observability

- [ ] OpenTelemetry
- [ ] Prometheus
- [ ] Grafana
- [ ] distributed tracing
- [ ] Kafka lag dashboards
- [ ] payment dashboards

## Phase 9 — Advanced

- [ ] ETA scoring
- [ ] driver rebalancing
- [ ] predictive demand
- [ ] cancellation prediction
- [ ] fraud/anomaly detection
- [ ] chaos testing

## MVP stopping point

For a strong portfolio version, stop after Phase 7 if time is limited. Phase 8 makes the architecture more production-like; Phase 9 is optional differentiation.
