# Failure Handling

## Kafka consumer failure

```text
consume
  |
process
  |
 +-- success -> commit
 |
 +-- transient failure -> retry
 |
 +-- repeated failure -> DLQ
```

Record:
- event ID
- topic
- partition
- offset
- exception class
- sanitized error message
- retry count
- timestamp

## Duplicate event

Check event ID/idempotency record before performing a non-idempotent action.

## Payment timeout

Do not immediately mark a payment as failed when provider status is uncertain.

Use:

```text
PENDING
  -> reconciliation
  -> SUCCESS / FAILED
```

## Redis failure

Tracking may degrade gracefully. Do not invent driver positions. Mark stale positions unavailable after a TTL.

## Matching service failure

Kafka retains ride events. When consumers recover, unprocessed events can be resumed.

## PostgreSQL failure

Fail closed for transactional operations. Do not acknowledge events whose required DB transaction did not succeed.

## Service-to-service timeout

Use bounded timeouts and circuit breakers. Do not retry indefinitely.

## Stale GPS

If:

```text
now - lastSeen > threshold
```

the driver should no longer be considered available for new matching.

## Reconciliation

Create scheduled jobs for:
- uncertain payments
- stale rides
- orphaned assignments
- payment/provider mismatch
- DLQ review
