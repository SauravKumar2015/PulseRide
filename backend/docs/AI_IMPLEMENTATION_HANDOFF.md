# PulseRide Implementation Handoff

This document is the current implementation context for AI coding agents working on PulseRide. Read it before generating or changing code.

## Project

PulseRide is a distributed, event-driven ride-hailing platform. The backend is organized as independently deployable Spring Boot services with service-owned data.

## Completed Services

### Auth Service

Location: `backend/auth-service`

Implemented capabilities:

- User registration at `POST /auth/register`.
- User login at `POST /auth/login`.
- Access-token and refresh-token issuance.
- Refresh-token rotation at `POST /auth/refresh`; the previous token is revoked before replacement tokens are issued.
- Authenticated logout at `POST /auth/logout`; the supplied refresh token must belong to the authenticated user.
- Password hashing with BCrypt. Plain-text passwords are never returned in user responses.
- JWT access tokens signed with HMAC HS256.
- JWT claims include issuer, subject/user ID, issued-at time, expiry, email, and role.
- JWT validation with issuer checking.
- Role normalization to uppercase and email normalization to trimmed lowercase during registration/login.
- Duplicate-email handling, invalid-credentials handling, invalid-refresh-token handling, and centralized error responses.
- Driver-profile creation is requested through the driver client when a newly registered user has the `DRIVER` role.
- Unit tests cover password hashing, generic login failures, refresh-token rotation, and logout ownership.

### Driver Service

Location: `backend/driver-service`

Implemented capabilities:

- Driver profile creation or lookup at `POST /drivers/profile`.
- Current driver profile at `GET /drivers/me`.
- Driver location update at `POST /drivers/location`.
- Driver availability update at `PATCH /drivers/status`.
- Vehicle type update at `PATCH /drivers/vehicle-type`.
- Driver ride endpoint exists at `GET /drivers/rides`, but currently returns an empty list until ride-service integration is implemented.
- Internal profile creation endpoint at `POST /internal/drivers/profile`, used by auth-service.
- Driver endpoints require the `DRIVER` role with `@PreAuthorize`.
- JWTs are validated as OAuth2 resource-server tokens using the shared issuer and secret configuration.
- Location validation restricts latitude to `-90..90` and longitude to `-180..180`.
- Driver status transitions are enforced server-side:
  - `OFFLINE -> AVAILABLE`
  - `AVAILABLE -> BUSY` or `OFFLINE`
  - `BUSY -> AVAILABLE`
  - Keeping the current status is allowed.
- Driver data is persisted with JPA/PostgreSQL.
- Location values use `BigDecimal`, not floating-point types.
- Unit tests cover invalid transitions, authenticated-driver ownership, location updates, and expected availability transitions.

## Current API Shapes

### Auth requests and responses

Registration request:

```json
{
  "name": "Sam Rider",
  "email": "sam@example.com",
  "password": "Password1!",
  "role": "USER"
}
```

Public registration roles currently accepted: `USER` and `DRIVER`.

Login request:

```json
{
  "email": "sam@example.com",
  "password": "Password1!"
}
```

Token response:

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

Refresh and logout requests contain:

```json
{
  "refreshToken": "..."
}
```

### Driver requests and response

Location request:

```json
{
  "latitude": 25.5941,
  "longitude": 85.1376
}
```

Status request contains a `status` value. Vehicle type request contains a `vehicleType` value.

Driver response fields:

```text
driverId, userId, status, latitude, longitude,
lastLocationUpdate, createdAt, updatedAt, vehicleType
```

The authenticated user ID must come from the JWT subject (`Authentication.getName()`), never from a client-supplied user ID.

## Technology Stack

- Java 17
- Spring Boot 3.5.5 in auth-service and driver-service
- Spring Web MVC / REST controllers
- Spring Data JPA and Hibernate
- PostgreSQL for service-owned transactional data
- Spring Security
- Spring Security OAuth2 JOSE
- Spring OAuth2 Resource Server for driver JWT verification
- Nimbus JWT encoder/decoder
- HMAC HS256 JWT signing
- BCrypt password hashing
- Jakarta Bean Validation
- Lombok
- Maven
- JUnit 5, Mockito, AssertJ, and Spring Security Test
- Docker Compose for local infrastructure
- Planned platform components: Spring Cloud Gateway, Kafka for domain events, Redis Stack for geospatial/low-latency state, and React/Vite/Tailwind for the frontend

## Configuration

JWT configuration uses environment variables with local development defaults:

```yaml
JWT_SECRET                 # shared signing secret; replace the local default outside development
JWT_ISSUER                 # defaults to pulseride-auth
JWT_ACCESS_EXPIRATION      # seconds; defaults to 900
JWT_REFRESH_EXPIRATION     # seconds; defaults to 2592000
```

Never commit production secrets. Auth-service and driver-service must use compatible JWT secret and issuer values.

## Architecture Rules For AI Agents

- Identify the owning service before changing a model, endpoint, or database table.
- Never access another service's database directly.
- Use DTOs at REST boundaries and validate every external request.
- Use constructor injection and Java 17/Jakarta namespaces.
- Do not trust role, user ID, price, or payment-success values sent by the frontend.
- Derive authenticated identity from the JWT and enforce ownership on the server.
- Keep provider-specific payment code inside payment-service adapters.
- Do not invent endpoint fields or event fields; consult the API and data-model contracts first.
- Use `BigDecimal` or integer minor units for money.
- Add authorization and failure-path tests for security-sensitive changes.
- Use the project's global error format and structured logging patterns.
- Preserve service boundaries and existing public APIs unless a migration is explicitly required.

## Recommended Next Work

1. Complete driver ride retrieval by defining the ride-service contract and replacing `List<Object>` with a typed DTO.
2. Add controller/API tests for auth and driver endpoints, including validation and authorization failures.
3. Add integration tests with PostgreSQL/Testcontainers where persistence behavior matters.
4. Harden service-to-service communication: authenticate the internal driver endpoint and define a consistent internal API error contract.
5. Add API gateway routing and centralized authentication behavior.
6. Implement ride-service lifecycle and connect driver availability to ride matching.
7. Introduce Kafka domain events with versioned immutable envelopes, transactional outbox, idempotent consumers, bounded retries, and DLQs.
8. Introduce Redis geospatial driver availability and low-latency location state when the matching design is ready.
9. Add refresh-token cleanup, token revocation strategy, rate limiting, audit logging, and production secret management.
10. Add frontend role-aware route guards for user experience while keeping backend authorization authoritative.

## Prompt For Future AI Agents

Use this context when working on PulseRide:

```text
You are modifying the PulseRide ride-hailing platform. First identify the owning service, existing DTOs/entities/repositories, API contract, authorization rule, database ownership, event or idempotency requirements, and failure scenarios. Preserve the existing Spring Boot 3.5.5, Java 17, PostgreSQL/JPA, Spring Security, JWT, validation, Lombok, and Maven conventions. Use constructor injection, Jakarta namespaces, DTOs, server-side ownership checks, and focused tests. Do not access another service's database, trust frontend identity or payment state, invent API fields, or expose secrets. Keep changes minimal and compilable. Check the relevant service tests after editing.
```

## Source References

- `backend/docs/ARCHITECTURE.md`
- `backend/docs/API_CONTRACTS.md`
- `backend/docs/DATA_MODEL.md`
- `backend/docs/SERVICE_BOUNDARIES.md`
- `backend/docs/SECURITY.md`
- `backend/docs/AI_CODING_CONTEXT.md`
