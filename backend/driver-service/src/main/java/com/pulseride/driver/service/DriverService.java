package com.pulseride.driver.service;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pulseride.driver.dto.DriverResponse;
import com.pulseride.driver.dto.LocationRequest;
import com.pulseride.driver.entity.Driver;
import com.pulseride.driver.exception.DriverNotFoundException;
import com.pulseride.driver.repository.DriverRepository;

@Service
public class DriverService {
    private static final Logger log = LoggerFactory.getLogger(DriverService.class);
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public DriverResponse updateLocation(String userId, LocationRequest request) {
        Driver driver = findDriver(userId);
        log.info("Updating location for driver userId={}", userId);
        driver.updateLocation(request.latitude(), request.longitude(), Instant.now());
        return response(driverRepository.save(driver));
    }

    public DriverResponse updateStatus(String userId, DriverStatus next) {
        Driver driver = findDriver(userId);
        log.info("Updating status for driver userId={} status={}", userId, next);
        if (!isAllowed(driver.getStatus(), next)) {
            throw new IllegalStateException("Invalid driver status transition");
        }
        driver.setStatus(next);
        return response(driverRepository.save(driver));
    }

    public DriverResponse get(String userId) {
        return response(findDriver(userId));
    }

    public Driver createProfile(String userId) {
        return driverRepository.findByUserId(userId)
            .orElseGet(() -> driverRepository.save(new Driver(userId, userId)));
    }

    public List<Object> getRides(String userId) {
        findDriver(userId);
        return List.of();
    }

    private boolean isAllowed(DriverStatus current, DriverStatus next) {
        return current == next || (current == DriverStatus.OFFLINE && next == DriverStatus.AVAILABLE)
                || (current == DriverStatus.AVAILABLE && (next == DriverStatus.BUSY || next == DriverStatus.OFFLINE))
                || (current == DriverStatus.BUSY && next == DriverStatus.AVAILABLE);
    }

    private Driver findDriver(String userId) {
        return driverRepository.findByUserId(userId).orElseThrow(() -> new DriverNotFoundException(userId));
    }

    private DriverResponse response(Driver driver) {
        return new DriverResponse(driver.getId(), driver.getUserId(), driver.getStatus(), driver.getLatitude(),
                driver.getLongitude(), driver.getLastLocationUpdate(), driver.getCreatedAt(), driver.getUpdatedAt());
    }
}
