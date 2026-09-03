package com.pulseride.ride.controller;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.pulseride.ride.dto.RideRequest;
import com.pulseride.ride.dto.RideResponse;
import com.pulseride.ride.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/rides")
@PreAuthorize("hasRole('PASSENGER')")
@RequiredArgsConstructor
public class RideController {
    private final RideService service;
    @PostMapping public ResponseEntity<RideResponse> create(@Valid @RequestBody RideRequest request, Authentication auth) { return ResponseEntity.status(201).body(service.create(auth.getName(), request)); }
    @GetMapping("/history") public List<RideResponse> history(Authentication auth) { return service.history(auth.getName()); }
    @GetMapping("/{rideId}") public RideResponse get(@PathVariable String rideId, Authentication auth) { return service.get(auth.getName(), rideId); }
    @PostMapping("/{rideId}/cancel") public RideResponse cancel(@PathVariable String rideId, Authentication auth) { return service.cancel(auth.getName(), rideId); }
}
