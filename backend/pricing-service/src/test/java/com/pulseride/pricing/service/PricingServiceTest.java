package com.pulseride.pricing.service;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.pulseride.pricing.dto.QuoteRequest;
class PricingServiceTest { @Test void calculatesMoneyWithDecimalArithmetic() { var s=new PricingService(new BigDecimal("50"),new BigDecimal("12"),new BigDecimal("2"),new BigDecimal("5"),"INR"); assertThat(s.quote(new QuoteRequest(new BigDecimal("2"),new BigDecimal("10"))).total()).isEqualByComparingTo("99.00"); } }
