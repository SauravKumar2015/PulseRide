# RBAC Specification

## Roles

### SUPER_ADMIN
Full platform administration.

### ADMIN
Operational administration: users, drivers, rides, pricing, payments, reports.

### SUPPORT_AGENT
Customer support operations with restricted mutation rights.

### DRIVER
Driver-facing operations and own profile/ride actions.

### RIDER
Rider-facing operations and own profile/ride/payment actions.

## Permission naming

Use explicit permissions instead of checking role names throughout business code.

Examples:

```text
USER_READ
USER_UPDATE
DRIVER_READ
DRIVER_SUSPEND
RIDE_READ
RIDE_CANCEL
PRICING_READ
PRICING_UPDATE
PAYMENT_READ
PAYMENT_REFUND
ANALYTICS_READ
ADMIN_AUDIT_READ
```

## Authorization model

```text
JWT
 |
 +-- subject/userId
 +-- roles
 +-- permissions
 +-- issuer
 +-- issuedAt
 +-- expiry
```

Prefer permission-based method authorization:

```java
@PreAuthorize("hasAuthority('PAYMENT_REFUND')")
```

Do not rely only on:

```java
@PreAuthorize("hasRole('ADMIN')")
```

for every endpoint.

## Ownership checks

A rider can read only their own rides/payments.

A driver can read only rides assigned to them.

An admin can access operational data according to permission.

Ownership must be checked server-side.

## Admin audit

Every sensitive administrative mutation should generate an audit record:

```text
actorUserId
action
resourceType
resourceId
beforeValue
afterValue
reason
timestamp
ipAddress
correlationId
```

Do not log passwords, tokens, payment secrets, or sensitive provider payloads.
