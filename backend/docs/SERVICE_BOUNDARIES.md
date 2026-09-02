# Service Boundaries

## Auth/User Service

Owns:
- user identity
- password hashes
- roles
- permissions
- driver/rider profile metadata
- account status

Does not own:
- ride state
- payment transactions
- live GPS positions

## Tracking Service

Owns:
- WebSocket driver connections
- latest driver location
- driver availability
- geospatial indexes

Publishes:
- `DRIVER_LOCATION_UPDATED`
- `DRIVER_STATUS_CHANGED`

## Ride Service

Owns:
- ride request
- lifecycle/state transitions
- pickup/dropoff
- assigned driver reference
- cancellation

Publishes:
- `RIDE_REQUESTED`
- `RIDE_STATUS_CHANGED`
- `RIDE_COMPLETED`
- `RIDE_CANCELLED`

## Matching Service

Owns:
- candidate discovery
- driver scoring
- assignment workflow
- assignment lock/idempotency

Publishes:
- `DRIVER_ASSIGNED`
- `MATCHING_FAILED`

## Pricing Service

Owns:
- fare rules
- surge zones
- pricing snapshots

Publishes:
- `FARE_CALCULATED`
- `SURGE_UPDATED`

## Payment Service

Owns:
- payment orders
- payment attempts
- provider references
- verification result
- refunds
- webhook event records

Publishes:
- `PAYMENT_CREATED`
- `PAYMENT_SUCCEEDED`
- `PAYMENT_FAILED`
- `REFUND_SUCCEEDED`
- `REFUND_FAILED`

Never store raw card numbers, CVV, UPI PIN, or provider secrets in the database.

## Notification Service

Consumes domain events and sends WebSocket/in-app notifications.

## Admin

Admin operations should call authorized service APIs. Avoid direct writes to another service's database.
