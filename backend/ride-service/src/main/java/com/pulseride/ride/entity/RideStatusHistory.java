package com.pulseride.ride.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ride_status_history")
@Getter
@Setter
@NoArgsConstructor
public class RideStatusHistory {

    @Id
    @Column(name = "history_id", nullable = false, updatable = false)
    private UUID historyId;

    @Column(name = "ride_id", nullable = false, updatable = false)
    private UUID rideId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RideStatus status;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    /**
     * Optional identifier of the user/driver/admin
     * responsible for the status change.
     */
    @Column(name = "changed_by")
    private UUID changedBy;

    @Column(name = "reason", length = 500)
    private String reason;

    @PrePersist
    protected void onCreate() {

        if (historyId == null) {
            historyId = UUID.randomUUID();
        }

        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }
}