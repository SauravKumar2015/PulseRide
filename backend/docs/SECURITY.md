# Security Requirements

## Authentication

Use Spring Security with short-lived access JWTs and a secure refresh-token strategy.

Never put secrets in source control.

## Authorization

Enforce permissions server-side at controller/service boundaries.

## Gateway

Gateway responsibilities:
- route requests
- reject malformed/expired JWTs
- rate-limit selected endpoints
- attach correlation/trace information

Do not make the Gateway the only authorization layer.

## Passwords

Use a strong adaptive password hash such as BCrypt/Argon2 through Spring Security.

Never log passwords.

## JWT

Validate:
- signature
- issuer
- expiry
- intended audience if configured

Do not put sensitive personal or financial data into JWT claims.

## Payment security

Use provider-hosted checkout/tokenization where possible. Never handle raw card data unless the compliance architecture explicitly requires it.

Verify webhooks cryptographically.

## Input validation

Validate:
- coordinates
- monetary values
- IDs
- pagination
- status transitions
- enum values

## API protection

Use:
- rate limiting
- request size limits
- timeouts
- CORS policy
- secure headers
- centralized error handling

## Logging

Never log:
- passwords
- JWTs
- API keys
- payment secrets
- card data
- CVV
- UPI PIN

Prefer structured logs with correlation IDs.
