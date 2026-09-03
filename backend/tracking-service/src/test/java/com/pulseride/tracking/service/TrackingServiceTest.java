package com.pulseride.tracking.service;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import com.pulseride.tracking.dto.LocationRequest;
class TrackingServiceTest { @Test void latestLocationIsReplacedPerDriver() { var s=new TrackingService(); s.update("d1",new LocationRequest(1d,2d)); assertThat(s.get("d1").latitude()).isEqualTo(1d); } }
