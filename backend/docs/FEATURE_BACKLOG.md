# PulseRide Feature Backlog

## P0 — Must have

- Authentication
- RBAC
- Ride lifecycle
- Real-time driver tracking
- Redis GEO matching
- Kafka ride events
- Distributed driver lock
- Dynamic surge
- Payment gateway
- Payment webhook verification
- Payment idempotency
- Admin dashboard
- Audit logging
- DLQ

## P1 — Strong enhancements

- Multi-factor driver scoring
- ETA estimation
- Spatial surge heatmap
- Notification service
- Transactional outbox
- Circuit breaker
- Rate limiting
- Prometheus/Grafana
- OpenTelemetry tracing

## P2 — Differentiators

- Predictive demand
- Driver rebalancing
- Cancellation prediction
- Fraud detection
- Fairness-aware matching
- Ride pooling
- Chaos testing

## Avoid for now

Do not add features merely because they are common CRUD requirements, such as:
- complex social profiles
- unnecessary chat systems
- excessive coupon rules
- dozens of microservices with no independent scaling need

The project should demonstrate distributed-systems engineering rather than maximum feature count.
