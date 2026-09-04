package com.pulseride.ride.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pulseride.ride.dto.CancelRideRequest;
import com.pulseride.ride.dto.CreateRideRequest;
import com.pulseride.ride.dto.RideResponse;
import com.pulseride.ride.entity.Ride;
import com.pulseride.ride.entity.RideStatus;
import com.pulseride.ride.repository.RideRepository;
import com.pulseride.ride.repository.RideStatusHistoryRepository;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RideStatusHistoryRepository
            rideStatusHistoryRepository;

    private RideServiceImpl rideService;

    private Long riderId;

    private Long otherUserId;

    private UUID rideId;

    @BeforeEach
    void setUp() {

        rideService = new RideServiceImpl(
                rideRepository,
                rideStatusHistoryRepository
        );

        riderId = 10L;
        otherUserId = 20L;
        rideId = UUID.randomUUID();
    }

    @Test
    void ownerCanCancelRequestedRide() {

        Ride ride = createRide(
                riderId,
                RideStatus.REQUESTED
        );

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CancelRideRequest request =
                new CancelRideRequest();

        request.setReason("Changed my mind");

        RideResponse response =
                rideService.cancelRide(
                        rideId,
                        riderId,
                        request
                );

        assertThat(response.getStatus())
                .isEqualTo(RideStatus.CANCELLED);
    }

    @Test
    void differentUserCannotAccessRide() {

        Ride ride = createRide(
                riderId,
                RideStatus.REQUESTED
        );

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(ride));

        assertThatThrownBy(() ->
                rideService.getRide(
                        rideId,
                        otherUserId
                )
        )
                .isInstanceOf(SecurityException.class)
                .hasMessage(
                        "You are not authorized to access this ride"
                );
    }

    @Test
    void differentUserCannotCancelRide() {

        Ride ride = createRide(
                riderId,
                RideStatus.REQUESTED
        );

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(ride));

        assertThatThrownBy(() ->
                rideService.cancelRide(
                        rideId,
                        otherUserId,
                        null
                )
        )
                .isInstanceOf(SecurityException.class)
                .hasMessage(
                        "You are not authorized to cancel this ride"
                );
    }

    @Test
    void completedRideCannotBeCancelled() {

        Ride ride = createRide(
                riderId,
                RideStatus.RIDE_COMPLETED
        );

        when(rideRepository.findById(rideId))
                .thenReturn(Optional.of(ride));

        assertThatThrownBy(() ->
                rideService.cancelRide(
                        rideId,
                        riderId,
                        null
                )
        )
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createRideStartsWithRequestedStatus() {

        CreateRideRequest request =
                new CreateRideRequest();

        Ride savedRide =
                createRide(
                        riderId,
                        RideStatus.REQUESTED
                );

        when(rideRepository.save(any(Ride.class)))
                .thenReturn(savedRide);

        RideResponse response =
                rideService.createRide(
                        riderId,
                        request
                );

        assertThat(response.getRiderId())
                .isEqualTo(riderId);

        assertThat(response.getStatus())
                .isEqualTo(RideStatus.REQUESTED);
    }

    private Ride createRide(
            Long riderId,
            RideStatus status) {

        Ride ride = new Ride();

        ride.setRideId(rideId);
        ride.setRiderId(riderId);
        ride.setStatus(status);

        return ride;
    }
}