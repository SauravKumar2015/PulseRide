package com.pulseride.pricing.service;
import java.math.*; import java.util.List; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service; import com.pulseride.pricing.dto.*;
@Service public class PricingService {
 private final BigDecimal base; private final BigDecimal distance; private final BigDecimal time; private final BigDecimal fee; private final String currency;
 public PricingService(@Value("${pricing.base-fare:50.00}") BigDecimal base,@Value("${pricing.distance-rate:12.00}") BigDecimal distance,@Value("${pricing.time-rate:2.00}") BigDecimal time,@Value("${pricing.service-fee:5.00}") BigDecimal fee,@Value("${pricing.currency:INR}") String currency) { this.base=base;this.distance=distance;this.time=time;this.fee=fee;this.currency=currency; }
 public QuoteResponse quote(QuoteRequest r) { var d=r.distanceKm().multiply(distance); var t=r.durationMinutes().multiply(time); var total=base.add(d).add(t).add(fee).setScale(2,RoundingMode.HALF_UP); return new QuoteResponse(base,d,t,fee,total,currency); }
 public List<SurgeZoneResponse> zones() { return List.of(); }
}
