package com.pulseride.ride.service;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.pulseride.ride.dto.RideRequest;
class RideServiceTest { @Test void ownerCanCancelRequestedRide() { var s=new RideService(); var r=s.create("p1",new RideRequest(1d,2d,3d,4d)); assertThat(s.cancel("p1",r.id()).status()).isEqualTo(RideStatus.CANCELLED); } @Test void differentOwnerIsRejected() { var s=new RideService(); var r=s.create("p1",new RideRequest(1d,2d,3d,4d)); assertThatThrownBy(()->s.get("p2",r.id())).isInstanceOf(org.springframework.security.access.AccessDeniedException.class); } }
