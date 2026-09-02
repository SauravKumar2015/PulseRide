# Data Model

## PostgreSQL ownership

### Auth/User Service

```text
users
roles
permissions
user_roles
role_permissions
```

### Ride Service

```text
rides
ride_status_history
```

### Payment Service

```text
payments
payment_attempts
refunds
payment_webhook_events
outbox_events
```

### Admin/Audit

```text
audit_logs
```

## Example relationships

```text
users 1---N rides
users 1---N payments
rides 1---N payments
payments 1---N payment_attempts
payments 1---N refunds
users N---N roles
roles N---N permissions
```

## Redis structures

### Driver latest location

```text
GEOADD drivers:available <longitude> <latitude> <driverId>
```

### Driver metadata

```text
driver:{driverId}
```

Suggested fields:

```text
status
vehicleType
rating
lastSeen
```

### Matching lock

```text
lock:driver:{driverId}
```

Use a safe distributed lock implementation with TTL and ownership semantics.

### Idempotency

```text
idempotency:{consumer}:{eventId}
```

Use an expiry appropriate to the business requirement.

## Money

Use `BIGINT`/Java `long` minor units:

```text
₹184.00 -> 18400 paise
```

Never use `double` for financial amounts.
