# PulseRide API Contract

This document describes the HTTP API currently exposed by the services. Client-facing
requests use the gateway prefix `/pulse-ride`; the service-local path is the same path
after that prefix is removed. For example, `/pulse-ride/rides` is routed to
`POST /rides` in Ride Service.

## Conventions

- Protected endpoints require `Authorization: Bearer <accessToken>`.
- Public endpoints are registration, login, token refresh, and provider webhooks.
- Roles are the values used by the services: `USER`, `DRIVER`, and `ADMIN`. Pricing
	currently checks `PASSENGER` or `DRIVER`; this role mismatch must be resolved before
	a USER can request a quote.
- JSON validation failures return the service's standard 4xx error response. Clients
	should handle `400` validation errors, `401` missing/invalid tokens, `403` role
	failures, `404` missing resources, `409` invalid state or duplicate operations, and
	`500` unexpected failures.
- UUID path parameters are used for rides. User, driver, and payment identifiers are
	currently strings or numeric values depending on the service.
- Send a unique `Idempotency-Key` for payment order, verification, and refund requests.
	Ride creation and cancellation do not currently accept an idempotency key and should
	be protected from duplicate submissions by the client until the service adds one.

## Gateway Routes

The gateway currently routes `auth`, `users`, `rides`, `drivers`, `tracking`, `pricing`,
`payments`, and `admin`. Matching and notifications have controllers but no gateway
route yet; call them only through an explicitly secured internal route until routing is
added. Internal driver profile creation is service-to-service only.

## 1. Authentication

### Register

```http
POST /pulse-ride/auth/register
Content-Type: application/json
```

Request:

```json
{"name":"Alex Rider","email":"alex@example.com","password":"StrongPass1!","role":"USER"}
```

`name`, `email`, `password`, and `role` are required. Password length is 8-16 and
must contain upper and lower case letters, a number, and a special character. `role`
must be `USER` or `DRIVER`; clients cannot register as `ADMIN`.

Returns `201` with `UserResponse`.

### Login

```http
POST /pulse-ride/auth/login
```

Request: `LoginRequest { email, password }`. Returns `200` with `TokenResponse`.

### Refresh access token

```http
POST /pulse-ride/auth/refresh
```

Request: `RefreshTokenRequest { refreshToken }`. Returns `200` with `TokenResponse`.

### Logout

```http
POST /pulse-ride/auth/logout
Authorization: Bearer <accessToken>
```

Request: `LogoutRequest { refreshToken }`. Returns `204`. The authenticated user must
own the refresh token.

## 2. User Profile

### Read my profile

```http
GET /pulse-ride/users/me
Authorization: Bearer <accessToken>
```

Returns `200` with `UserProfileResponse` for the JWT subject.

### Update my profile

```http
PATCH /pulse-ride/users/me
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Request: `UpdateProfileRequest { displayName }`. `displayName` is optional, but when
provided must contain 2-120 characters. Returns `200` with the updated profile.

## 3. Rider Ride Journey

### Request a ride

```http
POST /pulse-ride/rides
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Request: `CreateRideRequest` with required `pickupLatitude`, `pickupLongitude`,
`dropoffLatitude`, and `dropoffLongitude`. Latitude is -90..90 and longitude is
-180..180. Returns `201` with `RideResponse`.

### Read a ride

```http
GET /pulse-ride/rides/{rideId}
Authorization: Bearer <accessToken>
```

Returns `200` with `RideResponse`. The service checks that the authenticated user may
access the ride.

### List my ride history

```http
GET /pulse-ride/rides/history
Authorization: Bearer <accessToken>
```

Returns `200` with `RideResponse[]`.

### Read ride status history

```http
GET /pulse-ride/rides/{rideId}/history
Authorization: Bearer <accessToken>
```

Returns `200` with `RideStatusHistoryResponse[]`.

### Cancel a ride

```http
POST /pulse-ride/rides/{rideId}/cancel
Authorization: Bearer <accessToken>
Content-Type: application/json
```

The body is optional. When present it is `CancelRideRequest { reason }`, where
`reason` is at most 500 characters. Returns `200` with the cancelled `RideResponse`.
The service decides whether the current ride state permits cancellation.

## 4. Driver Journey

All driver endpoints require the `DRIVER` role.

### Create or initialize my driver profile

```http
POST /pulse-ride/drivers/profile
Authorization: Bearer <accessToken>
```

Returns `200` with `DriverResponse`.

### Read my driver profile

```http
GET /pulse-ride/drivers/me
Authorization: Bearer <accessToken>
```

Returns `200` with `DriverResponse`.

### Update driver location

```http
POST /pulse-ride/drivers/location
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Request: `LocationRequest { latitude, longitude }`, with latitude -90..90 and
longitude -180..180. Returns `200` with `DriverResponse`.

### Change availability/status

```http
PATCH /pulse-ride/drivers/status
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Request: `StatusRequest { status }`, where `status` is a valid `DriverStatus` enum.
Returns `200` with `DriverResponse`.

### Change vehicle type

```http
PATCH /pulse-ride/drivers/vehicle-type
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Request: `VehicleTypeRequest { vehicleType }`; `vehicleType` is required. Returns
`200` with `DriverResponse`.

### List my assigned rides

```http
GET /pulse-ride/drivers/rides
Authorization: Bearer <accessToken>
```

Returns `200` with the current `List<Object>` placeholder. A typed driver ride DTO
and ride actions (accept, arrive, start, complete) are not implemented yet.

## 5. Pricing

### Calculate a quote

```http
POST /pulse-ride/pricing/quote
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Request: `QuoteRequest { distanceKm, durationMinutes }`; both values are required and
must be zero or greater. The controller currently authorizes `PASSENGER` or `DRIVER`,
so a normal `USER` token is rejected until the role policy is aligned. Returns `200`
with `QuoteResponse`.

### List surge zones

```http
GET /pulse-ride/pricing/surge-zones
Authorization: Bearer <accessToken>
```

Returns `200` with `SurgeZoneResponse[]`.

## 6. Live Tracking

### Publish driver location for a ride

```http
POST /pulse-ride/tracking/location
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Requires `DRIVER`. Request: `DriverLocationRequest { rideId?, latitude, longitude }`.
`rideId` is optional; latitude and longitude are required with the same coordinate
limits as above. Returns `200` with `DriverLocationResponse`.

### Read latest driver location

```http
GET /pulse-ride/tracking/driver/{driverId}/latest
Authorization: Bearer <accessToken>
```

Requires `DRIVER` or `ADMIN`. Returns `200` with `DriverLocationResponse`.

### Read latest location for a ride

```http
GET /pulse-ride/tracking/ride/{rideId}
Authorization: Bearer <accessToken>
```

Requires `USER`, `DRIVER`, or `ADMIN`. Returns `200` with `RideTrackingResponse`.

### Read ride location history

```http
GET /pulse-ride/tracking/ride/{rideId}/history
Authorization: Bearer <accessToken>
```

Requires `USER`, `DRIVER`, or `ADMIN`. Returns `200` with `LocationHistoryResponse[]`.
There is no WebSocket controller in the current code; clients should poll this REST
API until a streaming endpoint is implemented.

## 7. Matching

These endpoints exist in Matching Service but are not routed by the gateway.

### Match a ride

```http
POST /matching/rides
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Requires `USER` or `ADMIN`. Request: `MatchRideRequest { rideId, riderId,
pickupLatitude, pickupLongitude }`, with required IDs and valid coordinates. Returns
`200` with `MatchResponse`.

### Read a match

```http
GET /matching/rides/{rideId}
Authorization: Bearer <accessToken>
```

Requires `USER`, `DRIVER`, or `ADMIN`; returns `MatchResponse`.

### Cancel a match

```http
DELETE /matching/rides/{rideId}
Authorization: Bearer <accessToken>
```

Requires `USER` or `ADMIN`; returns `MatchResponse`.

## 8. Payments

### Create a payment order

```http
POST /pulse-ride/payments/orders
Authorization: Bearer <accessToken>
Idempotency-Key: <unique-key>
Content-Type: application/json
```

Requires `PASSENGER` (currently inconsistent with the public `USER` registration
role). Request: `OrderRequest { amount, currency }`; `amount` is required and must be
positive, and `currency` is required and non-blank. Returns `200` with
`PaymentResponse { id, owner, amount, currency, state }`.

### Read a payment

```http
GET /pulse-ride/payments/{paymentId}
Authorization: Bearer <accessToken>
```

Returns `200` with `PaymentResponse`, subject to ownership checks in Payment Service.

### Verify a payment

```http
POST /pulse-ride/payments/{paymentId}/verify
Authorization: Bearer <accessToken>
Idempotency-Key: <unique-key>
```

Returns `200` with `PaymentResponse`.

### Request a refund

```http
POST /pulse-ride/payments/{paymentId}/refund
Authorization: Bearer <accessToken>
Idempotency-Key: <unique-key>
```

Returns `200` with `PaymentResponse`. The service must enforce that the caller owns
the payment or has an administrative refund permission.

### Receive a provider webhook

```http
POST /pulse-ride/payments/webhook/{provider}
X-Provider-Signature: <signature>
Content-Type: application/json
```

The body is the raw provider payload. Returns `202`. This endpoint is public at the
HTTP authentication layer but must verify the provider signature, reject replayed
events, and process each provider event idempotently.

## 9. Notifications

The notification controller is not currently routed by the gateway.

```http
GET /notifications/me
Authorization: Bearer <accessToken>
```

Returns `200` with `Notification[]` for the authenticated user. There are no current
read/unread, acknowledge, delete, or WebSocket notification endpoints.

## 10. Administration

Every admin endpoint requires the `ADMIN` role.

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

`PATCH` status requests use `StatusRequest { status }` with a required non-blank
status. The surge-zone patch currently accepts an untyped JSON object. List endpoints
currently return empty `Map<String,Object>[]` placeholders; refund and surge-zone
updates currently return `204` with no response body. Audit logs return `Audit[]` and
metrics return a map of counters.

## 11. Internal Endpoint

This endpoint is not a public client API and must be protected by service identity,
network policy, and request authentication:

```http
POST /internal/drivers/profile
Content-Type: text/plain
```

Body: the user ID string. Returns `DriverResponse`.

## Client Use Cases Not Yet Covered

The current code does not expose endpoints for password reset/change, email or phone
verification, saved places, fare estimate by address, scheduled rides, multiple stops,
ride acceptance/arrival/start/completion, driver earnings, ratings/tips, support or
ride dispute, payment method management, notification acknowledgement, pagination or
filters on admin lists, or a gateway route for matching and notifications. These are
product requirements to implement before claiming those user journeys are supported;
they are intentionally not documented as callable APIs above.

## Required Product APIs (Not Implemented Yet)

The following list is the target contract needed to cover the normal rider, driver, and
admin journeys. These routes are requirements only; they must not be called until their
owning service, DTOs, authorization checks, persistence, and tests are implemented.

### Account and identity

```http
POST  /pulse-ride/auth/password/forgot
POST  /pulse-ride/auth/password/reset
POST  /pulse-ride/auth/password/change
POST  /pulse-ride/auth/verify-email
POST  /pulse-ride/auth/verify-phone
DELETE /pulse-ride/users/me
```

### Rider trip planning and ride lifecycle

```http
GET   /pulse-ride/places/saved
POST  /pulse-ride/places/saved
PATCH /pulse-ride/places/saved/{placeId}
DELETE /pulse-ride/places/saved/{placeId}
POST  /pulse-ride/pricing/route-quote
POST  /pulse-ride/rides/{rideId}/stops
DELETE /pulse-ride/rides/{rideId}/stops/{stopId}
POST  /pulse-ride/rides/{rideId}/schedule
POST  /pulse-ride/rides/{rideId}/accept
POST  /pulse-ride/rides/{rideId}/arrive
POST  /pulse-ride/rides/{rideId}/start
POST  /pulse-ride/rides/{rideId}/complete
```

Riders need explicit endpoints for scheduled rides, extra stops, and the complete
state-machine transition history. Transition endpoints must validate the current state,
the actor, and idempotency before publishing lifecycle events.

### Driver operations

```http
GET   /pulse-ride/drivers/me/earnings
GET   /pulse-ride/drivers/me/earnings/{earningId}
GET   /pulse-ride/drivers/me/rides?status=&page=&size=
POST  /pulse-ride/drivers/rides/{rideId}/accept
POST  /pulse-ride/drivers/rides/{rideId}/arrive
POST  /pulse-ride/drivers/rides/{rideId}/start
POST  /pulse-ride/drivers/rides/{rideId}/complete
```

The current `GET /drivers/rides` placeholder should become a typed, paginated
response. Driver transitions must be limited to the assigned driver and the allowed
ride state.

### Matching and live communication

```http
POST /pulse-ride/matching/rides
GET  /pulse-ride/matching/rides/{rideId}
DELETE /pulse-ride/matching/rides/{rideId}
GET  /pulse-ride/notifications/me
PATCH /pulse-ride/notifications/{notificationId}/read
POST /pulse-ride/notifications/read-all
WS   /pulse-ride/ws/tracking
WS   /pulse-ride/ws/notifications
```

The existing matching and notification controllers need gateway routes, and WebSocket
authentication must bind subscriptions to the authenticated user or assigned driver.

### Payment methods, receipts, and disputes

```http
GET  /pulse-ride/payment-methods
POST /pulse-ride/payment-methods
DELETE /pulse-ride/payment-methods/{methodId}
GET  /pulse-ride/payments/{paymentId}/receipt
POST /pulse-ride/payments/{paymentId}/dispute
GET  /pulse-ride/payments/{paymentId}/refunds
```

Payment method tokens, not card numbers or CVV, must be accepted and stored. Receipt,
refund, and dispute APIs must enforce ownership or an explicit admin permission.

### Rider feedback and support

```http
POST /pulse-ride/rides/{rideId}/rating
PATCH /pulse-ride/rides/{rideId}/rating
POST /pulse-ride/support/tickets
GET  /pulse-ride/support/tickets
GET  /pulse-ride/support/tickets/{ticketId}
POST /pulse-ride/support/tickets/{ticketId}/messages
```

Ratings must be limited to completed rides and one rating per eligible rider/driver.
Support endpoints need ownership checks and a defined attachment policy.

### Admin operations needed for a usable dashboard

```http
GET   /pulse-ride/admin/users?page=&size=&status=&role=
GET   /pulse-ride/admin/users/{id}
GET   /pulse-ride/admin/drivers?page=&size=&status=
GET   /pulse-ride/admin/drivers/{id}
GET   /pulse-ride/admin/rides?page=&size=&status=&from=&to=
GET   /pulse-ride/admin/rides/{rideId}
GET   /pulse-ride/admin/payments?page=&size=&state=
GET   /pulse-ride/admin/payments/{paymentId}
POST  /pulse-ride/admin/payments/{paymentId}/refund
POST  /pulse-ride/admin/drivers/{id}/verify
GET   /pulse-ride/admin/support/tickets
PATCH /pulse-ride/admin/support/tickets/{ticketId}
```

All admin list APIs need stable pagination, filtering, and audit records. Admin APIs
must call service-owned APIs rather than writing another service's database directly.
