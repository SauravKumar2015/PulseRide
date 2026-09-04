package com.pulseride.ride.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseride.ride.dto.request.CancelRideRequest;
import com.pulseride.ride.dto.request.CreateRideRequest;
import com.pulseride.ride.dto.response.RideResponse;
import com.pulseride.ride.dto.response.RideStatusHistoryResponse;
import com.pulseride.ride.entity.Ride;
import com.pulseride.ride.entity.RideStatus;
import com.pulseride.ride.entity.RideStatusHistory;
import com.pulseride.ride.repository.RideRepository;
import com.pulseride.ride.repository.RideStatusHistoryRepository;

@Service
@Transactional
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideStatusHistoryRepository rideStatusHistoryRepository;

    public RideServiceImpl(
            RideRepository rideRepository,
            RideStatusHistoryRepository rideStatusHistoryRepository) {

        this.rideRepository = rideRepository;
        this.rideStatusHistoryRepository =
                rideStatusHistoryRepository;
    }

    @Override
    public RideResponse createRide(
            UUID riderId,
            CreateRideRequest request) {

        Ride ride = new Ride();

        ride.setRiderId(riderId);
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setDropoffLatitude(request.getDropoffLatitude());
        ride.setDropoffLongitude(request.getDropoffLongitude());
        ride.setStatus(RideStatus.REQUESTED);

        Ride savedRide = rideRepository.save(ride);

        saveStatusHistory(
                savedRide,
                RideStatus.REQUESTED,
                riderId,
                null
        );

        return mapToResponse(savedRide);
    }

    @Override
    @Transactional(readOnly = true)
    public RideResponse getRide(
            UUID rideId,
            UUID userId) {

        Ride ride = getRideById(rideId);

        validateRideAccess(ride, userId);

        return mapToResponse(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> getRideHistory(
            UUID riderId) {

        return rideRepository
                .findByRiderIdOrderByCreatedAtDesc(riderId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public RideResponse cancelRide(
            UUID rideId,
            UUID riderId,
            CancelRideRequest request) {

        Ride ride = getRideById(rideId);

        validateRiderOwnership(ride, riderId);

        validateCancellation(ride);

        String reason = request != null
                ? request.getReason()
                : null;

        ride.setStatus(RideStatus.CANCELLED);

        Ride savedRide = rideRepository.save(ride);

        saveStatusHistory(
                savedRide,
                RideStatus.CANCELLED,
                riderId,
                reason
        );

        return mapToResponse(savedRide);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideStatusHistoryResponse> getRideStatusHistory(
            UUID rideId,
            UUID userId) {

        Ride ride = getRideById(rideId);

        validateRideAccess(ride, userId);

        return rideStatusHistoryRepository
                .findByRideIdOrderByChangedAtAsc(rideId)
                .stream()
                .map(this::mapToHistoryResponse)
                .toList();
    }

    private Ride getRideById(UUID rideId) {

        return rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ride not found: " + rideId
                        )
                );
    }

    private void validateRideAccess(
            Ride ride,
            UUID userId) {

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

    private void validateRiderOwnership(
            Ride ride,
            UUID riderId) {

        if (!ride.getRiderId().equals(riderId)) {
            throw new SecurityException(
                    "You are not authorized to cancel this ride"
            );
        }
    }

    private void validateCancellation(Ride ride) {

        RideStatus status = ride.getStatus();

        if (status != RideStatus.REQUESTED
                && status != RideStatus.SEARCHING_DRIVER
                && status != RideStatus.DRIVER_ASSIGNED
                && status != RideStatus.DRIVER_ARRIVING) {

            throw new IllegalStateException(
                    "Ride cannot be cancelled in status: "
                            + status
            );
        }
    }

    private void saveStatusHistory(
            Ride ride,
            RideStatus status,
            UUID changedBy,
            String reason) {

        RideStatusHistory history =
                new RideStatusHistory();

        history.setRideId(ride.getRideId());
        history.setStatus(status);
        history.setChangedBy(changedBy);
        history.setReason(reason);

        rideStatusHistoryRepository.save(history);
    }

    private RideResponse mapToResponse(Ride ride) {

        return new RideResponse(
                ride.getRideId(),
                ride.getRiderId(),
                ride.getDriverId(),
                ride.getStatus(),
                ride.getPickupLatitude(),
                ride.getPickupLongitude(),
                ride.getDropoffLatitude(),
                ride.getDropoffLongitude(),
                ride.getCreatedAt(),
                ride.getUpdatedAt()
        );
    }

    private RideStatusHistoryResponse mapToHistoryResponse(
            RideStatusHistory history) {

        return new RideStatusHistoryResponse(
                history.getHistoryId(),
                history.getRideId(),
                history.getStatus(),
                history.getChangedAt(),
                history.getChangedBy(),
                history.getReason()
        );
    }
}