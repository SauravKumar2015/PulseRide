package com.pulseride.matching.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "match_records",
        indexes = {

                @Index(
                        name = "idx_match_ride",
                        columnList = "ride_id"
                ),

                @Index(
                        name = "idx_match_driver",
                        columnList = "driver_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "ride_id",
            nullable = false,
            unique = true
    )
    private UUID rideId;


    @Column(
            name = "rider_id",
            nullable = false
    )
    private Long riderId;


    @Column(name = "driver_id")
    private Long driverId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;


    @Column(name = "distance_km")
    private Double distanceKm;


    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;


    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;
}