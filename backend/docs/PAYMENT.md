# Payment Gateway Specification

## Goal

Add a provider-agnostic Payment Service so PulseRide can integrate Razorpay, Stripe, or another provider without coupling business logic to provider SDK classes.

## Core flow

```text
Ride Completed
     |
     v
PAYMENT_PENDING
     |
     v
Payment Service
     |
     +--> create payment order
     |
     v
Frontend checkout
     |
     v
Provider
     |
     +--> success callback
     +--> webhook
     |
     v
Payment Service
     |
     +--> verify signature
     +--> check amount/currency/order
     +--> idempotency check
     +--> persist result
     +--> publish Kafka event
```

## Payment states

```text
CREATED
PENDING
AUTHORIZED
CAPTURED
FAILED
CANCELLED
REFUND_PENDING
REFUNDED
PARTIALLY_REFUNDED
```

## Payment entity

Recommended fields:

```text
id
rideId
riderId
amountMinor
currency
status
provider
providerOrderId
providerPaymentId
idempotencyKey
failureCode
failureMessage
createdAt
updatedAt
```

Store money as integer minor units (for example paise), not floating-point numbers.

## Webhook rules

1. Verify provider signature using the official SDK/library.
2. Validate provider order ID.
3. Validate amount and currency.
4. Persist webhook/event ID for idempotency.
5. Never mark payment successful solely because the browser says it succeeded.
6. Return a fast HTTP response after safely recording/queueing the webhook.
7. Reconcile uncertain payments asynchronously.

## Idempotency

The same payment request must not create multiple business charges.

Use an idempotency key such as:

```text
rideId + paymentAttempt
```

or a client-generated UUID.

## Refunds

Refunds must be explicit and authorized.

Recommended flow:

```text
ADMIN_REFUND_REQUESTED
        |
        v
Payment Service
        |
        v
Provider refund API
        |
        v
REFUND_SUCCEEDED / REFUND_FAILED
```

## Provider abstraction

```java
public interface PaymentGateway {
    PaymentOrder createOrder(CreatePaymentCommand command);
    PaymentVerification verifyPayment(PaymentVerificationCommand command);
    RefundResult refund(RefundCommand command);
}
```

The rest of the service must depend on this interface, not directly on a provider SDK.

## Security

Never store:
- card number
- CVV
- UPI PIN
- raw authorization credentials
- provider API secrets

Use environment variables or a secret manager for provider credentials.
