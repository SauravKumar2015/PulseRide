package com.pulseride.payment.service;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.pulseride.payment.gateway.StripeGateway;
class PaymentServiceTest { @Test void orderIsIdempotent() { var s=new PaymentService("secret",new StripeGateway()); var req=new PaymentService.OrderRequest(new BigDecimal("10.00"),"INR"); var a=s.order("u1","k1",req); var b=s.order("u1","k1",req); assertThat(b.id()).isEqualTo(a.id()); } }
