# Kafka Event Contract

## Event envelope

Every event should use a common envelope:

```json
{
  "eventId": "UUID",
  "eventType": "RIDE_COMPLETED",
  "version": 1,
  "occurredAt": "2026-08-13T12:30:00Z",
  "correlationId": "UUID",
  "producer": "ride-service",
  "aggregateType": "RIDE",
  "aggregateId": "ride-123",
  "payload": {}
}
```

## Topics

```text
ride-events
driver-events
location-events
pricing-events
payment-events
notification-events
audit-events
billing-dlq-failures
```

A more granular topic strategy may be used later if traffic characteristics justify it.

## Important events

### RIDE_REQUESTED

```json
{
  "rideId": "ride-123",
  "riderId": "user-10",
  "pickup": {"lat": 23.21, "lng": 72.63},
  "dropoff": {"lat": 23.25, "lng": 72.66}
}
```

### DRIVER_ASSIGNED

```json
{
  "rideId": "ride-123",
  "driverId": "driver-77",
  "score": 91.4,
  "estimatedArrivalSeconds": 280
}
```

### PAYMENT_SUCCEEDED

```json
{
  "paymentId": "pay-10",
  "rideId": "ride-123",
  "amountMinor": 18400,
  "currency": "INR",
  "providerPaymentId": "provider-reference"
}
```

## Partitioning

Use a stable key to preserve ordering where required:

- ride lifecycle: `rideId`
- driver lifecycle: `driverId`
- payment lifecycle: `paymentId` or `rideId`

## Consumer requirements

Consumers must be:
- idempotent
- retryable
- observable
- safe against duplicate events
- safe against out-of-order events where possible

Failed processing should eventually go to a DLQ with enough metadata to diagnose and replay.
