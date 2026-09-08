# PulseRide AI Coding Context

## Project identity

PulseRide is a distributed, event-driven ride-hailing platform built as a multi-service Spring Boot architecture. The current implementation is already beyond the initial scaffold: the auth and driver services are working and the project is now transitioning from foundational service implementation into the ride, matching, pricing, payment, Kafka, and gateway layers.

## Current implementation status

### Completed and working

#### Auth Service

Location: `backend/auth-service`

Implemented and validated:
- User registration at `POST /auth/register`
- User login at `POST /auth/login`
- Access-token and refresh-token issuance
- Refresh-token rotation at `POST /auth/refresh`
- Authenticated logout at `POST /auth/logout`
- BCrypt password hashing
- JWT signing with HMAC HS256
- JWT claims include issuer, subject/user ID, issued-at, expiry, email, and role
- JWT validation with issuer checking
- Role normalization and email normalization
- Duplicate-email and invalid-credential handling
- Centralized error responses
- Driver-profile creation flow for newly registered drivers
- Unit tests for core auth flows and failure cases

#### Driver Service

Location: `backend/driver-service`

Implemented and validated:
- Driver profile creation or lookup at `POST /drivers/profile`
- Current profile fetch at `GET /drivers/me`
- Driver location update at `POST /drivers/location`
- Driver availability update at `PATCH /drivers/status`
- Vehicle type update at `PATCH /drivers/vehicle-type`
- Internal profile creation endpoint at `POST /internal/drivers/profile`
- Server-side driver status rules
- Postgres-backed JPA persistence for drivers
- Location validation using `BigDecimal`
- Security rules enforcing `DRIVER` access on driver endpoints
- Unit tests covering transitions, ownership checks, location updates, and auth restrictions

These two services are the first completed foundation and are expected to remain the base from which future services are extended.

## Architecture status

### Completed foundation

- Java 17
- Spring Boot 3.5.5 in auth and driver services
- Spring Web MVC / REST APIs
- Spring Data JPA and Hibernate
- PostgreSQL for service-owned transactional data
- Spring Security
- OAuth2 resource-server JWT validation
- Nimbus JWT encoder/decoder
- BCrypt password hashing
- Docker Compose for local infrastructure

### Planned / not yet implemented

The following are still in the roadmap and are not production-complete yet:
- Spring Cloud Gateway
- Kafka domain events and consumer patterns
- Redis geospatial driver state and matching support
- Ride service lifecycle management
- Matching service assignment logic
- Pricing service surge/fare logic
- Payment service provider integration and webhooks
- Notification service
- Admin dashboard and governance tooling
- Observability stack (OpenTelemetry, Prometheus, Grafana)

## Service ownership and boundaries

This project is intentionally structured around service ownership:

- Auth/User Service owns user identity, password hashes, roles, permissions, and account data.
- Driver Service owns driver profile, status, vehicle metadata, and driver location updates.
- Tracking Service owns live GPS and driver availability state.
- Ride Service owns ride lifecycle, pickup/dropoff, assignment state, and cancellations.
- Matching Service owns candidate discovery, scoring, and assignment workflow.
- Pricing Service owns fare rules and surge rules.
- Payment Service owns payment orders, provider references, verification, refunds, and webhook event records.
- Notification Service consumes events and sends user updates.

Important rule: do not access another service's database directly. Keep boundaries strict.

## Coding principles for future work

1. Follow service ownership. Never directly access another service's database.
2. Prefer DTOs at REST boundaries and validate all external input.
3. Derive authenticated identity from the JWT; never trust frontend-supplied user IDs or roles.
4. Use constructor injection and Jakarta namespaces.
5. Use `BigDecimal` or integer minor units for money.
6. Keep provider-specific payment code inside the payment-service adapter layer.
7. Use idempotent Kafka consumers and safe retry patterns.
8. Use transactional outbox for important DB-to-Kafka writes where applicable.
9. Do not invent APIs, fields, or events not defined by the contracts.
10. Keep authorization checks server-side and consistent with RBAC.
11. Add failure-path testing for security-sensitive features.
12. Preserve the architectural boundaries unless a migration is required.

## What is already done versus what remains

### Done

- Auth flow with registration, login, refresh, and logout
- Driver profile and status management
- JWT authentication + role enforcement
- Validation and error handling patterns
- Basic driver ownership logic and auth checks
- Service-level persistence and tests for the current features

### Still needed

- Gateway routing and centralized security policy
- Full ride lifecycle and ride-state machine
- Matching logic and driver assignment workflow
- Pricing and surge calculation
- Payment gateway integration and webhook verification
- Kafka event design and consumer wiring
- Redis geospatial tracking for matching and driver discovery
- Admin functions and audit trajectories
- Observability, resilience, and production-hardening

## Recommended next work order

1. Complete the driver ride retrieval contract and replace current placeholder `List<Object>` behavior with typed DTOs.
2. Add controller and API tests for auth and driver endpoints, especially validation and authorization failures.
3. Introduce integration tests with PostgreSQL/Testcontainers for persistence-heavy paths.
4. Harden internal service-to-service communication and define a consistent internal error contract.
5. Add the API gateway and centralized authentication/routing behavior.
6. Implement the ride service state machine and connect it to driver availability.
7. Add matching service assignment, driver scoring, and idempotent assignment logic.
8. Implement pricing and surge calculations in the pricing service.
9. Implement payment service flows with provider adapter, verification, idempotency, and refund handling.
10. Add Kafka events, transactional outbox, retry/DLQ handling, and idempotent consumers.
11. Add Redis geospatial and low-latency tracking when the matching design is ready.
12. Add admin dashboard, audit logging, and production-readiness improvements.
13. Add OpenTelemetry, Prometheus, Grafana, and distributed tracing.

## MVP stopping point

The project is already in a strong early portfolio state. For a solid MVP, the recommended stopping point is after Phase 7 of the roadmap if time is limited. That gives a working foundation with auth, driver, ride, matching, pricing, payment, and admin capabilities, while keeping the architecture realistic and demonstrable.

## Rules for future AI agents

Use this context when changing PulseRide:

```text
You are modifying the PulseRide ride-hailing platform. First identify the owning service, existing DTOs/entities/repositories, API contract, authorization rule, database ownership, event or idempotency requirements, and failure scenarios. Preserve the established Spring Boot 3.x, Java 17, PostgreSQL/JPA, Spring Security, JWT, validation, Lombok, and Maven conventions. Keep changes minimal, compilable, and aligned with current service boundaries. Do not access another service's database, trust frontend identity or payment state, invent API fields, or expose secrets. Add or update tests around the changed behavior, especially validation and authorization flows. Follow the completed architecture work in the auth and driver services, and build the remaining platform features in the documented order.
```

## Source references

- `backend/docs/ARCHITECTURE.md`
- `backend/docs/IMPLEMENTATION_ROADMAP.md`
- `backend/docs/AI_IMPLEMENTATION_HANDOFF.md`
- `backend/docs/SERVICE_BOUNDARIES.md`
- `backend/docs/FEATURE_BACKLOG.md`
- `backend/docs/API_CONTRACTS.md`
- `backend/docs/DATA_MODEL.md`
- `backend/docs/SECURITY.md`
- `backend/docs/RBAC.md`
- `backend/docs/FAILURE_HANDLING.md`

## Final working summary

The codebase has already delivered the working baseline for identity and driver operations. The project is now at the stage where the next major value is in building the ride, matching, pricing, payment, Kafka, and gateway layers according to the documented roadmap. Future implementation should remain disciplined, boundary-aware, and test-first to preserve the architecture and avoid breaking the service ownership model.
