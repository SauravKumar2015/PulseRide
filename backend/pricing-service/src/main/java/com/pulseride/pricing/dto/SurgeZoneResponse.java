package com.pulseride.pricing.dto;
import java.math.BigDecimal;
public record SurgeZoneResponse(String id, String name, BigDecimal multiplier, boolean active) {}
