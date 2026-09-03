package com.pulseride.admin.dto;
import jakarta.validation.constraints.NotBlank;
public record StatusRequest(@NotBlank String status) {}
