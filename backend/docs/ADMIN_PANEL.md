# Admin Panel Specification

## Purpose

The admin panel is an operational control plane, not another copy of the rider UI.

## Dashboard

Display:

- active rides
- online drivers
- active riders
- rides per minute
- average matching time
- average ETA
- current surge zones
- payment success rate
- failed payments
- Kafka consumer lag
- DLQ count
- service health

## Live Map

Show:
- available drivers
- busy drivers
- active rides
- demand zones
- surge multipliers
- driver clusters

Do not expose unnecessary personal information.

## User management

Admin can:
- search users
- inspect status
- suspend/activate accounts
- inspect basic ride/payment history according to permission

## Driver management

Admin can:
- search drivers
- inspect verification/status
- activate/suspend
- inspect current availability
- inspect operational metrics

## Ride operations

Admin can:
- search by ride ID
- inspect lifecycle history
- inspect assigned driver
- inspect fare snapshot
- inspect cancellation reason

Avoid allowing admins to arbitrarily mutate ride state. Use explicit operational commands.

## Payment operations

Admin can:
- inspect payment attempts
- inspect provider references
- initiate authorized refunds
- inspect failed payments
- inspect webhook/reconciliation status

## Surge management

Admin can:
- inspect zone demand
- inspect current multiplier
- configure bounded surge policy parameters
- disable/enable a policy when authorized

Every manual pricing change must be audited.

## Audit log

Display:
- actor
- action
- resource
- timestamp
- reason
- correlation ID
- result

## Admin UI structure

```text
Dashboard
├── Live Operations
│   ├── Live Map
│   ├── Rides
│   └── Drivers
├── Users
├── Payments
├── Pricing & Surge
├── Analytics
├── System Health
│   ├── Kafka
│   ├── Redis
│   └── Services
└── Audit Logs
```
