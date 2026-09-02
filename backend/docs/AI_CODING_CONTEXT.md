# PulseRide AI Coding Context

## Project identity

You are working on PulseRide, a distributed event-driven ride-hailing platform.

## Non-negotiable architecture

- Java 17 or 21
- Spring Boot 3.x
- Spring Security
- Spring Cloud Gateway
- PostgreSQL for transactional service-owned data
- Redis Stack for geospatial/low-latency state
- Kafka for asynchronous domain events
- React + Vite + Tailwind for frontend
- Docker Compose for local infrastructure

## Coding principles

1. Follow service ownership. Never directly access another service's database.
2. Prefer DTOs at API boundaries.
3. Validate all external input.
4. Use immutable event envelopes where practical.
5. Every Kafka event must contain `eventId`, `eventType`, `version`, `occurredAt`, `correlationId`, producer, aggregate type/id, and payload.
6. Kafka consumers must be idempotent.
7. Use transactional outbox for important DB-to-Kafka consistency.
8. Never use floating-point numbers for money.
9. Never store card data/CVV/UPI PIN.
10. Never trust role/user ID values sent by the frontend.
11. Enforce ownership server-side.
12. Sensitive admin operations require explicit permissions and audit logging.
13. Use global exception handling and consistent error DTOs.
14. Use structured logging and correlation IDs.
15. Use bounded retries and DLQs; never infinite retry loops.
16. Use timeouts and circuit breakers for synchronous service calls.
17. Do not invent APIs or fields not defined by the project contracts.
18. Preserve the existing architecture unless a requested change requires a migration.

## Code generation requirements

Before generating code:
- identify the service being modified
- list relevant entities/DTOs/events
- identify database ownership
- identify required Kafka topic
- identify authorization permission
- identify idempotency requirement
- identify failure scenarios

When generating code:
- provide complete compilable classes where requested
- include package names
- include imports
- avoid placeholder methods unless explicitly marked
- match Spring Boot 3 / Jakarta namespaces
- use constructor injection
- use configuration properties/environment variables for secrets
- include validation
- include tests for business-critical logic

## Payment-specific rules

Payment provider SDK code belongs only in the Payment Service adapter layer.

Business services must depend on `PaymentGateway`, not provider-specific classes.

Payment success must be verified server-side.

Webhook processing must be signature-verified and idempotent.

## RBAC-specific rules

Use permissions such as:

```text
USER_READ
DRIVER_SUSPEND
RIDE_READ
RIDE_CANCEL
PAYMENT_READ
PAYMENT_REFUND
PRICING_UPDATE
ANALYTICS_READ
AUDIT_READ
```

Do not scatter hard-coded role strings throughout the application.

## Admin-specific rules

Admin UI is a control plane. Every mutation:
- requires permission
- validates input
- records an audit log
- returns an explicit result

Do not provide a generic "change anything" endpoint.

## Frontend rules

- Use role/permission-aware route guards for UX only.
- Backend remains the source of truth for authorization.
- Use WebSockets for live ride/location updates.
- Never expose provider secret keys.
- Never trust frontend payment success state without backend verification.

## Testing expectations

For each feature include:
- unit tests
- controller/API tests where appropriate
- Kafka consumer tests
- idempotency tests
- authorization tests
- failure-path tests

For payment and RBAC, security and negative tests are mandatory.
