package com.pulseride.tracking.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.pulseride.tracking.dto.*;
import com.pulseride.tracking.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController @RequestMapping("/tracking") @PreAuthorize("hasRole('DRIVER')") @RequiredArgsConstructor
public class TrackingController {
 private final TrackingService service;
 @PostMapping("/location") public LocationResponse update(@Valid @RequestBody LocationRequest request, Authentication auth) { return service.update(auth.getName(), request); }
 @GetMapping("/location") public LocationResponse get(Authentication auth) { return service.get(auth.getName()); }
}
