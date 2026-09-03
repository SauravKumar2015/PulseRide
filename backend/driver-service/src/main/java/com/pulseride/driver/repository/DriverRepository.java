package com.pulseride.driver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulseride.driver.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, String> {
    Optional<Driver> findByUserId(String userId);
}