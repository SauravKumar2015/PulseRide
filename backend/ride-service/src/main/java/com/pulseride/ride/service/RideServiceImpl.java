package com.pulseride.ride.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseride.ride.dto.CancelRideRequest;
import com.pulseride.ride.dto.CreateRideRequest;
import com.pulseride.ride.dto.RideResponse;
import com.pulseride.ride.dto.RideStatusHistoryResponse;
import com.pulseride.ride.entity.Ride;
import com.pulseride.ride.entity.RideStatus;
import com.pulseride.ride.entity.RideStatusHistory;
import com.pulseride.ride.repository.RideRepository;
import com.pulseride.ride.repository.RideStatusHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;

    private final RideStatusHistoryRepository rideStatusHistoryRepository;

    @Override
    public RideResponse createRide(
            Long riderId,
            CreateRideRequest request) {

        Ride ride = new Ride();

        ride.setRiderId(riderId);
        ride.setStatus(RideStatus.REQUESTED);

        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());

        ride.setDropoffLatitude(request.getDropoffLatitude());
        ride.setDropoffLongitude(request.getDropoffLongitude());

        Ride savedRide = rideRepository.save(ride);

        saveStatusHistory(
                savedRide,
                riderId,
                RideStatus.REQUESTED,
                "Ride requested"
        );

        return mapToResponse(savedRide);
    }

    @Override
    @Transactional(readOnly = true)
    public RideResponse getRide(
            UUID rideId,
            Long userId) {

        Ride ride = getRideEntity(rideId);

        validateRideAccess(ride, userId);

        return mapToResponse(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> getRideHistory(Long riderId) {

        return rideRepository
                .findByRiderIdOrderByCreatedAtDesc(riderId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public RideResponse cancelRide(
            UUID rideId,
            Long riderId,
            CancelRideRequest request) {

        Ride ride = getRideEntity(rideId);

        if (!ride.getRiderId().equals(riderId)) {
            throw new SecurityException(
                    "You are not authorized to cancel this ride"
            );
        }

        if (!isCancellationAllowed(ride.getStatus())) {
            throw new IllegalStateException(
                    "Ride cannot be cancelled in status: "
                            + ride.getStatus()
            );
        }

        String reason = request != null
                ? request.getReason()
                : null;

        ride.setStatus(RideStatus.CANCELLED);

        Ride savedRide = rideRepository.save(ride);

        saveStatusHistory(
                savedRide,
                riderId,
                RideStatus.CANCELLED,
                reason
        );

        return mapToResponse(savedRide);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideStatusHistoryResponse> getRideStatusHistory(
            UUID rideId,
            Long userId) {

        Ride ride = getRideEntity(rideId);

        validateRideAccess(ride, userId);

        return rideStatusHistoryRepository
                .findByRideIdOrderByChangedAtAsc(rideId)
                .stream()
                .map(this::mapToHistoryResponse)
                .toList();
    }

    private Ride getRideEntity(UUID rideId) {

        return rideRepository
                .findById(rideId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ride not found: " + rideId
                        )
                );
    }

    private void validateRideAccess(
            Ride ride,
            Long userId) {

        boolean isRider =
                ride.getRiderId().equals(userId);

        boolean isDriver =
                ride.getDriverId() != null
                        && ride.getDriverId().equals(userId);

        if (!isRider && !isDriver) {
            throw new SecurityException(
                    "You are not authorized to access this ride"
            );
        }
    }

    private boolean isCancellationAllowed(
            RideStatus status) {

        return status == RideStatus.REQUESTED
                || status == RideStatus.SEARCHING_DRIVER
                || status == RideStatus.DRIVER_ASSIGNED
                || status == RideStatus.DRIVER_ARRIVING;
    }

    private void saveStatusHistory(
            Ride ride,
            Long changedBy,
            RideStatus status,
            String reason) {

        RideStatusHistory history =
                new RideStatusHistory();

        history.setRideId(ride.getRideId());
        history.setChangedBy(changedBy);
        history.setStatus(status);
        history.setReason(reason);

        rideStatusHistoryRepository.save(history);
    }

    private RideResponse mapToResponse(Ride ride) {

        return RideResponse.builder()
                .rideId(ride.getRideId())
                .riderId(ride.getRiderId())
                .driverId(ride.getDriverId())
                .status(ride.getStatus())
                .pickupLatitude(ride.getPickupLatitude())
                .pickupLongitude(ride.getPickupLongitude())
                .dropoffLatitude(ride.getDropoffLatitude())
                .dropoffLongitude(ride.getDropoffLongitude())
                .createdAt(ride.getCreatedAt())
                .updatedAt(ride.getUpdatedAt())
                .build();
    }

    private RideStatusHistoryResponse mapToHistoryResponse(
            RideStatusHistory history) {

        return RideStatusHistoryResponse.builder()
                .historyId(history.getHistoryId())
                .rideId(history.getRideId())
                .changedBy(history.getChangedBy())
                .status(history.getStatus())
                .reason(history.getReason())
                .changedAt(history.getChangedAt())
                .build();
    }
}