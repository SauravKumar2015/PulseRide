package com.pulseride.ride.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.pulseride.ride.dto.RideRequest;
import com.pulseride.ride.dto.RideResponse;

@Service
public class RideService {
    private final ConcurrentMap<String, RideRecord> rides = new ConcurrentHashMap<>();
    public RideResponse create(String passengerId, RideRequest request) { RideRecord ride = new RideRecord(passengerId, request); rides.put(ride.id, ride); return response(ride); }
    public RideResponse get(String passengerId, String id) { RideRecord ride = owned(passengerId, id); return response(ride); }
    public List<RideResponse> history(String passengerId) { return rides.values().stream().filter(r -> r.passengerId.equals(passengerId)).map(this::response).toList(); }
    public RideResponse cancel(String passengerId, String id) { RideRecord ride = owned(passengerId, id); transition(ride, RideStatus.CANCELLED); return response(ride); }
    private RideRecord owned(String passengerId, String id) { RideRecord ride = rides.get(id); if (ride == null) throw new IllegalArgumentException("Ride not found"); if (!ride.passengerId.equals(passengerId)) throw new AccessDeniedException("Ride does not belong to authenticated user"); return ride; }
    private void transition(RideRecord ride, RideStatus next) { if (ride.status == RideStatus.REQUESTED && next == RideStatus.CANCELLED) { ride.status = next; return; } throw new IllegalStateException("Invalid ride status transition"); }
    private RideResponse response(RideRecord r) { return new RideResponse(r.id, r.passengerId, r.request.pickupLatitude(), r.request.pickupLongitude(), r.request.dropoffLatitude(), r.request.dropoffLongitude(), r.status, r.createdAt); }
    private static final class RideRecord { final String id = UUID.randomUUID().toString(); final String passengerId; final RideRequest request; final Instant createdAt = Instant.now(); RideStatus status = RideStatus.REQUESTED; RideRecord(String p, RideRequest r) { passengerId = p; request = r; } }
}
