# PulseRide V2 Architecture

## 1. High-level architecture

```text
React Web App / Admin Panel
        |
        v
Spring Cloud Gateway
        |
  +-----+----------+-------------+-------------+
  |                |             |             |
  v                v             v             v
Auth/User      Tracking       Ride/Match     Payment
Service       Service        Service        Service
  |                |             |             |
PostgreSQL       Redis          Redis        PostgreSQL
                     \             /
                      \           /
                         Kafka
                           |
            +--------------+--------------+
            |              |              |
            v              v              v
       Pricing       Notification     Analytics/Risk
```

## 2. Request vs event communication

Use synchronous REST when the caller needs an immediate decision/result, such as authentication or obtaining a payment order.

Use Kafka when work can be asynchronous, durable, replayable, or fan-out to multiple consumers.

Examples:

- `POST /rides` -> Ride Service creates ride -> publishes `RIDE_REQUESTED`
- Matching Service consumes `RIDE_REQUESTED`
- Matching Service publishes `DRIVER_ASSIGNED`
- Pricing consumes relevant ride/location events
- Notification consumes lifecycle events
- Payment consumes `RIDE_COMPLETED`

## 3. Ride lifecycle

```text
REQUESTED
  -> SEARCHING_DRIVER
  -> DRIVER_ASSIGNED
  -> DRIVER_ARRIVING
  -> DRIVER_ARRIVED
  -> RIDE_STARTED
  -> RIDE_COMPLETED
  -> PAYMENT_PENDING
  -> COMPLETED
```

Cancellation is allowed only from explicitly permitted states.

## 4. Payment lifecycle

```text
RIDE_COMPLETED
      |
      v
PAYMENT_PENDING
      |
      v
Payment Service creates provider order
      |
      v
Frontend opens provider checkout
      |
      v
Provider callback/webhook
      |
      v
Signature verification
      |
      v
PAYMENT_SUCCESS / PAYMENT_FAILED
      |
      v
Kafka event
```

## 5. Security boundaries

The Gateway performs coarse JWT validation and routing. Each service must still enforce authorization for sensitive operations. Never trust a frontend-provided role or user ID.

## 6. Consistency

Do not use distributed database transactions across services. Prefer:

- local DB transactions
- transactional outbox
- Kafka events
- idempotent consumers
- retries
- DLQs
- reconciliation jobs

## 7. Scaling

Tracking and Kafka consumers are expected to scale independently. Kafka partitions should be selected using stable keys such as `driverId` or `rideId` depending on the ordering requirement.

## 8. Observability

Every request/event should carry a correlation/trace ID. Capture:

- API latency
- Kafka consumer lag
- matching latency
- GPS events/sec
- payment success/failure
- WebSocket connections
- Redis latency
- DLQ volume
- service error rate
