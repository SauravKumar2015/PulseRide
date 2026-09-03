package com.pulseride.driver.exception;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(String userId) {
        super("Driver profile not found for authenticated user " + userId);
    }
}