package com.pulseride.driver.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulseride.driver.dto.DriverResponse;
import com.pulseride.driver.dto.LocationRequest;
import com.pulseride.driver.dto.StatusRequest;
import com.pulseride.driver.dto.VehicleTypeRequest;
import com.pulseride.driver.service.DriverService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/drivers")
@PreAuthorize("hasRole('DRIVER')")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;

    @PostMapping("/profile")
    public DriverResponse createProfile(Authentication auth) {
        return service.createProfile(auth.getName());
    }

    @PostMapping("/location")
    public DriverResponse location(
            @Valid @RequestBody LocationRequest request,
            Authentication auth) {
        return service.updateLocation(auth.getName(), request);
    }

    @PatchMapping("/status")
    public DriverResponse status(
            @Valid @RequestBody StatusRequest request,
            Authentication auth) {
        return service.updateStatus(auth.getName(), request.status());
    }

    @GetMapping("/me")
    public DriverResponse me(Authentication auth) {
        return service.get(auth.getName());
    }

    @GetMapping("/rides")
    public List<Object> rides(Authentication auth) {
        return service.getRides(auth.getName());
    }

    @PatchMapping("/vehicle-type")
    public DriverResponse vehicleType(
            @Valid @RequestBody VehicleTypeRequest request,
            Authentication auth) {
        return service.setVehicleType(auth.getName(), request);
    }

    // @PostMapping("/internal/profile")
    // public DriverResponse createInternalProfile(@RequestBody String userId) {
    //     return service.createProfile(userId);
    // }
}