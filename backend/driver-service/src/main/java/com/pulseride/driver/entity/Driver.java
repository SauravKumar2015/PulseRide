package com.pulseride.driver.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.pulseride.driver.service.DriverStatus;
import com.pulseride.driver.service.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
public class Driver {

    @Id
    private String id;

    @Column(nullable = false, unique = true, updatable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    private Instant lastLocationUpdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private VehicleType vehicleType;

    private Instant createdAt;

    private Instant updatedAt;

    public Driver(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.status = DriverStatus.OFFLINE;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = DriverStatus.OFFLINE;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void updateLocation(
            BigDecimal latitude,
            BigDecimal longitude,
            Instant timestamp) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.lastLocationUpdate = timestamp;
    }
}