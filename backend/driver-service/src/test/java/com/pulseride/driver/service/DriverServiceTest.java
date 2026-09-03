package com.pulseride.driver.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.pulseride.driver.repository.DriverRepository;
import com.pulseride.driver.dto.LocationRequest;
import com.pulseride.driver.exception.DriverNotFoundException;

import java.util.Optional;

class DriverServiceTest {
    @Test
    void rejectsInvalidTransition() {
        DriverRepository repository = mock(DriverRepository.class);
        when(repository.findByUserId("d1")).thenReturn(Optional.of(new com.pulseride.driver.entity.Driver("d1", "d1")));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        DriverService service = new DriverService(repository);
        service.createProfile("d1");

        assertThatThrownBy(() -> service.updateStatus("d1", DriverStatus.BUSY)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updatesOnlyTheAuthenticatedDriverProfile() {
        DriverRepository repository = mock(DriverRepository.class);
        when(repository.findByUserId("driver-1")).thenReturn(Optional.of(new com.pulseride.driver.entity.Driver("driver-1", "driver-1")));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        DriverService service = new DriverService(repository);
        service.createProfile("driver-1");

        var response = service.updateLocation("driver-1",
                new LocationRequest(new BigDecimal("25.5941"), new BigDecimal("85.1376")));

        assertThat(response.userId()).isEqualTo("driver-1");
        assertThat(response.latitude()).isEqualByComparingTo("25.5941");
        assertThat(response.longitude()).isEqualByComparingTo("85.1376");
        assertThatThrownBy(() -> service.get("driver-2")).isInstanceOf(DriverNotFoundException.class);
    }

    @Test
    void allowsExpectedAvailabilityTransitions() {
        DriverRepository repository = mock(DriverRepository.class);
        when(repository.findByUserId("driver-1")).thenReturn(Optional.of(new com.pulseride.driver.entity.Driver("driver-1", "driver-1")));
        when(repository.findByUserId("driver-2")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        DriverService service = new DriverService(repository);
        service.createProfile("driver-1");

        service.updateStatus("driver-1", DriverStatus.AVAILABLE);
        var response = service.updateStatus("driver-1", DriverStatus.BUSY);

        assertThat(response.status()).isEqualTo(DriverStatus.BUSY);
    }
}
