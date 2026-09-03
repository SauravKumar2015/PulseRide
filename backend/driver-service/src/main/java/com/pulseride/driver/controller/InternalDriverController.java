package com.pulseride.driver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulseride.driver.dto.DriverResponse;
import com.pulseride.driver.service.DriverService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/drivers")
@RequiredArgsConstructor
public class InternalDriverController {

    private final DriverService service;

    @PostMapping("/profile")
    public DriverResponse createProfile(@RequestBody String userId) {
        return service.createProfile(userId.trim());
    }
}