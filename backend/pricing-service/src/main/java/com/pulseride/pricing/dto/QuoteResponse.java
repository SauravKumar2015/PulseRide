package com.pulseride.pricing.dto;
import java.math.BigDecimal;
public record QuoteResponse(BigDecimal baseFare, BigDecimal distanceFare, BigDecimal timeFare, BigDecimal serviceFee, BigDecimal total, String currency) {}
