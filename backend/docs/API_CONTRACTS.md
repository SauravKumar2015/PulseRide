# pulse-ride Contract Outline

All protected pulse-rides require a valid Bearer JWT unless explicitly documented as public.

## Auth

```http
POST /pulse-ride/auth/register
POST /pulse-ride/auth/login
POST /pulse-ride/auth/refresh
POST /pulse-ride/auth/logout
```

## Rider

```http
POST /pulse-ride/rides
GET  /pulse-ride/rides/{rideId}
POST /pulse-ride/rides/{rideId}/cancel
GET  /pulse-ride/rides/history
```

## Driver

```http
POST /pulse-ride/drivers/location
PATCH /pulse-ride/drivers/status
GET /pulse-ride/drivers/me
GET /pulse-ride/drivers/rides
```

Location streaming should normally use WebSocket rather than repeatedly calling REST.

## Pricing

```http
POST /pulse-ride/pricing/quote
GET  /pulse-ride/pricing/surge-zones
```

## Payments

```http
POST /pulse-ride/payments/orders
GET  /pulse-ride/payments/{paymentId}
POST /pulse-ride/payments/{paymentId}/verify
POST /pulse-ride/payments/webhook/{provider}
POST /pulse-ride/payments/{paymentId}/refund
```

Webhook endpoints are provider-facing and must not trust JWT authentication as their primary security mechanism.

## Admin

```http
GET   /pulse-ride/admin/users
PATCH /pulse-ride/admin/users/{id}/status
GET   /pulse-ride/admin/drivers
PATCH /pulse-ride/admin/drivers/{id}/status
GET   /pulse-ride/admin/rides
GET   /pulse-ride/admin/payments
POST  /pulse-ride/admin/payments/{id}/refund
GET   /pulse-ride/admin/surge-zones
PATCH /pulse-ride/admin/surge-zones/{id}
GET   /pulse-ride/admin/audit-logs
GET   /pulse-ride/admin/metrics
```

Every endpoint must document:
- authorization permission
- request DTO
- response DTO
- validation
- error responses
- idempotency requirements
