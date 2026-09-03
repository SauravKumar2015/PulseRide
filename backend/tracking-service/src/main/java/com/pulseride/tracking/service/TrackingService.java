package com.pulseride.tracking.service;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;
import com.pulseride.tracking.dto.*;
@Service public class TrackingService {
 private final ConcurrentMap<String, LocationResponse> latest = new ConcurrentHashMap<>();
 public LocationResponse update(String driverId, LocationRequest request) { var result = new LocationResponse(driverId, request.latitude(), request.longitude(), Instant.now()); latest.put(driverId, result); return result; }
 public LocationResponse get(String driverId) { return latest.get(driverId); }
}
